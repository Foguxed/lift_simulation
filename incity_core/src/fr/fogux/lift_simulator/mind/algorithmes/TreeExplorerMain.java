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

public class TreeExplorerMain<P extends FewUpdatePool,A extends AscIndepIteratif<P>> extends Algorithme /*extends PoolFillerAlgo<IdAscPersPool,ChoixAlgoAsc<IdAscPersPool>>*/ implements Consumer<OptionSimu>,CommandSupplier
{
    //protected PriorityQueue<OptionSimu> simusQueue = new PriorityQueue<>(OptionSimu.OPT_TIME_COMPARATOR);
    protected ArbreOptionSimuStruct<A> optSimuStruct;
    final ArbreOptSimuStructInstantiator structInst;


    public static final int structCapacity = 8000;

    public static final long maxForecastTime = 1000*60*3;


    protected final ShadowIndepAscInstantiator<A> inst;
    protected final PupetTreeExplorer<P,A> innerAlg;

    protected int nbPersonnesArrivees;

    protected List<IdAscPersPool> pools;

    public TreeExplorerMain(final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> instantiator, final ArbreOptSimuStructInstantiator structInst, final List<IdAscPersPool> pools)
    {
        /*super(phys, c,new ChoixAscInstantiator<>(),pools);
        montees.stream().forEach(m -> m.forEachAsc(a -> a.registerAlgo(this)));*/
        super(phys,c);
        this.nbPersonnesArrivees = 0;
        this.inst = instantiator;
        this.structInst = structInst;
        this.pools = pools;
        //System.out.println(" pools " + pools);
        innerAlg = new PupetTreeExplorer<>(new Arbre<>(), this, new BOcamlList<Choix<?,A>>(), output, c, instantiator, this.pools,true);
    }

    protected ArbreOptionSimuStruct<A> newStructInstance(final TreeExplorer<P,A> seed, final int capacity)
    {
        final ArbreOptionSimuStruct<A> struct = structInst.getStruct(capacity, maxForecastTime);
        seed.arbre = struct.getArbre();
        seed.outputStruct = struct;
        //System.out.println("initPremiereSimu");
        seed.out().simu.resumeWithoutInit();

        //System.out.println("fininitPremiereSimu");
        final OptionSimu opt = seed.optSimu;
        opt.simulationUpdated();
        if(seed.out().simu.interrupted())
        {
            struct.add(opt);
        }
        else
        {
            struct.registerTerminatedSimu(opt);
        }
        return struct;
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

        boolean reinit = false;
        if(optSimuStruct == null)
        {
            reinitStruct();
            reinit = true;
        }
        else
        {
            optSimuStruct.onPoolUpdated();
            if(optSimuStruct.isEmpty())
            {
                reinitStruct();
                reinit = true;
            }
        }
        if(!reinit)
        {
            final PersonneSimu p = out().simu.getLastPersonne();
            for(final OptionSimu os:optSimuStruct)
            {
                os.getSimulation().addShadowPersonne(p);
            }
        }
        //System.out.println(Utils.getTimeString(out().simu.getTime()) + " MainAppelExterieur");
        //System.out.println("MainAppelExterieur fin initStruct");
        nbPersonnesArrivees ++ ;

        int newStructCapacity = -1;
        
        if(((float)nbPersonnesArrivees)/ (float)(out().simu.getPersonnesNonLivrees().size()+1) > 0.05)
        {
            newStructCapacity = (int)(structCapacity*0.5);
            nbPersonnesArrivees = 0;
        }
        else
        {
            if(updatedPools.stream().anyMatch(p -> p.getAscs().stream().anyMatch(id -> !innerAlg.getAsc(id).occupe() )))
            {
                newStructCapacity = (int)(structCapacity * 0.05);
            }
        } //TODO remettre
        /*
        if(updatedPools.stream().anyMatch(p -> p.getAscs().stream().anyMatch(id -> !innerAlg.getAsc(id).occupe() )))
        {
            newStructCapacity = (int)(structCapacity * 0.05);
        }*/

        if(newStructCapacity> 0)
        {
            final Simulation newS = new Simulation(out().simu, getCurrentInstantiator(new BOcamlList<>()), true);
            final TreeExplorer<P,A> prgm = (TreeExplorer<P,A>)newS.getPrgm();
            prgm.arbre = null;
            registerPings(prgm);
            //System.out.println(" une nouvelle struct " + Utils.getTimeString(out().simu.getTime()) + " newS " + newS.hashCode());
            final ArbreOptionSimuStruct<A> newStruct = newStructInstance(prgm,newStructCapacity);
            newStruct.pushSimulations(time());
            final int time = (int)Math.min(newStruct.minSimuTime(), optSimuStruct.minSimuTime());
            final OptionSimu a = newStruct.getBestSimu(time);
            final OptionSimu b = optSimuStruct.getBestSimu(time);
            if(b == null || newStruct.aMeilleurQueB(a, b, time))
            {
                //out().println("on change de structure car arrivee de " + newPers);
                optSimuStruct = newStruct;
                newStruct.changeCapacity(structCapacity);
                innerAlg.arbre = optSimuStruct.getArbre();
                registerPings(innerAlg);
                //System.out.println(" on a ajoute les pings " + Utils.getTimeString(out().simu.getTime()));
                //System.out.println(out().simu.getGestio());
            }

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

    private void reinitStruct()
    {
        final Simulation newS = new Simulation(out().simu, getCurrentInstantiator(new BOcamlList<>()), true);
        this.optSimuStruct = newStructInstance((TreeExplorer<P,A>) newS.getPrgm(),structCapacity);
        optSimuStruct.pushSimulations(time());
    }

    public Choix<?,?> pollNextCommande(final AlgoIndependentAsc a)
    {
        optSimuStruct.pushSimulations(time());
        final Arbre arbre = optSimuStruct.getBestFirstChoix();

        optSimuStruct.keepOnlyMatchingFirstChoix(arbre);
        final Choix c = ((NoeudChoix<?, ?>)arbre.getHead()).choix;
        innerAlg.arbre = optSimuStruct.getArbre();

        c.apply(innerAlg.getAsc(a.getId()));
        innerAlg.flushPoolUpdates();
        return c;
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
        this.optSimuStruct.accept(t);
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {
        innerAlg.finDeTransfertDePersonnes(idAscenseur, niveau);
    }

    @Override
    public String toString()
    {
        return "innerAlg " + innerAlg + " struct " + optSimuStruct;
    }

}
