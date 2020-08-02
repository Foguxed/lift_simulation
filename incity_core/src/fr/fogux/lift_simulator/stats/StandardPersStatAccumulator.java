package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.population.PersonneSimu;

public class StandardPersStatAccumulator implements StatAccumulator<PersonneSimu>
{
    protected long totalTransportTime;
    protected long maxTransportTime;
    protected int nbPersonnes;

    public StandardPersStatAccumulator()
    {
        totalTransportTime = 0;
        nbPersonnes = 0;
        maxTransportTime = 0;
    }

    @Override
    public void accumulateStat(final PersonneSimu e)
    {
        nbPersonnes ++;
        final long waitTime = e.getTransportTime();
        if(waitTime > maxTransportTime)
        {
            maxTransportTime = waitTime;
        }
        totalTransportTime += waitTime;
    }

    public StandardSimulationStat getResult()
    {
        return new StandardSimulationStat(nbPersonnes, maxTransportTime,totalTransportTime);
    }
}
