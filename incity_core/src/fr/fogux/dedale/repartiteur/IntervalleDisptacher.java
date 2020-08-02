package fr.fogux.dedale.repartiteur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.fogux.dedale.function.Utils;

public class IntervalleDisptacher<T extends Intervalle>
{
    protected final double[] stacksMins;
    protected final List<Set<T>> intervalleStacks = new ArrayList<Set<T>>();
    
    public IntervalleDisptacher(List<T> intervallesInput) 
    {
        List<Double> stackMinsList = new ArrayList<Double>();
        if(!intervallesInput.isEmpty())
        {
            intervallesInput.sort
            (    
                new Comparator<Intervalle>()
                {
                    @Override
                    public int compare(Intervalle o1, Intervalle o2)
                    {
                        return Double.compare(o1.min,o2.min);
                    }
                }
            );
            List<T> intervallesParMax = new ArrayList<T>(intervallesInput);
            intervallesParMax.sort
            (    
                new Comparator<Intervalle>()
                {
                    @Override
                    public int compare(Intervalle o1, Intervalle o2)
                    {
                        return Double.compare(o1.max,o2.max);
                    }
                }
            );
            
            final Collection<T> currentIntervalles = new HashSet<T>();
            Iterator<T> iteratorParMin = intervallesInput.iterator();
            Iterator<T> iteratorParMax = intervallesParMax.iterator();
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
                intervalleStacks.add(new HashSet<T>(currentIntervalles));
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
    
    public Set<T> getCollectionAt(double x)
    {
        return intervalleStacks.get(Utils.rechercheDichotomiqueBornee(stacksMins, x));
    }
    
    public String toString()
    {
    	return "mins: " + stacksMins + " intervalles " + intervalleStacks.toString();
    }
}
