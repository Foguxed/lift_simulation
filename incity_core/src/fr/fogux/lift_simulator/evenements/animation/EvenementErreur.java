package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementErreur extends EvenementChangementEtat
{
    protected String description;

    public EvenementErreur(String description)
    {
        this.description = description;
    }

    public EvenementErreur(long time, DataTagCompound data)
    {
        super(time);
        description = data.getString(TagNames.description);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setString(TagNames.description, description);
    }

    @Override
    public void visuRunetatSuivant()
    {
        Simulateur.getSimulateur().getGameScreen().setDisplayedError(description);
    }

    @Override
    public void visuRunetatPrecedent()
    {
        Simulateur.getSimulateur().getGameScreen().setDisplayedError(null);
    }

}
