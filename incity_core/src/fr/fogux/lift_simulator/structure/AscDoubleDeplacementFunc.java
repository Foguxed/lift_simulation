package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.Simulateur;

public class AscDoubleDeplacementFunc extends AscDeplacementFunc
{

    protected final float b3;

    public AscDoubleDeplacementFunc(final long t0,final float xi, final float vi, final float xf, final float a)
    {
        super(t0,xi, vi, xf, a, t0 + (long) ( (bonne_sqrt_value(a,xi,xf,vi) - vi)/a) ,(xf+xi)/2f - vi*vi/(4f*a));
        b3 = (bonne_sqrt_value(a,xi,xf,vi));
    }

    public static float bonne_sqrt_value(final float a, final float xi, final float xf, final float vi)
    {
        Simulateur.println("valeur dans sqrt " + (vi*vi/2f + a * (xf-xi)));
        if(a >= 0)
        {
            return (float)Math.sqrt(vi*vi/2f + a * (xf-xi));
        }
        else
        {
            return (float)-Math.sqrt(vi*vi/2f + a * (xf-xi));
        }
    }

    @Override
    public float getX(final long absTime)
    {
        long t;
        if(absTime <= t1)
        {
            t = absTime - t0;
            return xi + (a/2) * t*t + vi*t;
        }
        else
        {
            t = absTime - t1;
            return c3 - (a/2) * t * t + b3 * t;
        }
    }

    @Override
    public float getV(final long absTime)
    {
        long t;
        if(absTime <= t1)
        {
            t = absTime - t0;
            return a * t + vi;
        }
        else
        {
            t = absTime - t1;
            return - a * t + b3;
        }
    }

    @Override
    public String toString()
    {
        return "double dep func " + super.toString();
    }
}
