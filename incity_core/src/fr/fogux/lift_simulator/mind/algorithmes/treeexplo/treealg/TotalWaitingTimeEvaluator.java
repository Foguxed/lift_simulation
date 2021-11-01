package fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.SimuEvaluator;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class TotalWaitingTimeEvaluator implements SimuEvaluator<Integer>
{

    @Override
    public Integer evaluate(final Simulation s, final int time)
    {
        int i = 0;
        final long t = time;
        for(final PersonneSimu p :s.getPersonneList())
        {
            if(p!=null)
            {
                i += p.getTempsTrajet(t);
            }
        }
        return i;
    }

    @Override
    public Integer evaluateTerminated(final Simulation s)
    {
        if(!s.completed())
        {
            return Integer.MAX_VALUE;
        }
        int i = 0;
        for(final PersonneSimu p :s.getPersonneList())
        {
            if(p!=null)
            {

                i += p.getTempsTrajet();
            }
        }
        return i;
    }


    @Override
    public Integer evaluateAbsolute(final Simulation s, final int time)
    {
        int i = 0;
        final long t = time;
        for(final PersonneSimu p :s.getPersonneList())
        {
            if(p != null)
            {
                i += p.getTempsTrajet(t);
            }
        }
        return i - (int)(0.5*(s.getTime() - time));
    }

}
