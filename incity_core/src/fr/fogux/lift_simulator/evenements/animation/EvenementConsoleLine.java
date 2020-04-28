package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementConsoleLine extends EvenementChangementEtat
{
    protected String ligne;

    public EvenementConsoleLine(final String ligne)
    {
        this.ligne = ligne;
    }

    public EvenementConsoleLine(final long time, final DataTagCompound data)
    {
        super(time);
        ligne = data.getString(TagNames.description);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound,final long t)
    {
        compound.setString(TagNames.description, ligne);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animp)
    {
        Simulateur.getSimulateur().getGameScreen().ajouterLigneConsole(ligne);
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animp)
    {
        Simulateur.getSimulateur().getGameScreen().enleverDernierLigneConsole();

    }

}
