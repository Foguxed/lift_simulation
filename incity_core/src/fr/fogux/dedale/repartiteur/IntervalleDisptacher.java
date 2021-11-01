package fr.fogux.dedale.repartiteur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fr.fogux.dedale.function.Utils;

public class IntervalleDisptacher<T extends Intervalle>
{
    protected final double[] stacksMins;
    protected final List<Set<T>> intervalleStacks = new ArrayList<>();

    public IntervalleDisptacher(final List<T> intervallesInput)
    {
        final List<Double> stackMinsList = new ArrayList<>();
        if(!intervallesInput.isEmpty())
        {
            intervallesInput.sort
            (
                new Comparator<Intervalle>()
                {
                    @Override
                    public int compare(final Intervalle o1, final Intervalle o2)
                    {
                        return Double.compare(o1.min,o2.min);
                    }
                }
                );
            final List<T> intervallesParMax = new ArrayList<>(intervallesInput);
            intervallesParMax.sort
            (
                new Comparator<Intervalle>()
                {
                    @Override
                    public int compare(final Intervalle o1, final Intervalle o2)
                    {
                        return Double.compare(o1.max,o2.max);
                    }
                }
                );

            final Collection<T> currentIntervalles = new LinkedHashSet<>();
            final Iterator<T> iteratorParMin = intervallesInput.iterator();
            final Iterator<T> iteratorParMax = intervallesParMax.iterator();
            double breakDouble;
            T currentMin = iteratorParMin.next();
            T currentMax = iteratorParMax.next();
            boolean noMoreMins = false;
            while(iteratorParMax.hasNext())
            {
                if(currentMin.min < currentMax.max && !noMoreMins)
                {
                    breakDouble = currentMin.min;
                }
                else
                {
                    breakDouble = currentMax.max;
                }
                while(currentMin.min == breakDouble)
                {
                    currentIntervalles.add(currentMin);
                    if(iteratorParMin.hasNext())
                    {
                        currentMin = iteratorParMin.next();
                    }
                    else
                    {
                        noMoreMins = true;
                        break;
                    }
                }
                while(currentMax.max == breakDouble)
                {
                    currentIntervalles.remove(currentMax);
                    if(iteratorParMax.hasNext())
                    {
                        currentMax = iteratorParMax.next();
                    }
                    else break;
                }
                intervalleStacks.add(new HashSet<>(currentIntervalles));
                stackMinsList.add(breakDouble);
            }
        }
        stacksMins = new double[stackMinsList.size()];
        for(int i = 0 ; i < stackMinsList.size(); i ++)
        {
            stacksMins[i] = stackMinsList.get(i);
        }
    }
    /**
     *
     * @param x
     * @return les intervalles auxquels appartient x
     */

    public Set<T> getCollectionAt(final double x)
    {
        return intervalleStacks.get(Utils.rechercheDichotomiqueBornee(stacksMins, x));
    }

    @Override
    public String toString()
    {
        return "mins: " + stacksMins + " intervalles " + intervalleStacks.toString();
    }
}
