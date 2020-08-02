package fr.fogux.lift_simulator.stats;

public class StandardSimulationStat implements SimulationStat
{
	public final int nbPersTransportees;
	public final long maxTravelTime;
	public final long totalTravelTime;
	
	public StandardSimulationStat(int nbPersTransportees, long maxTravelTime, long totalTravelTime) 
	{
		super();
		this.nbPersTransportees = nbPersTransportees;
		this.maxTravelTime = maxTravelTime;
		this.totalTravelTime = totalTravelTime;
	}
}
