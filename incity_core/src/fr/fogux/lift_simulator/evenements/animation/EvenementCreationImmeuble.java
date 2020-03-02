package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementCreationImmeuble extends EvenementAnimationOnly
{

    protected final int etageMin;
    protected final int etageMax;
    protected final int nbAscenseurs;

    public EvenementCreationImmeuble(int etageMin, int etageMax, int nbAscenseurs)
    {
        super(0);
        this.etageMin = etageMin;
        this.etageMax = etageMax;
        this.nbAscenseurs = nbAscenseurs;
    }

    public EvenementCreationImmeuble(long time, DataTagCompound compound)
    {
        super(0);
        etageMin = compound.getInt(TagNames.etageMin);
        etageMax = compound.getInt(TagNames.etageMax);
        nbAscenseurs = compound.getInt(TagNames.nbAscenseurs);
    }

    public void create()
    {
        Simulateur.getSimulateur().getGameScreen().loadVisualisation(etageMin, etageMax, nbAscenseurs);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setInt(TagNames.etageMin, etageMin);
        compound.setInt(TagNames.etageMax, etageMax);
        compound.setInt(TagNames.nbAscenseurs, nbAscenseurs);
    }

    @Override
    public void visuRun()
    {

    }

}
