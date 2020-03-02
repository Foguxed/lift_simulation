package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class EvenementEntreePersonne extends AnimatedEvent
{
    protected final int personneId;
    protected final int ascenseurId;
    protected final int niveau;

    public EvenementEntreePersonne(long time, DataTagCompound data)
    {
        super(time, data);
        System.out.println("entree pers constructor");
        personneId = data.getInt(TagNames.personneId);
        ascenseurId = data.getInt(TagNames.ascenseurId);
        niveau = data.getInt(TagNames.etage);
    }

    public EvenementEntreePersonne(long timeAbsolu, int personneId, int ascenseurId, int niveau)
    {
        super(timeAbsolu, true);
        this.personneId = personneId;
        this.ascenseurId = ascenseurId;
        this.niveau = niveau;
    }

    @Override
    public void simuRun()
    {
        super.simuRun();
        PersonneSimu.getPersonne(personneId).entrerDansAscenseur(
            Simulateur.getImmeubleSimu().getAscenseur(ascenseurId));
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setInt(TagNames.ascenseurId, ascenseurId);
        compound.setInt(TagNames.personneId, personneId);
        compound.setInt(TagNames.etage, niveau);
    }

    @Override
    protected void runAnimation(long timeDebut, long animationDuree)
    {
        System.out.println("run animation evenement entree personne");
        PersonneVisu.getPersonne(personneId).getPersonneGroup().animationDeplacement(
            timeDebut, animationDuree, Simulateur.getImmeubleVisu().getEtage(niveau),
            Simulateur.getImmeubleVisu().getAscenseur(ascenseurId));
    }

    @Override
    protected void sortieAnimation()
    {

    }

}
