package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.evenements.EvenementMouvementPortes;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonTriangle;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.ProgrammeEntryListener;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.structure.Etage;
import fr.fogux.lift_simulator.utils.Utils;

public class EtageSimu extends Etage
{

    protected List<PersonneSimu> listAttente = new ArrayList<PersonneSimu>();
    protected boolean transferEnCours = false;
    protected boolean ouvertureEnCours = false;

    public EtageSimu(int numero)
    {
        super(numero);
    }

    /*
     * public void choisirDirection(Personne personne) { }
     */

    public void onOuverture(AscenseurSimu asc)
    {
        transferEnCours = true;
        ouvertureEnCours = false;
        faireEntrerSuivant(asc);
    }

    public void faireEntrerSuivant(AscenseurSimu asc)
    {
        if (!listAttente.isEmpty() && asc.getNbPersonnesIn() < asc.persMax)
        {
            listAttente.get(0).jentreDansAscenseur(asc);
            listAttente.remove(0);
        } else
        {
            transferEnCours = false;
            ProgrammeEntryListener.finDeTransfertDePersonnes(getNiveau(), asc.getId());
        }
    }

    public void ouvrir(AscenseurSimu asc)
    {
        if (ouvertureEnCours)
        {
            throw new SimulateurAcceptableException(
                "Ouverture des portes impossible car elles sont deja en cours d'ouverture sur l'asc " + asc.getId());
        }
        ouvertureEnCours = true;
        Utils.printCreationOf(new EvenementMouvementPortes(asc.getId(), getNiveau(), true));
    }

    public void fermer(AscenseurSimu asc)
    {
        if (transferEnCours)
        {
            throw new SimulateurAcceptableException(
                "Fermeture des portes impossible si les personnes ne sont pas encore toutes entrees. etage" + num);
        }
        Utils.printCreationOf(new EvenementMouvementPortes(asc.getId(), getNiveau(), false));

    }

    public void rappuyerBoutonsSiNecessaire()
    {
        for (PersonneSimu pers : listAttente)
        {
            System.out.println("pers choix destination");
            pers.choisirDestination();
        }
    }

    public void entree(PersonneSimu personne)
    {
        listAttente.add(personne);
    }

    public void nouvelleEntreePossible(AscenseurSimu ascenseur)
    {
        faireEntrerSuivant(ascenseur);
    }

    public int getNiveau()
    {
        return num;
    }

    public boolean boutonHautAllume()
    {
        return hautAllume;
    }

    public boolean boutonBasAllume()
    {
        return basAllume;
    }

    public void setBoutonState(boolean allume, boolean boutonDuHaut)
    {
        new EvenementBoutonTriangle(this.num, boutonDuHaut, allume, boutonDuHaut ? hautAllume : basAllume).print();
        if (boutonDuHaut)
        {
            hautAllume = allume;
        } else
        {
            basAllume = allume;
        }

    }

    @Override
    public void arriveeDe(int nbPersonnes, int destination)
    {
        for (int i = 0; i < nbPersonnes; i++)
        {
            entree(new PersonneSimu(destination, this));
            // System.out.println("new pers");
        }
    }
}
