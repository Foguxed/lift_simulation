package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementChangementPlanifier extends EvenementChangementEtat
{
    protected final AscId ascId;
    protected DataTagCompound oldPlanifier;
    protected DataTagCompound newPlanificateur;

    /**
     * Creation cote server
     */
    public EvenementChangementPlanifier(final AscId ascId, final DataTagCompound oldPlanificateur, final DataTagCompound newPlanificateur)
    {
        super();
        this.ascId = ascId;
        this.oldPlanifier = oldPlanificateur;
        this.newPlanificateur = newPlanificateur;
    }

    public EvenementChangementPlanifier(final long time, final DataTagCompound data)
    {
        super(time);
        oldPlanifier = data.getCompound(TagNames.oldPlanner);
        newPlanificateur = data.getCompound(TagNames.newPlanner);
        ascId = AscId.fromCompound(data);
    }

    @Override
    public void visuRunetatSuivant(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changerPlanifierVers(newPlanificateur);;
    }

    @Override
    public void visuRunetatPrecedent(final AnimationProcess animP)
    {
        animP.getImmeubleVisu().getAscenseur(ascId).changerPlanifierVers(oldPlanifier);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long atTime)
    {
        compound.setCompound(TagNames.oldPlanner, oldPlanifier);
        compound.setCompound(TagNames.newPlanner, newPlanificateur);
        ascId.printIn(compound);
    }

	

}
