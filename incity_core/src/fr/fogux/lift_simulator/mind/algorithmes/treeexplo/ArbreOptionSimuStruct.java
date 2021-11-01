package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import java.util.Iterator;
import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.algorithmes.TreeExplorer;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.NoeudChoix;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.utils.Arbre;
import fr.fogux.lift_simulator.utils.ListedTreemap;

public abstract class ArbreOptionSimuStruct<A extends AscIndepIteratif> implements OptionSimuStruct, Iterable<OptionSimu>, Consumer<OptionSimu>
{
    protected Arbre<NoeudChoix<?,A>> arbre;
    public ListedTreemap<Integer, OptionSimu> tMap;

    protected int computationCount;
    public int structSize = 0;

    public int structCapacity;

    //public final int totalComputationCapacity;
    public final long maxForecastTime;

    protected Arbre<NoeudChoix<?,A>> toStripNext;

    public int ct = 0;

    public ArbreOptionSimuStruct(final int capacity, final long maxForecasttime)
    {
        this.structCapacity = capacity;
        this.maxForecastTime = maxForecasttime;
        this.arbre = new Arbre<>();
        this.tMap = new ListedTreemap<>();
    }



    public void changeCapacity(final int newCapacity)
    {
        this.structCapacity = newCapacity;
    }

    public Arbre<NoeudChoix<?,A>> getArbre()
    {
        return arbre;
    }

    protected Arbre<NoeudChoix<?, A>> getFirstChoix(final OptionSimu simu)
    {
        Arbre<NoeudChoix<?, A>> a = simu.getNoeudCorrespondant();
        boolean continuer = true;
        while(continuer)
        {
            final Arbre<NoeudChoix<?, A>> b = a.getQueue();
            if(b.estRacine())
            {
                continuer = false;
            }
            else
            {
                a = b;
            }
        }
        return a;
    }

    public OptionSimu pollLowestSimulationTime()
    {
        return tMap.pollAnyFirst();
    }

    @Override
    public void add(final OptionSimu opt)
    {
        tMap.put(opt.getTime(), opt);
        structSize ++;
    }

    protected void remove(final OptionSimu opt)
    {
        if(tMap.remove(opt.getTime(), opt))
        {
            structSize --;
        }
        opt.getNoeudCorrespondant().stripBranch();
    }


    private void checkRemove()
    {
        if(structSize >= structCapacity)
        {
            removeSimu((int) (structSize * 0.25f));
        }
    }

    @Override
    public void accept(final OptionSimu t)
    {
        if(t.getSimulation().interrupted())
        {
            add(t);
        }
        else
        {
            registerTerminatedSimu(t);
        }
    }

    public abstract void registerTerminatedSimu(OptionSimu s);

    @Override
    public void keepOnlyMatchingFirstChoix(final Arbre opt)
    {
        opt.popFromPere();
        for(final Arbre<NoeudChoix<?,A>> a : arbre) // parcourt tous les noeuds de restants de l'arbre 
        {
            if(!a.estRacine())
            {
                if(tMap.remove(a.getHead().simu.getTime(), a.getHead().simu))
                {
                    structSize --;
                }
            }
        }
        this.arbre = opt;
    }
    
    /**
     * avance toutes les simulations actives des feuilles jusqu'à timeNow + maxForecastTime.
     */
    public void pushSimulations(final long timeNow)
    {
        final long fin = timeNow + maxForecastTime;
        while( !tMap.isEmpty() && minSimuTime() < fin)
        {
            final OptionSimu s = pollLowestSimulationTime();// on prend la simulation la moins avancée dans temps
            final TreeExplorer<?,?> alg = (TreeExplorer<?,?>)s.getSimulation().getPrgm();
            structSize --;
            alg.addAndPlayPossibilites(); //crée les suivantes à partir de s
            accept(s);// remet s dans la structure (car s est l'un des enfants pour limiter le nombre d'instances crées et de copies)
            checkRemove();// vérifie si la taille de la structure n'est pas dépassée
        }
        if(toStripNext != null)
        {
            toStripNext.stripBranch();
            structSize --;
            toStripNext = null;
        }
    }

    public abstract void onPoolUpdated();

    @Override
    public long minSimuTime()
    {
        return tMap
            .
            getAnyFirst()
            .
            getTime()
            ;
    }

    public boolean isEmpty()
    {
        return structSize == 0;
    }

    @Override
    public Iterator<OptionSimu> iterator()
    {
        return tMap.iterator();
    }

}
