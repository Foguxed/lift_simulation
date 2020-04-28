package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementChangementMouvement extends EvenementChangementEtat
{
    protected final AscId ascId;
    protected final float newXobjectif;
    protected final float oldXObjectif;
    protected final long oldTi;
    protected final float oldXi;
    protected final float oldVi;


    public EvenementChangementMouvement(final AscId ascId, final float newXobjectif, final float oldXObjectif, final long oldTi, final float oldXi, final float oldVi)
    {
        super();
        this.newXobjectif = newXobjectif;
        this.oldXObjectif = oldXObjectif;
        this.oldTi = oldTi;
        this.oldXi = oldXi;
        this.oldVi = oldVi;
        this.ascId = ascId;
    }

    public EvenementChangementMouvement(final long time, final DataTagCompound data)
    {
        super(time);
        newXobjectif = data.getFloat(TagNames.newXObjectif);
        oldXObjectif = data.getFloat(TagNames.oldXObjectif);
        oldTi = data.getLong(TagNames.oldInitialTime);
        oldXi = data.getFloat(TagNames.oldInitialPos);
        oldVi = data.getFloat(TagNames.oldInitialSpeed);
        ascId = AscId.fromCompound(data);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changementDuFuturXObjectif(time, newXobjectif);
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changementVersAncienXObjectif(oldXObjectif, oldTi, oldXi, oldVi);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long atTime)
    {
        compound.setFloat(TagNames.newXObjectif, newXobjectif);
        compound.setFloat(TagNames.oldXObjectif, oldXObjectif);
        compound.setLong(TagNames.oldInitialTime, oldTi);
        compound.setFloat(TagNames.oldInitialPos, oldXi);
        compound.setFloat(TagNames.oldInitialSpeed, oldVi);
        ascId.printIn(compound);
    }

}
