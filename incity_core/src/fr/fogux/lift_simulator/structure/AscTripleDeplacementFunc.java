package fr.fogux.lift_simulator.structure;

public class AscTripleDeplacementFunc extends AscDeplacementFunc
{

    protected final long t2;
    protected final float vLim;
    protected final float c2;

    public AscTripleDeplacementFunc(final long t0, final float xi, final float vi, final float xf, final float a, final float vLim)
    {
        super(t0,xi,vi,xf,a,(long) ((vLim - vi)/a) + t0, xf - vLim*vLim/(2*a));
        this.vLim = vLim;
        t2 = t0 + (long) ((xf - xi + (vi*vi)/(2*a))/vLim - vi/a);
        c2 = (vLim*vLim - vi*vi)/(2*a) + xi;
    }

    @Override
    public float getX(final long absTime)
    {
        float t;
        if(absTime < t1)
        {
            t = absTime - t0;
            return xi + (a/2) * t*t + vi*t;
        }
        else if(absTime <= t2)
        {
            t = absTime - t1;
            return c2 + vLim*t;
        }
        else
        {
            t = absTime - t2;
            return c3 - (a/2) * t * t + vLim * t;
        }
    }
    @Override
    public String toString()
    {
        return "AscTripleDepFunc " + "t2:" + t2 + ",vLim:" + vLim + ",xOriginePartie2:" +c2+ " super:" + super.toString();
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
}
