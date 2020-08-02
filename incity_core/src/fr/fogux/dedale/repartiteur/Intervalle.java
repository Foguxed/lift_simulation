package fr.fogux.dedale.repartiteur;

import java.security.InvalidParameterException;

public class Intervalle
{
    public final double min;
    public final double max;
    
    public Intervalle(double min, double max)
    {
        if(min >= max)
        {
            throw new IllegalArgumentException(" min >= max");
        }
        this.min = min;
        this.max = max;
    }
    
    public String toString()
    {
    	return "min: " + min + "max " + max;
    }
}
