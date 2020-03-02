package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementConsoleLine extends EvenementChangementEtat
{
    protected String ligne;

    public EvenementConsoleLine(String ligne)
    {
        this.ligne = ligne;
    }

    public EvenementConsoleLine(long time, DataTagCompound data)
    {
        super(time);
        ligne = data.getString(TagNames.description);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setString(TagNames.description, ligne);
    }

    @Override
    public void visuRunetatSuivant()
    {
        System.out.println("ALLLLLOOOOO");
        Simulateur.getSimulateur().getGameScreen().ajouterLigneConsole(ligne);
    }

    @Override
    public void visuRunetatPrecedent()
    {
        Simulateur.getSimulateur().getGameScreen().enleverDernierLigneConsole();
        ;
    }

}
