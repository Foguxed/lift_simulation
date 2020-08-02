package fr.fogux.lift_simulator.batchs.core;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.stats.SimulationStat;

public interface SimulationStatCreator<S extends Object>
{
	public abstract S produceStat(Simulation terminatedSimulation);
}
