package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementChangementDeplacementAscenseur extends PrintableEvenement
{
    protected final int ascId;
    protected final float oldXi;
    protected final float oldVi;
    protected final float oldGamma;
    protected final long oldDepStart;
    protected final float newXi;
    protected final float newVi;
    protected final float newG;

    public EvenementChangementDeplacementAscenseur(long time, DataTagCompound data)
    {
        super(time, data);

        ascId = data.getInt(TagNames.ascenseurId);
        newXi = data.getFloat(TagNames.newInitialPos);
        newVi = data.getFloat(TagNames.newInitialSpeed);
        newG = data.getFloat(TagNames.newAcceleration);
        oldXi = data.getFloat(TagNames.oldInitialPos);
        oldVi = data.getFloat(TagNames.oldInitialSpeed);
        oldGamma = data.getFloat(TagNames.oldAcceleration);
        oldDepStart = data.getLong(TagNames.oldDeplacementTime);
    }

    public EvenementChangementDeplacementAscenseur(long time, int ascId, long oldDepTime, float oldXi, float oldVi,
        float oldGamma, float newXi, float newVi, float newG, boolean executerTasks)
    {
        super(time, executerTasks);
        System.out.println("new AccelerarationChangement " + newG + " time " + time);
        this.ascId = ascId;
        this.oldDepStart = oldDepTime;
        this.oldXi = oldXi;
        this.oldVi = oldVi;
        this.oldGamma = oldGamma;
        this.newXi = newXi;
        this.newVi = newVi;
        this.newG = newG;

    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setInt(TagNames.ascenseurId, ascId);
        compound.setFloat(TagNames.newInitialPos, newXi);
        compound.setFloat(TagNames.newInitialSpeed, newVi);
        compound.setFloat(TagNames.newAcceleration, newG);
        compound.setFloat(TagNames.oldInitialPos, oldXi);
        compound.setFloat(TagNames.oldInitialSpeed, oldVi);
        compound.setFloat(TagNames.oldAcceleration, oldGamma);
        compound.setLong(TagNames.oldDeplacementTime, oldDepStart);

    }

    @Override
    public void simuRun()
    {
        super.simuRun();
        Simulateur.getImmeubleSimu().getAscenseur(ascId).setDeplacement(time, newXi, newVi, newG);
    }

    @Override
    public void visuRun()
    {
        if (GestionnaireDeTaches.marcheArriere())
        {
            Simulateur.getImmeubleVisu().getAscenseur(ascId).setDeplacement(oldDepStart, oldXi, oldVi, oldGamma);
        } else
        {
            Simulateur.getImmeubleVisu().getAscenseur(ascId).setDeplacement(time, newXi, newVi, newG);
        }
    }

}
