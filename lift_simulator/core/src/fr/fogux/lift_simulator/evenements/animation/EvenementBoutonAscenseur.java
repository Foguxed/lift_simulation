package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementBoutonAscenseur extends EvenementChangementEtat
{
    protected final int niveau;
    protected final int ascenseurId;
    protected final boolean newBoutonOn;
    protected final boolean oldBoutonOn;
    
    
    public EvenementBoutonAscenseur(int niveau,int ascenseurId,boolean newBoutonOn, boolean oldBoutonOn)
    {
        super();
        this.niveau = niveau;
        this.ascenseurId = ascenseurId;
        this.newBoutonOn = newBoutonOn;
        this.oldBoutonOn = oldBoutonOn;
    }
    
    public EvenementBoutonAscenseur(long time, DataTagCompound data)
    {
        super(time);
        this.niveau = data.getInt(TagNames.etage);
        this.ascenseurId = data.getInt(TagNames.ascenseurId);
        this.newBoutonOn = data.getBoolean(TagNames.newBoutonOn);
        this.oldBoutonOn = data.getBoolean(TagNames.oldBoutonOn);
    }
    
    @Override
    public void visuRunetatSuivant()
    {
        Simulateur.getImmeubleVisu().getAscenseur(ascenseurId).changerEtatBouton(niveau, newBoutonOn);
    }

    @Override
    public void visuRunetatPrecedent()
    {
        Simulateur.getImmeubleVisu().getAscenseur(ascenseurId).changerEtatBouton(niveau, oldBoutonOn);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setInt(TagNames.etage, niveau);
        compound.setInt(TagNames.ascenseurId, ascenseurId);
        compound.setBoolean(TagNames.newBoutonOn, newBoutonOn);
        compound.setBoolean(TagNames.oldBoutonOn, oldBoutonOn);
    }

}
