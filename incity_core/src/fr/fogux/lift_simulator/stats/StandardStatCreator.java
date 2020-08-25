package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class StandardStatCreator implements SimulationStatCreator<StandardSimulationStat>
{

    @Override
    public StandardSimulationStat produceStat(final Simulation terminatedSimulation)
    {
        final StandardPersStatAccumulator acc = new StandardPersStatAccumulator();
        for(final PersonneSimu p : terminatedSimulation.getPersonneList())
        {
            acc.accumulateStat(p);
        }
        return new StandardSimulationStat(acc.transportTime, terminatedSimulation.getTime());
    }

}
