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

    protected final float c3;

    public AscDeplacementFunc(final long t0, final float xi, final float vi, final float xf, final float a, final long t1,final float xOriginePartie3)
    {
        this.t0 = t0;
        this.xi = xi;
        this.vi = vi;
        this.a = a;
        this.t1 = t1;
        c3 = xOriginePartie3;
    }

    public static AscDeplacementFunc getDeplacementFunc(final ConfigSimu c,final long t0, final float xi, final float vi, final float xf)
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

    public static long getTimeStraightToObjective(final ConfigSimu c, final long t0, final float vi, final float xi, final float xf)
    {
        Simulateur.println("time straight to O t0 " + t0 + " vi " + vi + " xi " + xi + " xf " + xf + " Config " + c.toString());

        if(c.faitTroisPhases(xi, vi, xf))
        {

            //System.out.println("3 phases");
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
            return t0 + (long) ((xf-xi + vi*vi/(2*a))/vLim + (vLim - vi)/a);
        }
        else
        {
            final float a = choixAcceleration(xi,xf,vi,c);
            //System.out.println("valeur au dessus" + (2*AscDoubleDeplacementFunc.bonne_sqrt_value(a, xi, xf, vi) - vi));
            return t0 + (long) ((2*AscDoubleDeplacementFunc.bonne_sqrt_value(a, xi, xf, vi) - vi)/a);
        }
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
        /*if((vi*vi/2f + c.getAscenseurAcceleration() * (xf-xi)) > 0)
        {
            return c.getAscenseurAcceleration();
        }
        else
        {
            return -c.getAscenseurAcceleration();
        }*/
    }

    private static boolean necessiteDemiTour(final float xi, final float xf, final float vi, final ConfigSimu c)
    {
        if(vi >= 0)
        {
            return xf < getXArretMinimalMontee(c,xi,vi);
        }
        else
        {
            return xf > getXArretMaximalDescente(c,xi,vi);
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

    @Override
    public String toString()
    {
        return "[t1:" + t1 + ",a:" + a + ",xOriginePartie3:" + c3 + ",xi:" + xi +"]";
    }

}
