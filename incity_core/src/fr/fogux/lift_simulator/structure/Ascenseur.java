package fr.fogux.lift_simulator.structure;

import java.util.ArrayList;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public abstract class Ascenseur
{
    protected final AscId id;
    public final int persMax;

    protected long instantProchainArret = Long.MIN_VALUE;
    protected float vi;
    protected float xi;
    protected long ti;

    protected float acceleration;

    protected ArrayList<Integer> boutonsAllumes = new ArrayList<>();

    /**
     * ce sont les updates de xObjectif (l'objectif réel) qui sont enregistrées, car la fonction de déplacement peut en être
     * déduite
     */
    protected float xObjectifActuel;

    protected int etageObjectif;
    protected DeplacementFunc depFunc;



    public Ascenseur(final AscId id, final int persMax, final float initialHeight)
    {
        this.id = id;
        this.persMax = persMax;
        xi = initialHeight;
        etageObjectif = (int)xi;
        xObjectifActuel = xi;
        vi = 0;
        ti = 0;
    }

    protected void changerXObjectif(final float newXObjectif, final long timeChangement, final ConfigSimu c)
    {
        if(instantProchainArret <= timeChangement)
        {
            vi = 0f;
            xi = xObjectifActuel;
        }
        else
        {
            if(depFunc == null)
            {
                instantiateDepFunc(c);
            }
            xi = depFunc.getX(timeChangement);
            vi = depFunc.getV(timeChangement);
        }
        depFunc = null;
        ti = timeChangement;
        xObjectifActuel = newXObjectif;
        updateInstantProchainArret(c);
    }

    public long getHeureArrivee(final float newXObjectif, final long timeChangement, final ConfigSimu c)
    {
        final float tempXi;
        final float tempVi;
        if(instantProchainArret <= timeChangement)
        {
            tempVi = 0f;
            tempXi = xObjectifActuel;
        }
        else
        {
            if(depFunc == null)
            {
                instantiateDepFunc(c);
            }
            tempVi = depFunc.getX(timeChangement);
            tempXi = depFunc.getV(timeChangement);
        }
        return AscDeplacementFunc.getTimeStraightToObjective(c,timeChangement,tempVi,tempXi,newXObjectif);
    }

    protected void updateInstantProchainArret(final ConfigSimu c)
    {
        instantProchainArret = AscDeplacementFunc.getTimeStraightToObjective(c, ti, vi, xi, xObjectifActuel);
        Simulateur.println("resultat " + instantProchainArret);
    }



    protected void instantiateDepFunc(final ConfigSimu c)
    {
        depFunc = AscDeplacementFunc.getDeplacementFunc(c,ti, xi, vi, xObjectifActuel);
    }

    /**
     * update le field instantProchainArret en fonction de ti, vi, xi, et xObjectifActuel (le nouvel objectif différent de xi)
     * @param c
     */

    public float getXObjectif()
    {
        return xObjectifActuel;
    }

    public AscId getId()
    {
        return id;
    }

    public void changerEtatBouton(final int bouton, final boolean allume)
    {
        if (allume)
        {
            boutonsAllumes.add(bouton);
        } else
        {
            boutonsAllumes.removeIf(i -> i == bouton);
        }
    }

    public void setDeplacement(final long debutDeplacement, final float xI, final float vI, final float gamma)
    {
        ti = debutDeplacement;
        vi = vI;
        xi = xI;
        acceleration = gamma;
    }

    @Override
    public String toString()
    {
        return " Ascenseur : " + id + " ";
    }
}
