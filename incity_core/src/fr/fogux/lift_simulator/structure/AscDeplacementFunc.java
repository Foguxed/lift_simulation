package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public abstract class AscDeplacementFunc implements DeplacementFunc
{
    protected final long t0;
    protected final long t1;
    protected final float a;

    protected final float vi;
    protected final float xi;

    public AscDeplacementFunc(final long t0, final float xi, final float vi, final float a, final long t1)
    {
        this.t0 = t0;
        this.xi = xi;
        this.vi = vi;
        this.a = a;
        this.t1 = t1;
    }
    
    public static AscDeplacementFunc getDirectDeplacementFunc(final ConfigSimu c,final long t0, final float xi, final float vi, final float xf)
    {

        if(c.faitTroisPhases(xi,vi,xf))
        {
            float a;
            float vLim;
            if(xf >= xi)
            {
                a = c.getAscenseurAcceleration();
                vLim = c.getAscenseurSpeed();
            }
            else
            {
                a = -c.getAscenseurAcceleration();
                vLim = -c.getAscenseurSpeed();
            }
            return new AscTripleDeplacementFunc(t0, xi, vi, xf, a, vLim);
        }
        else
        {
            final float a = choixAcceleration(xi,xf,vi,c);
            Simulateur.println("choix a " + a);
            return new AscDoubleDeplacementFunc(t0, xi, vi, xf, a);
        }
    }
    @Deprecated
    public static DemiTour getDemiTour(byte demiTourType,long t0, float xi, float vi, ConfigSimu c)
    {
    	final float a;
    	switch(demiTourType)
    	{
    	case 1:// a <0 et vi > 0
    		a = - c.getAscenseurAcceleration();
    		break;
    	case -1:
    		a = c.getAscenseurAcceleration();
    		break;
    	default:
    		throw new IllegalArgumentException("demiTourType is " + demiTourType + " and should only be 1 or -1 here");
    	}
    	float tmax = (-vi/a);
    	return new DemiTour(t0 + ((long) tmax), 0.5f*a*tmax*tmax + vi*tmax + xi);
    }
    
    public static AscSoftDep getSoftDepStraightToObjective(final ConfigSimu c, final AscState EI, final float xf)
    {
    	return getSoftDepStraightToObjective(c,EI.t,EI.v,EI.x,xf);
    }
    public static AscSoftDep getSoftDepStraightToObjective(final ConfigSimu c, final long t0, final float vi, final float xi, final float xf)
    {
        Simulateur.println("time straight to O t0 " + t0 + " vi " + vi + " xi " + xi + " xf " + xf + " Config " + c.toString());
        final long time;
        final float a;
        byte type;
        if(c.faitTroisPhases(xi, vi, xf))
        {

            //System.out.println("3 phases");
            float vLim;
            if(xf >= xi)
            {
                a = c.getAscenseurAcceleration();
                vLim = c.getAscenseurSpeed();
                type = (byte) 2;
            }
            else
            {
                a = -c.getAscenseurAcceleration();
                vLim = -c.getAscenseurSpeed();
                type = (byte) -2;
            }
            time = t0 + (long) ((xf-xi + vi*vi/(2*a))/vLim + (vLim - vi)/a);
        }
        else
        {
            a = choixAcceleration(xi,xf,vi,c);
            //System.out.println("valeur au dessus" + (2*AscDoubleDeplacementFunc.bonne_sqrt_value(a, xi, xf, vi) - vi));
            time = t0 + (long) ((2*AscDoubleDeplacementFunc.bonne_sqrt_value(a, xi, xf, vi) - vi)/a);
            if(a>=0)
            {
            	type = (byte) 1;
            }
            else
            {
            	type = (byte) -1;
            }
        }
        return new AscSoftDep(time, xf, 0f, type);
    }
    

    public static float getMaxXPossible(long tf, ConfigSimu c, AscState EI, boolean aPositif)
    {
    	final float vLim;
    	final float a;
    	if(aPositif)
    	{
    		vLim = c.getAscenseurSpeed();
    		a = c.getAscenseurAcceleration();
    	}
    	else
    	{
    		vLim = -c.getAscenseurSpeed();
    		a = -c.getAscenseurAcceleration();
    	}
    	long tm = AscTripleDeplacementFunc.getT1(EI.t, vLim, EI.v, a);
    	if(tf <= tm)
    	{
    		long t = tf - EI.t;
    		return (a/2)*t*t + EI.v*t + EI.x;
    	}
    	else
    	{
    		long t = tf - tm;
    		return vLim*t + (vLim*vLim - EI.v*EI.v)/(2*a) + EI.x;
    	}
    }
    
    public static AscDeplacementFunc getDeplacementFunc(final ConfigSimu c, final long t0, final float x0, final float v0, final AscSoftDep sd)
    {
    	byte b = sd.softDepType;
    	final float a;
    	if(b > 0)
    	{
    		a = c.getAscenseurAcceleration();
    	}
    	else
    	{
    		a = -c.getAscenseurAcceleration();
    		b = (byte)-b;
    	}
    	
    	switch(b)
    	{
    	case 1:
    	case 3:
    		return new AscDoubleDeplacementFunc(t0, x0, v0, a, sd);
    	case 2:
    	case 4:
    		return new AscTripleDeplacementFunc(t0, x0, v0, a, a<0 ? -c.getAscenseurSpeed():c.getAscenseurSpeed(), sd);
    	default:
    		throw new IllegalArgumentException(" softDepType cannot be " + sd.softDepType);
    	}
    }
    
    public static AscSoftDep getDeplacementTangeant(final ConfigSimu c, final long t0, final float xi, final float vi, final Polynome p)
    {
    	AscSoftDep versionDoubleDep = AscDoubleDeplacementFunc.getDeplacementTangeant(t0, xi, vi, p);
    	if(Math.abs(AscDoubleDeplacementFunc.getPickSpeed(t0, xi, vi, p.a, versionDoubleDep)) <= c.getAscenseurSpeed())
    	{
    		return versionDoubleDep;
    	}
    	else
    	{
    		final float vLim;
    		if(p.a > 0)
    		{
    			vLim = c.getAscenseurSpeed();
    		}
    		else
    		{
    			vLim = -c.getAscenseurSpeed();
    		}
    		return AscTripleDeplacementFunc.getDeplacementTangeant(t0, xi, vi, vLim, p);
    	}
    }
    /**
     * 
     * @param a
     * @param vi
     * @return 0 si pas de demi tour, -1 si bloque l'asc inf, 1 si bloque l'asc superieur
     */
    private static byte demiTourType(final float a, final float vi)
    {
    	if(a > 0)
    	{
    		if(vi < - ConfigSimu.SPEED_ERROR_MARGIN)
    		{
    			return -1;
    		}
    	}
    	else
    	{
    		if(vi > ConfigSimu.SPEED_ERROR_MARGIN)
    		{
    			return 1;
    		}
    	}
    	return 0;
    }
    
    private static float choixAcceleration(final float xi, final float xf, final float vi, final ConfigSimu c)
    {
        if(vi >= 0)
        {
            if(xf >= getXArretMinimalMontee(c,xi,vi))
            {
                return c.getAscenseurAcceleration();
            }
            else
            {
                return -c.getAscenseurAcceleration();
            }
        }
        else
        {
            if(xf <= getXArretMaximalDescente(c, xi, vi))
            {
                return -c.getAscenseurAcceleration();
            }
            else
            {
                return c.getAscenseurAcceleration();
            }
        }
    }

    public static float getXArretMinimalMontee(final ConfigSimu c, final float xi, final float vi)
    {
        return xi + vi*vi/(2f*c.getAscenseurAcceleration());
    }

    public static float getXArretMaximalDescente(final ConfigSimu c, final float xi, final float vi)
    {
        return xi - vi*vi/(2f*c.getAscenseurAcceleration());
    }
    
    @Override
    public abstract float getX(final long absTime);

    @Override
    public abstract float getV(final long absTime);
    
    /**
     * 
     * @return t tel que si il y a collision alors il y a collision à ce t
     */
    public abstract long getCriticalT();
    
    public abstract Polynome getConnectablePolynome(float xOffset);
    
    public abstract boolean isPossible(long tf);
    
    public long getEndOfConnectablePolynome()
    {
    	return t1;
    }
    
    @Override
    public String toString()
    {
        return "[t1:" + t1 + ",a:" + a + " xi:" + xi +"]";
    }

	
}
