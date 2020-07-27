package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class AscDoubleDeplacementFunc extends AscDeplacementFunc
{

    protected final float b1;
    protected final float c1;

    public AscDoubleDeplacementFunc(final long t0,final float xi, final float vi, final float xf, final float a)
    {
        super(t0,xi, vi, a, t0 + (long) ( (bonne_sqrt_value(a,xi,xf,vi) - vi)/a));
        b1 = (bonne_sqrt_value(a,xi,xf,vi));
        c1 = (xf+xi)/2f - vi*vi/(4f*a);
    }

    public AscDoubleDeplacementFunc(final long t0,final float xi, final float vi, final float a,AscSoftDep softDep)
    {
    	super(t0,xi,vi,a,getT1(t0,softDep.t,vi,softDep.v,a));
    	final float t = t1-t0;
    	c1 = xi + (a/2)*t*t+vi*t;
    	b1 = a*t+vi;
    }
    
    private static long getT1(long t0, long tf,float v0, float vf, float a)
    {
    	return ((tf+t0) + (long)((vf-v0)/a))/2;
    }
    
    public static float getPickSpeed(final long t0, final float xi, final float vi, final float a,AscSoftDep softDep)
    {
    	return a*(getT1(t0,softDep.t,vi,softDep.v,a) - t0)+vi;
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
    
    public static AscSoftDep getDeplacementTangeant(final long t0, final float xi, final float vi, final Polynome p)
    {
    	final float deltaT = p.ti - t0;
    	final float deltaV = p.vi - vi;
    	final float a = p.a;
    	final long t2 = p.ti + (long) ((a*(2*(vi+p.vi)*deltaT + 4*(xi-p.xi)+a*deltaT*deltaT)-deltaV*deltaV)/(4*a*(deltaV-a*deltaT)));
    	return new AscSoftDep(t2, p.apply(t2), p.applyDer(t2),a < 0 ? (byte) -3: (byte) 3);
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
            return c1 - (a/2) * t * t + b1 * t;
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
            return - a * t + b1;
        }
    }

    @Override
    public String toString()
    {
        return "double dep func " + super.toString();
    }

	@Override
	public Polynome getConnectablePolynome(float xOffset) 
	{
		return new Polynome(t0, xi + xOffset, vi, a);
	}

	@Override
	public long getCriticalT() 
	{
		return t1;
	}

	@Override
	public boolean isPossible(long tf) 
	{
		return tf+ConfigSimu.TEQUALITY_MARGIN>=t1 & t1+ConfigSimu.TEQUALITY_MARGIN>=t0;
	}
}
