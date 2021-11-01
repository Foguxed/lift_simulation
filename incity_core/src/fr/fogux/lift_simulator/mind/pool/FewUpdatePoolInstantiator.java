package fr.fogux.lift_simulator.mind.pool;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;

public interface FewUpdatePoolInstantiator<P extends IdAscPersPool, F extends FewUpdatePool>
{
    F newPoolInstance(P innerPool);
    F poolShadow(F toShadow);
}
