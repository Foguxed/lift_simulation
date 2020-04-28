package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementBoutonTriangle extends EvenementChangementEtat
{
    protected final int niveau;
    protected final boolean versHaut;
    protected final boolean newBoutonOn;
    protected final boolean oldBoutonOn;

    public EvenementBoutonTriangle(final int niveau, final boolean versHaut, final boolean newBoutonOn, final boolean oldBoutonOn)
    {
        super();
        this.niveau = niveau;
        this.versHaut = versHaut;
        this.newBoutonOn = newBoutonOn;
        this.oldBoutonOn = oldBoutonOn;
    }

    public EvenementBoutonTriangle(final long time, final DataTagCompound data)
    {
        super(time);
        niveau = data.getInt(TagNames.etage);
        versHaut = data.getBoolean(TagNames.versHaut);
        newBoutonOn = data.getBoolean(TagNames.newBoutonOn);
        oldBoutonOn = data.getBoolean(TagNames.oldBoutonOn);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getEtage(niveau).changerEtatBouton(versHaut, newBoutonOn);
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getEtage(niveau).changerEtatBouton(versHaut, oldBoutonOn);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long printTime)
    {
        compound.setInt(TagNames.etage, niveau);
        compound.setBoolean(TagNames.versHaut, versHaut);
        compound.setBoolean(TagNames.newBoutonOn, newBoutonOn);
        compound.setBoolean(TagNames.oldBoutonOn, oldBoutonOn);
    }

}
