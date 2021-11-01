package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.AlgoInstantiatorJustRef;
import fr.fogux.lift_simulator.mind.DynamicOutputAlgorithm;
import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.AscPlanning;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.mind.planifiers.AlgoRequete;
import fr.fogux.lift_simulator.mind.planifiers.EntreePers;
import fr.fogux.lift_simulator.mind.planifiers.NonOperation;
import fr.fogux.lift_simulator.mind.planifiers.PlanningOperation;
import fr.fogux.lift_simulator.mind.planifiers.PlanningState;
import fr.fogux.lift_simulator.mind.planifiers.SortiePers;
import fr.fogux.lift_simulator.mind.trajets.AlgoImmeuble;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public class  BestInstert<T extends Comparable<T>> extends AlgoImmeuble<AscPlanning> implements Supplier<Boolean>, DynamicOutputAlgorithm
{

    protected final SimulationStatCreator<T> statCreator;
    final AlgoInstantiator autoInstantiator;
    PlanningOperation<T> min = null;
    final InterfacePhysique originalOutput;


    public BestInstert(final OutputProvider phys, final ConfigSimu c, final SimulationStatCreator<T> statCreator)
    {
        super(getMontees(phys,c,new IndepAscInstantiator()
        {

            @Override
            public AlgoIndependentAsc getNewInstance(final AscId id, final ConfigSimu config, final OutputProvider phys,
                final VoisinAsc ascPrecedent)
            {
                return new AscPlanning(id, config, phys, ascPrecedent);
            }
        }), phys, c);
        this.statCreator = statCreator;
        autoInstantiator = new AlgoInstantiatorJustRef(this);
        originalOutput = out();
    }

    @Override
    protected int algInit()
    {
        return -1;
    }

    @Override
    public void ping()
    {

    }
    /**
     * trouve la meilleure insertion de p
     * @param p personne à insérer
     */
    protected void insererAll(final AlgoPersonne p)
    {
        final EntreePers e = new EntreePers(p);
        final SortiePers s = new SortiePers(p);
        min = new NonOperation<>();
        
        try
        {
            for(final Montee<AscPlanning> m : montees)
            {

                m.forEachAsc(a ->
                {
                    a.forEachFullInsertUntil(e, s, config().nbPersMaxAscenseur(),this); // va modifier min si une meilleure insertion est trouvée
                    output.interfacePhys = originalOutput;// pour avoir les messages de debug
                });
            }
        }
        catch(final Throwable ex)
        {
            System.out.println("INSERT EXEPTION");
            ex.printStackTrace();
            throw ex;
        }
        min.apply(this);

    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        shadowMode(); // se prépare à effectuer des simulations de prévision (car les insertions sont effectuées en place: complexité en espace = 1 seule simulation)
        insererAll(new AlgoPersonne(idPersonne, niveau, destination)); // essaye toutes les insertions possibles de la personne
        repasserModeReelEtInit();// remet la bonne interface physique en sortie de l'algorithme, et envoie un ping() aux ascenseurs pour qu'ils se déplacent si besoin
    }

    protected int nbEscalesActives()
    {
        int nb = 0;
        for(final Montee<AscPlanning> m : montees)
        {
            for(final AscPlanning a : m.ascenseurs)
            {
                nb += a.reqs.size();
            }
        }
        return nb;
    }

    protected void shadowMode()
    {
        for(final Montee<AscPlanning> m : montees)
        {
            m.forEachAsc(a -> a.shadowMode());
        }
    }

    protected void repasserModeReelEtInit()
    {
        output.interfacePhys = originalOutput;
        for(final Montee<AscPlanning> m : montees)
        {
            for(final AscPlanning a : m.ascenseurs)
            {
                a.realMode();
            }
        }
        init();
    }

    @Override
    public AscPlanning getAsc(final AscId a)
    {
        return montees.get(a.monteeId).ascenseurs.get(a.stackId);
    }

    /**
     * Il s'agit de la méthode appellée à chauqe fois qu'une nouvelle insertion est testée, au moment où cette méthode est appellée,
     * les ascenseurs contiennent des listes d'instructions valides tels que l'un d'entre eux a inséré le nouvel arrivant dans son plan
     */
    @Override
    public Boolean get()
    {
        for(final Montee<AscPlanning> m : montees)
        {
            m.forEachAsc(a -> a.rallBack()); // on revient dans un état qui permet de lancer la simulation
        }
        final Simulation newS = new Simulation(originalOutput.simu,autoInstantiator,false);
        newS.initPrgmAndResume();
        final T result = statCreator.produceStat(newS);
        if(min.compareTo(result) > 0)
        {
            min = new PlanningState<>(result, getState()); // contient la nouvelle meilleure insertion trouvée
        }
        return false; // n'imterrompt pas la recherche de la meilleure insertion
    }
    @Override
    public void setOutput(final InterfacePhysique output)
    {
        this.output.interfacePhys = output;
    }
    
    public void applyState(final List<List<List<AlgoRequete>>> state)
    {
        for(int i = 0; i <  state.size(); i ++)
        {
            for(int j = 0; j < state.get(i).size(); j ++)
            {
                montees.get(i).ascenseurs.get(j).applyState(state.get(i).get(j));
            }
        }
    }

    public List<List<List<AlgoRequete>>> getState()
    {
        final List<List<List<AlgoRequete>>> retour = new ArrayList<>();
        for(int i = 0; i <  montees.size(); i ++)
        {
            retour.add(new ArrayList<>());
            for(int j = 0; j < montees.get(i).ascenseurs.size(); j ++)
            {
                retour.get(i).add(montees.get(i).ascenseurs.get(j).cloneState());
            }
        }
        return retour;
    }
    
    public void debugCheckState(final String msg)
    {

        for(final Montee<AscPlanning> m : montees)
        {
            for(final AscPlanning a : m.ascenseurs)
            {
                a.debugCheckState("",config.nbPersMaxAscenseur());
            }
        }
    }

    protected int nbOp()
    {
        int nbOp = 0;
        for(final Montee<AscPlanning> m : montees)
        {
            for(final AscPlanning a : m.ascenseurs)
            {
                nbOp += a.reqs.size()*a.reqs.size()/2;
            }
        }
        return nbOp;
    }
    
    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
    	
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {
    	
    }
}
