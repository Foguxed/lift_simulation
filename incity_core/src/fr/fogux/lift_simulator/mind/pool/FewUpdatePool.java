package fr.fogux.lift_simulator.mind.pool;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;

public interface FewUpdatePool extends IdAscPersPool
{
    void flushUpdates();
}
