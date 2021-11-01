package fr.fogux.lift_simulator.utils;

import java.util.function.Consumer;

public class IntEnsemble
{
    final int[][] intervalles; //l'ensemble est l'union disjointe de ces intervalles

    public IntEnsemble(final int[][] intervalles)
    {
        this.intervalles = intervalles;
    }

    public IntEnsemble(final int min, final int maxExclu)
    {
        this(new int[][] {{min,maxExclu}});
    }

    public IntEnsemble(final int min1, final int maxExclu1,final int min2,final int maxExclu2)
    {
        this(new int[][] {{min1,maxExclu1},{min2,maxExclu2}});
    }

    public boolean appartient(final int i)
    {
        for (final int[] intervalle : intervalles)
        {
            if(intervalle[0] <= i && intervalle[1] > i)
            {
                return true;
            }
        }
        return false;
    }

    public void forEach(final Consumer<Integer> c)
    {
        for (final int[] intervalle : intervalles)
        {
            for(int k = intervalle[0]; k < intervalle[1]; k ++)
            {
                c.accept(k);
            }
        }
    }
}
