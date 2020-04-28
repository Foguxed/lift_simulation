package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class EvenementSortiePersonne extends AnimatedEvent
{

    protected final int personneId;

    public EvenementSortiePersonne(final long time, final DataTagCompound data)
    {
        super(time, data);

        personneId = data.getInt(TagNames.personneId);
    }

    public EvenementSortiePersonne(final long debutSortie, final ConfigSimu c, final int personneId)
    {
        super(debutSortie + c.getDureeSortieEntreePersonne(), debutSortie);
        this.personneId = personneId;
    }

    @Override
    public void simuRun(final Simulation s)
    {
        s.getPersonne(personneId).sortirDeAscenseur();
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long time)
    {
        super.printFieldsIn(compound,time);
        compound.setInt(TagNames.personneId, personneId);
    }

    @Override
    protected void runAnimation(final AnimationProcess p, final long timeDebut, final long animationDuree)
    {
        PersonneVisu.getPersonne(personneId).animationSortie(timeDebut, animationDuree);
    }

    @Override
    protected void sortieAnimation(final AnimationProcess p)
    {
    }

}
