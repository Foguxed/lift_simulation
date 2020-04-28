package fr.fogux.lift_simulator;

import java.util.List;

public class CompiledSimuResult
{
    protected final boolean failed; //a la fin tout le monde n'a pas été transporté
    protected final long totalTransportTime;
    protected final double nbPersonneTransportees;
    protected final int nbSimuResult;

    public CompiledSimuResult(final List<SimuResult> results)
    {
        nbSimuResult = results.size();
        int sumPers = 0;
        long sumTTime = 0;
        boolean failedTemp = false;
        for(final SimuResult r : results)
        {
            if(r.failed)
            {
                failedTemp = true;
            }
            sumPers += r.nbPersonneTransportees;
            sumTTime += r.totalTransportTime;
        }
        totalTransportTime = sumTTime;
        nbPersonneTransportees = sumPers;
        failed = failedTemp;
    }
}
