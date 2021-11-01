package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.TreeExplorerSInstantiator;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.ArbreOptSimuStructInstantiator;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.ArbreOptionSimuStruct;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg.AscCycleOption;
import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.option.NoeudChoix;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.Arbre;
import fr.fogux.lift_simulator.utils.BOcamlList;
import fr.fogux.lift_simulator.utils.OcamlList;
import fr.fogux.lift_simulator.utils.Utils;

public class MonoExplorerMain<P extends FewUpdatePool,A extends AscIndepIteratif<P>> extends Algorithme/*extends PoolFillerAlgo<IdAscPersPool,ChoixAlgoAsc<IdAscPersPool>>*/ implements Consumer<OptionSimu>,CommandSupplier
{
    //protected PriorityQueue<OptionSimu> simusQueue = new PriorityQueue<>(OptionSimu.OPT_TIME_COMPARATOR);
    final ArbreOptSimuStructInstantiator structInst;


    protected final ShadowIndepAscInstantiator<A> inst;
    protected final PupetTreeExplorer<P,A> innerAlg;

    protected int nbPersonnesArrivees;

    protected List<IdAscPersPool> pools;

    public MonoExplorerMain(final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> instantiator, final ArbreOptSimuStructInstantiator structInst, final List<IdAscPersPool> pools)
    {
        /*super(phys, c,new ChoixAscInstantiator<>(),pools);
        montees.stream().forEach(m -> m.forEachAsc(a -> a.registerAlgo(this)));*/
        super(phys,c);
        this.nbPersonnesArrivees = 0;
        this.inst = instantiator;
        this.structInst = structInst;
        this.pools = pools;
        //System.out.println(" pools " + pools);
        innerAlg = new PupetTreeExplorer<>(new Arbre<>(), this, new BOcamlList<Choix<?,A>>(), output, c, instantiator, this.pools,false);
    }

    @Override
    public long init()
    {
        return -1;
    }

    @Override
    public void ping()
    {
        innerAlg.getAsc(out().getPingStoredData()).ping();
    }


    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        innerAlg.getAsc(idASc).noticeOuverture(niveau);
        return innerAlg.listeInvites(idASc, places_disponibles, niveau);
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        final AlgoPersonne newPers = new AlgoPersonne(idPersonne, niveau, destination);
        final List<IdAscPersPool> updatedPools = new ArrayList<>();
        //System.out.println(" newPers " + idPersonne);
        for(final IdAscPersPool pool : pools)
        {
            if(pool.couldAccept(newPers))
            {
                pool.addToPool(newPers);
                updatedPools.add(pool);
            }
        }
        if(updatedPools.stream().anyMatch(p -> p.getAscs().stream().anyMatch(id -> !innerAlg.getAsc(id).occupe() )))
        {
            registerPings(innerAlg);
        }

    }



    private void registerPings(final TreeExplorer<P,A> prgm)
    {
        final List<AscId> Ids = config().getTousAscId();
        Ids.stream().forEach(i -> prgm.out().registerPingData(0,i));
    }

    protected AlgoInstantiator getCurrentInstantiator(final OcamlList<Choix<?,A>> commande)
    {
        return new TreeExplorerSInstantiator<>(innerAlg, commande, inst);
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    private long time()
    {
        return out().simu.getTime();
    }

    public Choix<?,?> pollNextCommande(final AlgoIndependentAsc a)
    {
        throw new RuntimeException("jamais utilise");
    }

    private final void testDebug(final String t)
    {
        if(((AscCycleOption)innerAlg.getAsc(new AscId(0,0))).contenu.contient(15, 24, 1))
        {
            out().println(t + " attention personne id 3 dans asc 0 0 ");
        }
    }

    @Override
    public void accept(final OptionSimu t)
    {
    	throw new RuntimeException("jamais utilise");
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {
        innerAlg.finDeTransfertDePersonnes(idAscenseur, niveau);
    }

    @Override
    public String toString()
    {
        return "innerAlg " + innerAlg;
    }

}
