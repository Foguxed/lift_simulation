package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.function.Supplier;

public class MinProcess<T extends Comparable<T>> implements Supplier<Boolean>
{

    protected final Supplier<T> evaluateur;
    protected T min;

    public MinProcess(final Supplier<T> evaluateur, final T initialValue)
    {
        this.evaluateur = evaluateur;
        this.min = initialValue;
    }

    @Override
    public Boolean get()
    {
        final T v = evaluateur.get();
        if(v.compareTo(min) < 0)
        {
            min = v;
        }
        return false;
    }

    public T getMin()
    {
        return min;
    }
}
