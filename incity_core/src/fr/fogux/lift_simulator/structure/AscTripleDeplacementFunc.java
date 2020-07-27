package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.physic.ConfigSimu;

public class AscTripleDeplacementFunc extends AscDeplacementFunc
{

    protected final long t2;
    protected final float vLim;
    protected final float c1;
    protected final float c2;

    public AscTripleDeplacementFunc(final long t0, final float xi, final float vi, final float xf, final float a, final float vLim)
    {
        super(t0,xi,vi,a,getT1(t0,vLim,vi,a));
        this.c2 =  xf - vLim*vLim/(2*a);
        this.vLim = vLim;
        t2 = t0 + (long) ((xf - xi + (vi*vi)/(2*a))/vLim - vi/a);
        c1 = (vLim*vLim - vi*vi)/(2*a) + xi;
    }
    
    public static final long getT1(long t0, float vLim, float vi, float a)
    {
    	return (long) ((vLim-vi)/a) + t0;
    }
    
    public AscTripleDeplacementFunc(final long t0, final float xi, final float vi, final float a,final float vLim, final AscSoftDep sd)
	{
    	super(t0,xi,vi,a,getT1(t0,vLim,vi,a));
    	this.vLim = vLim;
    	this.t2 = sd.t + (long) ((sd.v-vLim)/a);
    	this.c1 = (vLim*vLim - vi*vi)/(2*a) + xi;
    	this.c2 = sd.x + (sd.v*sd.v - vLim*vLim)/(2*a);
	}
    
  
    
    public boolean isPossible(long tf)
    {
    	return t2+ConfigSimu.TEQUALITY_MARGIN>=t1 & tf+ConfigSimu.TEQUALITY_MARGIN >= t2;
    }
    
    public static AscSoftDep getDeplacementTangeant(final long t0, final float xi, final float vi, final float vLim, final Polynome p)
    {
    	final float a = p.a;
    	float sqrtDeter = (float)Math.sqrt(
    			4*a*(vLim*(p.ti - t0) + xi - p.xi) + 2f * (p.vi - vi) * (p.vi + vi - 2*vLim) 
    			);
    	if(a > 0)
    	{
    		sqrtDeter = -sqrtDeter;
    	}
    	final long t3 = p.ti + (long) ((2*(vLim - p.vi) + sqrtDeter)/(2*a));
    	return new AscSoftDep(t3, p.apply(t3), p.applyDer(t3),a < 0 ? (byte) -4 : (byte) 4);
    }
    
    
    
    @Override
    public float getX(final long absTime)
    {
        final float t;
        if(absTime < t1)
        {
            t = absTime - t0;
            return xi + (a/2) * t*t + vi*t;
        }
        else if(absTime <= t2)
        {
            t = absTime - t1;
            return c1 + vLim*t;
        }
        else
        {
            t = absTime - t2;
            return c2 - (a/2) * t * t + vLim * t;
        }
    }
    @Override
    public String toString()
    {
        return "AscTripleDepFunc " + "t2:" + t2 + ",vLim:" + vLim + ",xOriginePartie2:" +c1+ " super:" + super.toString();
    }

    @Override
    public float getV(final long absTime)
    {
        float t;
        if(absTime < t1)
        {
            t = absTime - t0;
            return a * t + vi;
        }
        else if(absTime <= t2)
        {
            t = absTime - t1;
            return vLim;
        }
        else
        {
            t = absTime - t2;
            return -a * t + vLim;
        }
    }

	@Override
	public Polynome getConnectablePolynome(float xOffset) 
	{
		return new Polynome(t0,xi + xOffset, vi, a);
	}
	
	@Override
	public long getCriticalT() 
	{
		return t2;
	}
}
