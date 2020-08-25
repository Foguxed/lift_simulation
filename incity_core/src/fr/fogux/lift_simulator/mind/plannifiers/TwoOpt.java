package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.function.Supplier;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.independant.Montee;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class TwoOpt<T extends Comparable<T>> extends BestInstert<T>
{

    public TwoOpt(final OutputProvider phys, final ConfigSimu c, final SimulationStatCreator<T> statCreator)
    {
        super(phys, c, statCreator);
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        //out().println("mais qui voila " + idPersonne + " niv " + niveau + " dest " + destination);
        shadowMode();
        insererAll(new AlgoPersonne(idPersonne, niveau, destination));
        faireTranspositions();
        repasserModeReelEtInit();
    }

    protected void faireTranspositions()
    {

        final T initial = min.resultat;
        final Supplier<Boolean> tacheUntilTrue = new Supplier<Boolean>()
        {
            protected T current = initial;
            @Override
            public Boolean get()
            {
                for(final Montee<AscPlanning> m : montees)
                {
                    m.forEachAsc(a -> a.rallBack());
                }
                final Simulation newS = new Simulation(originalOutput.simu,autoInstantiator);
                //System.out.println("simustate " + newS.getPersonneList());
                //System.out.println("state " + getState());
                newS.initPrgmAndResume();
                //System.out.println("termine sans encombre");
                final T result = statCreator.produceStat(newS);
                if(current.compareTo(result) > 0)
                {
                    current = result;
                    return true;
                }
                return false;
            }
        };

        try
        {
            while(improve(tacheUntilTrue));
        }
        catch(final Throwable ex)
        {
            ex.printStackTrace();
            throw new SimulateurAcceptableException("pb transposition " + ex.getMessage());
        }
    }

    protected boolean improve(final Supplier<Boolean> tacheUntilTrue)
    {
        for(int i = 0; i < montees.size(); i ++)
        {
            for(int j = 0; j < montees.get(i).ascenseurs.size(); j ++)
            {

                final AscPlanning a = montees.get(i).ascenseurs.get(j);

                final int[] c = a.current0();
                while(a.exists(c))
                {
                    if(a.forEachEchangeInterne(c, config.nbPersMaxAscenseur(), tacheUntilTrue))
                    {
                        return true;
                    }
                    int initial = j+1;
                    for(int k = i; k < montees.size(); k ++)
                    {
                        for(int l = initial; l < montees.get(k).ascenseurs.size(); l ++)
                        {
                            if(a.forEachEchangeExterne(c, config.nbPersMaxAscenseur(), montees.get(k).ascenseurs.get(l), tacheUntilTrue))
                            {
                                return true;
                            }
                        }
                        initial = 0;
                    }
                    a.next(c);
                }
            }
        }
        return false;
    }
}


