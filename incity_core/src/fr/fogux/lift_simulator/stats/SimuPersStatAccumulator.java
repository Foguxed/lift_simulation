package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.SimuResult;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class SimuPersStatAccumulator implements StatAccumulator<PersonneSimu>
{
    protected long totalTransportTime;
    protected long maxTransportTime;
    protected int nbPersonnes;
    protected boolean failed;

    public SimuPersStatAccumulator()
    {
        totalTransportTime = 0;
        nbPersonnes = 0;
        failed = false;
        maxTransportTime = 0;
    }

    @Override
    public void accumulateStat(final PersonneSimu e)
    {
        if(!e.livree())
        {
            failed = true;
        }
        else
        {
            nbPersonnes ++;
            final long waitTime = e.getTransportTime();
            if(waitTime > maxTransportTime)
            {
                maxTransportTime = waitTime;
            }
            totalTransportTime += waitTime;
        }
    }

    public SimuResult getResult()
    {
        return new SimuResult(failed, totalTransportTime, maxTransportTime, nbPersonnes);
    }
}
