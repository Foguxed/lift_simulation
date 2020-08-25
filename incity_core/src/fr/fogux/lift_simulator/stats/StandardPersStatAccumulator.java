package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.population.PersonneSimu;

public class StandardPersStatAccumulator implements StatAccumulator<PersonneSimu>
{
    public LongStatMaker transportTime;

    public StandardPersStatAccumulator()
    {
        transportTime = new LongStatMaker();
    }

    @Override
    public void accumulateStat(final PersonneSimu e)
    {
        transportTime.registerVal(e.getTempsTrajet());
    }
}
