package fr.fogux.lift_simulator.mind.planifiers;

import java.util.function.Supplier;

public class FirstLeastProcess<T extends Comparable<T>> implements Supplier<Boolean>
{

    protected final Supplier<T> evaluateur;
    protected T currentBest;

    public FirstLeastProcess(final Supplier<T> evaluateur, final T initialValue)
    {
        this.evaluateur = evaluateur;
        this.currentBest = initialValue;
    }

    @Override
    public Boolean get()
    {
        final T v = evaluateur.get();
        if(v.compareTo(currentBest) < 0)
        {
            return true;
        }
        return false;
    }

    public T getCurrentBest()
    {
        return currentBest;
    }
}
