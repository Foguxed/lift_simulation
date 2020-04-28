package fr.fogux.lift_simulator.stats;

public interface StatAccumulator<E extends Object>
{
    void accumulateStat(E e);
}
