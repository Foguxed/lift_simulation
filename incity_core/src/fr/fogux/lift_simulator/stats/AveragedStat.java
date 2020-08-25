package fr.fogux.lift_simulator.stats;

import java.util.Collection;

public class AveragedStat implements SimulationStat
{
    public final LongStats resultat;
    public final long averageCompletionTime;

    public AveragedStat(final Collection<StandardSimulationStat> stats)
    {
        final LongStatMaker accumulateur = new LongStatMaker();
        long totalCompletionTime = 0;
        for(final StandardSimulationStat s : stats)
        {
            accumulateur.addAllStats(s.persTempsTrajet);
            totalCompletionTime += s.completionTime;
        }
        averageCompletionTime = totalCompletionTime/stats.size();
        resultat = accumulateur.produceLongStats();
    }


    public String toString(final String separator)
    {
        return averageCompletionTime + separator + resultat.toString(separator);
    }
}
