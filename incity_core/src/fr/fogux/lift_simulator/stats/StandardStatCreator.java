package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class StandardStatCreator implements SimulationStatCreator<StandardSimulationStat>
{

	@Override
	public StandardSimulationStat produceStat(Simulation terminatedSimulation) 
	{
		StandardPersStatAccumulator acc = new StandardPersStatAccumulator();
		for(PersonneSimu p : terminatedSimulation.getPersonneList())
		{
			acc.accumulateStat(p);
		}
		return acc.getResult();
	}

}
