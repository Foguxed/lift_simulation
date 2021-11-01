package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementBoutonAscenseur extends EvenementChangementEtat
{
    protected final int niveau;
    protected final AscId ascenseurId;
    protected final boolean newBoutonOn;
    protected final boolean oldBoutonOn;

    public EvenementBoutonAscenseur(final int niveau, final AscId ascenseurId, final boolean newBoutonOn, final boolean oldBoutonOn)
    {
        super();
        this.niveau = niveau;
        this.ascenseurId = ascenseurId;
        this.newBoutonOn = newBoutonOn;
        this.oldBoutonOn = oldBoutonOn;
    }

    public EvenementBoutonAscenseur(final long time, final DataTagCompound data)
    {
        super(time);
        niveau = data.getInt(TagNames.etage);
        ascenseurId = AscId.fromCompound(data);
        newBoutonOn = data.getBoolean(TagNames.newBoutonOn);
        oldBoutonOn = data.getBoolean(TagNames.oldBoutonOn);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animp)
    {
        animp.getImmeubleVisu().getAscenseur(ascenseurId).changerEtatBouton(niveau, newBoutonOn);
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animp)
    {
        animp.getImmeubleVisu().getAscenseur(ascenseurId).changerEtatBouton(niveau, oldBoutonOn);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound,final long t)
    {
        compound.setInt(TagNames.etage, niveau);
        ascenseurId.printIn(compound);
        compound.setBoolean(TagNames.newBoutonOn, newBoutonOn);
        compound.setBoolean(TagNames.oldBoutonOn, oldBoutonOn);
    }


}
