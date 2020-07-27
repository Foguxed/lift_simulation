package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementChangementPlannifier extends EvenementChangementEtat
{
    protected final AscId ascId;
    protected DataTagCompound oldPlannifier;
    protected DataTagCompound newPlannifier;

    /**
     * Creation cote server
     */
    public EvenementChangementPlannifier(final AscId ascId, final DataTagCompound oldPlannifier, final DataTagCompound newPlannifier)
    {
        super();
        this.ascId = ascId;
        this.oldPlannifier = oldPlannifier;
        this.newPlannifier = newPlannifier;
    }

    public EvenementChangementPlannifier(final long time, final DataTagCompound data)
    {
        super(time);
        oldPlannifier = data.getCompound(TagNames.oldPlannifier);
        newPlannifier = data.getCompound(TagNames.newPlannifier);
        ascId = AscId.fromCompound(data);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changerPlannifierVers(newPlannifier);;
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changerPlannifierVers(oldPlannifier);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long atTime)
    {
        compound.setCompound(TagNames.oldPlannifier, oldPlannifier);
        compound.setCompound(TagNames.newPlannifier, newPlannifier);
        ascId.printIn(compound);
    }

}
