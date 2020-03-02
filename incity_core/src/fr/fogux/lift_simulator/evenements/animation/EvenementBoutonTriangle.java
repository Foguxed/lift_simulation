package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementBoutonTriangle extends EvenementChangementEtat
{
    protected final int niveau;
    protected final boolean versHaut;
    protected final boolean newBoutonOn;
    protected final boolean oldBoutonOn;

    public EvenementBoutonTriangle(int niveau, boolean versHaut, boolean newBoutonOn, boolean oldBoutonOn)
    {
        super();
        this.niveau = niveau;
        this.versHaut = versHaut;
        this.newBoutonOn = newBoutonOn;
        this.oldBoutonOn = oldBoutonOn;
    }

    public EvenementBoutonTriangle(long time, DataTagCompound data)
    {
        super(time);
        this.niveau = data.getInt(TagNames.etage);
        this.versHaut = data.getBoolean(TagNames.versHaut);
        this.newBoutonOn = data.getBoolean(TagNames.newBoutonOn);
        this.oldBoutonOn = data.getBoolean(TagNames.oldBoutonOn);
    }

    @Override
    public void visuRunetatSuivant()
    {
        Simulateur.getImmeubleVisu().getEtage(niveau).changerEtatBouton(versHaut, newBoutonOn);
    }

    @Override
    public void visuRunetatPrecedent()
    {
        Simulateur.getImmeubleVisu().getEtage(niveau).changerEtatBouton(versHaut, oldBoutonOn);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setInt(TagNames.etage, niveau);
        compound.setBoolean(TagNames.versHaut, versHaut);
        compound.setBoolean(TagNames.newBoutonOn, newBoutonOn);
        compound.setBoolean(TagNames.oldBoutonOn, oldBoutonOn);
    }

}
