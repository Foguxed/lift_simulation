package fr.fogux.lift_simulator.batchs.core;

import fr.fogux.lift_simulator.Simulation;

public interface SimulationStatCreator<S extends Object>
{
	public abstract S produceStat(Simulation terminatedSimulation);
}
