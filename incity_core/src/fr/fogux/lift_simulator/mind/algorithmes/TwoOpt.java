package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.function.Supplier;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.ascenseurs.AscPlanning;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public class TwoOpt<T extends Comparable<T>> extends BestInstert<T>
{

    public TwoOpt(final OutputProvider phys, final ConfigSimu c, final SimulationStatCreator<T> statCreator)
    {
        super(phys, c, statCreator);
    }

    /**
     * remplace la méthode appelExterieur de BestInsert
     */
    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        shadowMode(); // change l'interface physique pour que les simulations de test n'affectent pas la "réalité" mais de nouvelles simulations 
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
                    m.forEachAsc(a -> a.rallBack());// remet les ascenseurs dans l'état initial pour préparer la prochaine simulation
                }
                final Simulation newS = new Simulation(originalOutput.simu,autoInstantiator,false);
                newS.initPrgmAndResume();
                final T result = statCreator.produceStat(newS);
                if(current.compareTo(result) > 0)
                {
                	// l'algorithme a trouvé une transposition qui convient
                    current = result;
                    return true;// va interrompre les transpositions pour pouvoir reprendre la recherche du début
                }
                return false;
            }
        };

        try
        {
            while(improve(tacheUntilTrue)); // tant qu'on trouve une transposition qui améliore la solution, on continue
        }
        catch(final Throwable ex)
        {
            System.out.println("TWOOPT exception heure erreur " + originalOutput.simu.getTime() +  " type " + ex.getMessage());
            ex.printStackTrace();
            throw new SimulateurException("pb transposition " + ex.getMessage());
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


