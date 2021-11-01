package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.algorithmes.BestInstert;

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
        throw new SimulateurException("NonOperation applied, peut être tous les scénarios ont amené à l'incomplétion des simulations, ou l'une des personnes ne peut pas être acheminée");
    }

}
