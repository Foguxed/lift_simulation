package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import java.util.Iterator;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.utils.Arbre;
import fr.fogux.lift_simulator.utils.IteratorAndThen;
import fr.fogux.lift_simulator.utils.ListedTreeMapIterator;
import fr.fogux.lift_simulator.utils.ListedTreemap;

/**
 * 
 * Classe utilisée par l'algorithme d'exploration d'arbre d'option pour stocker l'arbre et le front de feuilles actives
 */
public class OptSimuStructStat<A extends AscIndepIteratif, T extends Comparable<T>> extends ArbreOptionSimuStruct<AscIndepIteratif>
{

	
    protected final SimuEvaluator<T> simuEval;// fonction de coût de la simulation que l'on cherche à minimiser

    protected ComparableOptSimu<T> bestTerminatedSimu = null;


    public OptSimuStructStat(final int capacity, final long maxForecasttime,final SimuEvaluator<T> simuEval)
    {
        super(capacity, maxForecasttime);
        this.simuEval = simuEval;
    }

    @Override
    public void removeSimu(int nb)
    {
        final int time = (int)this.minSimuTime(); //temps auquel va avoir lieue la comparaison
        final ListedTreemap<T, OptionSimu> tm = new ListedTreemap<>();// sert de file de priorité
        for(final OptionSimu s : tMap)
        {
            tm.put(simuEval.evaluate(s.getSimulation(), time), s);
        }
        final Iterator<OptionSimu> iter = new ListedTreeMapIterator<>(tm, false); // iteration de la file de priorité dans le sens décroissant
        while(nb > 0)
        {
            remove(iter.next());// enleve les simulations qui ont le coût le plus élevé
            nb--;
        }
    }

    @Override
    public Arbre getBestFirstChoix()
    {
        final int time = (int)this.minSimuTime();
        return getFirstChoix(getBestSimu(time));
    }

    @Override
    public OptionSimu getBestSimu(final int time)
    {
        if(tMap.isEmpty())
        {
            return bestTerminatedSimu.simu;
        }
        final Iterator<OptionSimu> iter = tMap.iterator();
        OptionSimu minS = iter.next();
        T min = simuEval.evaluate(minS.getSimulation(), time);
        while(iter.hasNext())
        {
            final OptionSimu op = iter.next();

            final T val = simuEval.evaluate(op.getSimulation(), time);

            //System.out.println("decide "  +val.compareTo(min));
            if(val.compareTo(min) < 0)
            {
                min = val;
                minS = op;
            }
        }

        if(bestTerminatedSimu != null && aMeilleurQueB(bestTerminatedSimu.simu, minS, time))
        {
            return bestTerminatedSimu.simu;
        }
        return minS;
    }

    @Override
    public long minSimuTime()
    {
        if(tMap.isEmpty())
        {
            return bestTerminatedSimu.simu.getTime() + 30*60*1000;
        }
        return super.minSimuTime();
    }

    @Override
    public void keepOnlyMatchingFirstChoix(final Arbre opt)
    {
        if(bestTerminatedSimu!=null && getFirstChoix(bestTerminatedSimu.simu) != opt) // ordre important
        {
            bestTerminatedSimu.simu.getNoeudCorrespondant().stripBranch();
            bestTerminatedSimu = null;
            structSize --;
        }
        super.keepOnlyMatchingFirstChoix(opt);
    }

    @Override
    public boolean aMeilleurQueB(final OptionSimu a, final OptionSimu b, final int time)
    {
        //System.out.println("tranche " );
        return simuEval.evaluateAbsolute(a.getSimulation(),time).compareTo(simuEval.evaluateAbsolute(b.getSimulation(), time)) < 0;
    }

    @Override
    public void registerTerminatedSimu(final OptionSimu s)
    {
        final ComparableOptSimu<T> copt = new ComparableOptSimu<>(s, simuEval.evaluateTerminated(s.getSimulation()));
        if(bestTerminatedSimu == null)
        {
            structSize ++;
            bestTerminatedSimu = copt;
        }
        else
        {

            if(copt.compareTo(bestTerminatedSimu) < 0)
            {
                bestTerminatedSimu.simu.getNoeudCorrespondant().stripBranch();
                bestTerminatedSimu = copt;
            }
            else
            {
                if(toStripNext != null)
                {
                    toStripNext.stripBranch();
                }
                else
                {
                    structSize++;
                }
                toStripNext = s.getNoeudCorrespondant(); // risque de cut la branche avec la simulation qui lance ses enfants sinon
            }
        }
    }

    @Override
    public Iterator<OptionSimu> iterator()
    {
        if(this.bestTerminatedSimu != null)
        {
            return new IteratorAndThen<>(bestTerminatedSimu.simu, super.iterator());
        }
        else
        {
            return super.iterator();
        }
    }

    @Override
    public void onPoolUpdated()
    {
        if(toStripNext != null)
        {

            toStripNext.stripBranch();
            toStripNext = null;
            structSize --;
        }
        if(bestTerminatedSimu != null)
        {
            bestTerminatedSimu.simu.getNoeudCorrespondant().stripBranch();
            bestTerminatedSimu = null;
            structSize --;
        }
    }

}
