package fr.fogux.lift_simulator.mind.plannifiers;

import fr.fogux.lift_simulator.exceptions.SimulateurException;

public class NonOperation<T extends Comparable<T>> extends PlanningOperation<T>
{

    public NonOperation()
    {
        super(null);
    }

    @Override
    public int compareTo(final T o)
    {
        return 1;
    }

    @Override
    public void apply(final BestInstert<T> p)
    {
        throw new SimulateurException("NonOperation applied");
    }

}
