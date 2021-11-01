package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.algorithmes.BestInstert;

public abstract class PlanningOperation<T extends Comparable<T>> implements Comparable<T>
{
    public T resultat;

    public PlanningOperation(final T resultat)
    {
        this.resultat = resultat;
    }

    @Override
    public int compareTo(final T o)
    {
        return resultat.compareTo(o);
    }

    public abstract void apply(BestInstert<T> p);
}
