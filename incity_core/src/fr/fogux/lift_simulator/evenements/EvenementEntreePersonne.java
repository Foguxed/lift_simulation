package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementEntreePersonne extends AnimatedEvent
{


    protected final int personneId;
    protected final AscId ascenseurId;
    protected final int niveau;

    public EvenementEntreePersonne(final long time, final DataTagCompound data)
    {
        super(time, data);
        personneId = data.getInt(TagNames.personneId);
        ascenseurId = AscId.fromCompound(data);
        niveau = data.getInt(TagNames.etage);
    }

    public EvenementEntreePersonne(final long debutEntree, final ConfigSimu c, final int personneId, final AscId ascenseurId, final int niveau)
    {
        super(debutEntree + c.getDureeSortieEntreePersonne(),debutEntree);
        this.personneId = personneId;
        this.ascenseurId = ascenseurId;
        this.niveau = niveau;
    }

    @Override
    public void simuRun(final Simulation simu)
    {

        simu.getPrgm().appelInterieur(simu.getPersonne(personneId).getDestination(), ascenseurId);
        if(!simu.interrupted())
        {
            simu.getPersonne(personneId).entrerDansAscenseur(
                simu.getImmeubleSimu().getAscenseur(ascenseurId));
        }
        // Utils.msg(this, "ascenseur " + ascenseur);
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long time)
    {
        super.printFieldsIn(compound,time);
        ascenseurId.printIn(compound);
        compound.setInt(TagNames.personneId, personneId);
        compound.setInt(TagNames.etage, niveau);
    }

    @Override
    protected void runAnimation(final AnimationProcess animP,final long timeDebut, final long animationDuree)
    {
        PersonneVisu.getPersonne(personneId).getPersonneGroup().animationDeplacement(
            timeDebut, animationDuree, animP.getImmeubleVisu().getEtage(niveau),
            animP.getImmeubleVisu().getAscenseur(ascenseurId));
    }

    @Override
    protected void sortieAnimation(final AnimationProcess animP)
    {

    }

    @Override
    public void reRun(final Simulation simu)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurId).reRunDemandeDeListe();
        simu.getPrgm().appelInterieur(simu.getPersonne(personneId).getDestination(), ascenseurId);
    }

}
