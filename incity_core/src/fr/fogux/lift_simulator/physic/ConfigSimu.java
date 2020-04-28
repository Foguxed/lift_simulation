package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class ConfigSimu extends ConfigImmeuble
{

    public static final float EQUALITY_MARGIN = 0.000001f; // en niveau



    protected final float ascenseurSpeed;
    protected final float acceleration;

    protected final long dureeEntreeSortiePers;
    protected final long dureePortes;

    protected final int capaciteAsc;
    protected final float margeInterAsc;

    protected final float marge_sup_inter_ascenseur;



    public ConfigSimu(final int niveauMin, final int niveauMax, final int[] repartAsc, final float ascenseurSpeed, final float acceleration,
        final long dureeEntreeSortiePers, final long dureePortes, final int capaciteAsc, final float margeInterAsc)
    {
        super(niveauMin, niveauMax, repartAsc);
        this.ascenseurSpeed = ascenseurSpeed;
        this.acceleration = acceleration;
        this.dureeEntreeSortiePers = dureeEntreeSortiePers;
        this.dureePortes = dureePortes;
        this.capaciteAsc = capaciteAsc;
        this.margeInterAsc = margeInterAsc;
        marge_sup_inter_ascenseur = margeSup(margeInterAsc);
    }

    private static final float margeSup(final float marge)
    {
        return marge + 0.001f* marge;
    }

    public ConfigSimu(final DataTagCompound c)
    {
        super(c);
        ascenseurSpeed = c.getFloat(TagNames.ascenseurSpeed);
        acceleration = c.getFloat(TagNames.acceleration);
        dureeEntreeSortiePers = c.getLong(TagNames.dureeEntreeSortiePers);
        dureePortes = c.getLong(TagNames.dureePortes);
        capaciteAsc = c.getInt(TagNames.capaciteAsc);
        margeInterAsc = c.getFloat(TagNames.margeInterAsc);
        marge_sup_inter_ascenseur = margeSup(margeInterAsc);
    }

    @Override
    public void printFieldsIn(final DataTagCompound c)
    {
        super.printFieldsIn(c);
        printOnlySimuFieldsIn(c);
    }

    public void printOnlySimuFieldsIn(final DataTagCompound c)
    {
        c.setFloat(TagNames.ascenseurSpeed, ascenseurSpeed);
        c.setFloat(TagNames.acceleration, acceleration);
        c.setLong(TagNames.dureeEntreeSortiePers, dureeEntreeSortiePers);
        c.setLong(TagNames.dureePortes, dureePortes);
        c.setInt(TagNames.capaciteAsc, capaciteAsc);
        c.setFloat(TagNames.margeInterAsc, margeInterAsc);
    }

    public long getDureeSortieEntreePersonne()
    {
        return dureeEntreeSortiePers;
    }

    public float getAscenseurSpeed()
    {
        return ascenseurSpeed;
    }

    public float getAscenseurAcceleration()
    {
        return acceleration;
    }

    public float getMargeInterAscenseur()
    {
        return margeInterAsc;
    }

    public float getMargeSupInterAscenseur()
    {
        return marge_sup_inter_ascenseur;
    }

    public long getDureePortes()
    {
        return dureePortes;
    }

    public int nbPersMaxAscenseur()
    {
        return capaciteAsc;
    }




    public boolean faitTroisPhases(final float xi, final float vi, final float xf)
    {
        final float distMinimale = (getAscenseurSpeed()*getAscenseurSpeed() - vi*vi/2f)/getAscenseurAcceleration();
        return Math.abs(xf-xi) > distMinimale;
    }
}
