package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementErreur extends EvenementChangementEtat
{
    protected String description;

    public EvenementErreur(final String description)
    {
        this.description = description;
    }

    public EvenementErreur(final long time, final DataTagCompound data)
    {
        super(time);
        description = data.getString(TagNames.description);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long t)
    {
        compound.setString(TagNames.description, description);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animp)
    {
        Simulateur.getSimulateur().getGameScreen().setDisplayedError(description);
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animp)
    {
        Simulateur.getSimulateur().getGameScreen().setDisplayedError(null);
    }

}
