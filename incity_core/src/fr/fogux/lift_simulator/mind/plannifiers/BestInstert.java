package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.AlgoInstantiatorJustRef;
import fr.fogux.lift_simulator.mind.DynamicOutputAlgorithm;
import fr.fogux.lift_simulator.mind.independant.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.independant.IndepAscInstantiator;
import fr.fogux.lift_simulator.mind.independant.Montee;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.independant.VoisinAsc;
import fr.fogux.lift_simulator.mind.trajets.AlgoImmeuble;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class  BestInstert<T extends Comparable<T>> extends AlgoImmeuble<Montee<AscPlanning>> implements Supplier<Boolean>, DynamicOutputAlgorithm
{

    protected final SimulationStatCreator<T> statCreator;
    final AlgoInstantiator autoInstantiator;
    PlanningOperation<T> min = null;
    final InterfacePhysique originalOutput;
    protected int testCOunter;


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

    private static List<Montee<AscPlanning>> getMontees(final OutputProvider output,final ConfigSimu config, final IndepAscInstantiator instantiator)
    {
        final int[] repart = config.getRepartAscenseurs();
        final List<Montee<AscPlanning>> montees = new ArrayList<>(repart.length);
        for(int j = 0 ; j < repart.length; j ++)
        {
            montees.add(new Montee<>(output, config, j, repart[j], instantiator));
        }
        return montees;
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

    protected void insererAll(final AlgoPersonne p)
    {
        final EntreePers e = new EntreePers(p);
        final SortiePers s = new SortiePers(p);
        min = new NonOperation<>();
        try
        {
            //System.out.println("On essaye d'insert " + p);
            for(final Montee<AscPlanning> m : montees)
            {
                m.forEachAsc(a -> a.forEachFullInsertUntil(e, s, config().nbPersMaxAscenseur(),this));
            }
        }
        catch(final Throwable ex)
        {
            ex.printStackTrace();
            throw new SimulateurAcceptableException("pb bestInsert " + ex.getMessage());
        }
        min.apply(this);

    }
    /*
    protected void bestInsert(EntreePers e,SortiePers s, AscPlanning asc)
    {
        min = new NonOperation<>();
        try
        {
            asc.forEachFullInsertUntil(e, s, config().nbPersMaxAscenseur(),this));

        }
        catch(final Throwable ex)
        {
            ex.printStackTrace();
            throw new SimulateurAcceptableException("pb bestInsert " + ex.getMessage());
        }
        min.apply(this);
    }*/

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        final int nbOp = nbOp();
        shadowMode();
        testCOunter = 0;
        insererAll(new AlgoPersonne(idPersonne, niveau, destination));
        System.out.println("idPers " + idPersonne + " nbOp " + nbOp + " vraimentEffectuees " + testCOunter);
        repasserModeReelEtInit();
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

    public AscPlanning getAsc(final AscId a)
    {
        return montees.get(a.monteeId).ascenseurs.get(a.stackId);
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

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

    @Override
    public Boolean get()
    {
        for(final Montee<AscPlanning> m : montees)
        {
            m.forEachAsc(a -> a.rallBack());
        }
        //System.out.println("attention on teste " + getState());


        final Simulation newS = new Simulation(originalOutput.simu,autoInstantiator);
        //System.out.println("1la state de simu newSimu " + newS.getPersonneList());
        newS.initPrgmAndResume();
        testCOunter ++;
        //System.out.println("2la state de simu newSimu " + newS.getPersonneList());
        final T result = statCreator.produceStat(newS);
        //System.out.println("doit etre identique " + getState());
        if(min.compareTo(result) > 0)
        {
            min = new PlanningState<>(result, getState());
            //System.out.println("ON A PRIS " + getState());
        }
        return false;
    }

    @Override
    public void setOutput(final InterfacePhysique output)
    {
        //System.out.println("on change d'output " + output.hashCode());
        this.output.interfacePhys = output;
    }



}
