package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.TreeExplorerSInstantiator;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.option.GhostChoix;
import fr.fogux.lift_simulator.mind.option.NoeudChoix;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.Arbre;
import fr.fogux.lift_simulator.utils.BOcamlList;
import fr.fogux.lift_simulator.utils.OcamlList;

public class TreeExplorer<P extends FewUpdatePool,A extends AscIndepIteratif<P>> extends PoolFillerAlgo<P,A>
{
    protected Arbre<NoeudChoix<?,A>> arbre;// trié du choix le plus tardif au choix le plus ancien
    protected OcamlList<Choix<?,A>> commande; // trié de la premiere commande a effectuer à la derniere
    public BOcamlList<Choix<?,A>> buffer = new BOcamlList<>();
    public Consumer<OptionSimu> outputStruct;
    public final ShadowIndepAscInstantiator<A> inst;
    public final OptionSimu optSimu;
    protected Collection<Choix<?,A>> currentSplit;

    public TreeExplorer(final Arbre<NoeudChoix<?,A>> choix , final Consumer<OptionSimu> output, final OcamlList<Choix<?,A>> newcommande, final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> inst,final List<IdAscPersPool> initialPools)
    {
        super(phys,c,inst,TreeExplorer.fromPoolList(initialPools,inst));
        this.arbre = choix;
        this.commande = newcommande;
        this.outputStruct = output;
        this.inst = inst;
        this.optSimu = new OptionSimu(phys.out().simu);
        montees.stream().forEach(m -> m.forEachAsc(a -> a.registerAlgo(this)));
    }
    
    public TreeExplorer(final TreeExplorer<P,A> shadowed,final OcamlList<Choix<?,A>> newcommande, final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> inst)
    {
        super(shadowed,phys,c, inst,TreeExplorer.fromSPoolList(shadowed.pools,inst));
        this.arbre = shadowed.arbre;
        this.commande = newcommande;
        this.outputStruct = shadowed.outputStruct;
        this.inst = inst;
        this.optSimu = new OptionSimu(phys.out().simu);
        montees.stream().forEach(m -> m.forEachAsc(a -> a.registerAlgo(this)));
    }

    public TreeExplorerSInstantiator<P,A> shadow(final OcamlList<Choix<?,A>> newcommande)
    {
        return new TreeExplorerSInstantiator<>(this, newcommande, inst);
    }

    public Arbre<NoeudChoix<?,A>> getNoeud()
    {
        return arbre;
    }

    public OcamlList<? extends Choix> getCommande()
    {
        return commande;
    }

    public void algoDonneUnSeulChoix(final Choix c, final AscIndepIteratif<P> asc)
    {
        final Choix<?,A> c2 = c;
        c2.apply((A)asc);
        bufferChoix(c);
    }

    public void bufferChoix(final Choix c)
    {
        buffer = buffer.add(c);
    }

    public Choix<?,A> pollNextCommande(final AscIndepIteratif<P> asc)
    {
        final Choix<?,A> c = commande.getHead();
        bufferChoix(c);
        c.apply((A)asc);
        commande = commande.getQueue();
        return c;
    }

    public void setCurrentSplit(final Collection<Choix<?,A>> split)
    {
        this.currentSplit = split;
    }

    public void addAndPlayPossibilites()
    {
        final Stream<Choix<?,A>> st = currentSplit.stream();
        final Choix<?,A> first = st.findFirst().get();
        final BOcamlList<Choix<?,A>> b = BOcamlList.map(buffer, c -> new GhostChoix<>(c));
        currentSplit.stream().skip(1).forEach(
            c ->
            {
                final OcamlList<Choix<?,A>> newcommande = b.add(c).reverse();
                final Simulation s = new Simulation(out().simu,shadow(newcommande), true);// attention ne contiendra pas les arrivees de personnes futures
                final TreeExplorer<?, ?> alg = (TreeExplorer<?, ?>)s.getPrgm();
                final OptionSimu newOptionSimu = alg.optSimu;
                s.resumeWithoutInit();// va jusqu'à une pause
                newOptionSimu.simulationUpdated();
                outputStruct.accept(newOptionSimu);
            }
            );
        this.commande = b.add(first).reverse();
        this.resetBuffer();
        out().simu.resumeWithoutInit();
        this.optSimu.simulationUpdated();
    }

    public boolean hasNextCommande()
    {
        return !commande.isEmpty();
    }

    @Override
    public void ping()
    {
        getAsc(out().getPingStoredData()).ping();
        if(!out().simu.interrupted())
        {
            unBuffer();
        }
        else
        {
            commande = buffer.reverse();
            resetBuffer();
        }
    }



    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }


    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        final List<Integer> r = super.listeInvites(idASc, places_disponibles, niveau);
        if(!out().simu.interrupted())
        {
            unBuffer();
        }
        else
        {
            commande = buffer.reverse();
            resetBuffer();
        }
        return r;
    }
    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {
        super.finDeTransfertDePersonnes(idAscenseur, niveau);
        if(!out().simu.interrupted())
        {
            unBuffer();
        }
        else
        {
            commande = buffer.reverse();
            resetBuffer();
        }
    }

    private void resetBuffer()
    {
        buffer = new BOcamlList<>();
    }

    private void unBuffer()
    {
        BOcamlList<Choix<?,A>> b = buffer.reverse();
        buffer = new BOcamlList<>();
        while(!b.isEmpty())
        {
            final Choix<?,A> c = b.getHead();
            arbre = arbre.add(new NoeudChoix<>(c, this.optSimu));// on garde un représentant de la branche
            b = b.getQueue();
        }
    }

    @Override
    protected int algInit()
    {
        return -1;
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        throw new SimulateurException("impossible dans le TreeExplorer, puisque la simulation doit être shadowed");
    }

    public static <P extends FewUpdatePool,A extends AscIndepIteratif<P>> List<P> fromPoolList(final List<IdAscPersPool> list, final ShadowIndepAscInstantiator<A> inst)
    {
        final List<P> r = new ArrayList<>(list.size());
        for(final IdAscPersPool p : list)
        {
            r.add((P)inst.newPoolInstance(p));
        }
        return r;
    }

    public static <P extends FewUpdatePool,A extends AscIndepIteratif<P>> List<P> fromSPoolList(final List<P> list,final ShadowIndepAscInstantiator<A> inst)
    {
        final List<P> r = new ArrayList<>(list.size());
        for(final P p : list)
        {
            r.add((P)inst.poolShadow(p));
        }
        return r;
    }

    public void printDebug(final InterfacePhysique debugOutput)
    {
        montees.stream().forEach(m -> m.ascenseurs.stream().forEach(a -> a.printDebug(debugOutput)));
    }

    @Override
    public String toString()
    {
        return  " buffer: " + buffer + " commande: " + commande + " arbre " + arbre.toString(4) + " " + super.toString();
    }
}
