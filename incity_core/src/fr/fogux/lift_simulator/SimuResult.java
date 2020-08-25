package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.stats.StandardSimulationStat;

public class SimuResult
{
    public final boolean failed; //à la fin tout le monde n'a pas été transporté
    public final long totalTransportTime;
    public final long maxTransportTime;
    public final int nbPersonneTransportees;
    public final long completionTime;

    public SimuResult(final StandardSimulationStat stat)
    {
        this(false,stat.persTempsTrajet.getTotal(),stat.persTempsTrajet.getMaximum(),stat.persTempsTrajet.getCount(),stat.completionTime);
    }

    public SimuResult(final boolean failed, final long totalTransportTime, final long maxTransportTime, final int nbPersonneTransportees, final long completionTime)
    {
        this.failed = failed;
        this.totalTransportTime = totalTransportTime;
        this.nbPersonneTransportees = nbPersonneTransportees;
        this.maxTransportTime = maxTransportTime;
        this.completionTime = completionTime;
    }

    public void printFieldsIn(final DataTagCompound c)
    {
        c.setBoolean(TagNames.failed, failed);
        c.setLong(TagNames.totalTransportTime, totalTransportTime);
        c.setLong(TagNames.maxTransportTime, maxTransportTime);
        c.setInt(TagNames.nbPersonneTransportees, nbPersonneTransportees);
        c.setLong("completionTime", completionTime);
    }
}
