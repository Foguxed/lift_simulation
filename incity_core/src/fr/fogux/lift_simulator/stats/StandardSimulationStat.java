package fr.fogux.lift_simulator.stats;

public class StandardSimulationStat implements SimulationStat
{
    public final LongStatMaker persTempsTrajet;
    public final long completionTime;

    public StandardSimulationStat(final LongStatMaker persTempsTrajet, final long completionTime)
    {
        super();
        this.persTempsTrajet = persTempsTrajet;
        this.completionTime = completionTime;
    }
}
