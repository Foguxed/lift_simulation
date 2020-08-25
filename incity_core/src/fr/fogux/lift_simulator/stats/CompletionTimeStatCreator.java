package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class CompletionTimeStatCreator implements MinorableSimulStatCreator<Integer>
{

    @Override
    public Integer produceStat(final Simulation s)
    {
        return (int)s.getTime();
    }

    @Override
    public Integer getMinorant(final Simulation simu, final EtatMonoAsc etat, final ConfigSimu c)
    {
        return Integer.MIN_VALUE;
    }

}
