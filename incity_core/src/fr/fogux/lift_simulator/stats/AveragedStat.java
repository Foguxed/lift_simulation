package fr.fogux.lift_simulator.stats;

import java.util.Collection;
import java.util.List;

public class AveragedStat implements SimulationStat
{
	public final long totalTravelTime;
	public final int nbPersDeplacees;
	public final long maxTravelTime;
	
	public AveragedStat(Collection<StandardSimulationStat> stats)
	{
		long tTravelTime = 0;
		int nbPersDeplacees = 0;
		long maxWaitingTime = 0;
		for(StandardSimulationStat stat : stats)
		{
			tTravelTime += stat.totalTravelTime;
			nbPersDeplacees += stat.nbPersTransportees;
			if(stat.maxTravelTime > maxWaitingTime)
			{
				maxWaitingTime = stat.maxTravelTime;
			}
		}
		this.totalTravelTime = tTravelTime;
		this.nbPersDeplacees = nbPersDeplacees;
		this.maxTravelTime = maxWaitingTime;
	}
}
