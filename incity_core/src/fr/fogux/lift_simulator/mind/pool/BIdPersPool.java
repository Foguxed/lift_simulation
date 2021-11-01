package fr.fogux.lift_simulator.mind.pool;

import java.util.List;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class BIdPersPool implements IdAscPersPool
{
    protected final List<AscId> ascIds;

    public BIdPersPool(final List<AscId> ascIds)
    {
        this.ascIds = ascIds;
    }

    @Override
    public List<AscId> getAscs()
    {
        return ascIds;
    }

    @Override
    public String toString()
    {
        return " ascids " + ascIds;
    }
}
