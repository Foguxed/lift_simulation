package fr.fogux.lift_simulator.population;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.evenements.EvenementEntreePersonne;
import fr.fogux.lift_simulator.evenements.EvenementSortiePersonne;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.mind.ProgrammeEntryListener;
import fr.fogux.lift_simulator.physic.AscenseurSimu;
import fr.fogux.lift_simulator.physic.EtageSimu;
import fr.fogux.lift_simulator.physic.TimeConfig;
import fr.fogux.lift_simulator.stats.StatsCarrier;
import fr.fogux.lift_simulator.structure.Etage;
import fr.fogux.lift_simulator.structure.Personne;

public class PersonneSimu extends Personne implements StatsCarrier
{
    protected EtageSimu etageActuel;
    protected static List<PersonneSimu> personnesList;
    protected boolean deleteMe = false;
    protected boolean deleteMeRe = false;
    protected AscenseurSimu ascenseur;
    
    protected final long timeInput;
    protected long heureEntreeAscenseur;
    protected long heureSortieAscenseur;
    protected int ascenseurUtilise;
    
    public PersonneSimu(int destination,EtageSimu etageActuel)
    {
        super(destination,personnesList.size());
        timeInput = GestionnaireDeTaches.getInnerTime();
        personnesList.add(this);
        this.etageActuel = etageActuel;
        choisirDestination();
    }
    
    public static void initClass()
    {
        personnesList = new ArrayList<PersonneSimu>();
    }
    
    public static PersonneSimu getPersonne(int id)
    {
        return personnesList.get(id);
    }
    
    public static void printPersStats()
    {
        for(PersonneSimu pers : personnesList)
        {
            DataTagCompound compound  = new DataTagCompound();
            pers.printStats(compound);
            GestionnaireDeFichiers.printStatPersonne(compound.getValueAsString());
        }
    }
    
    @Override
    public void printStats(DataTagCompound compound)
    {
        compound.setInt(TagNames.ascenseurId, ascenseurUtilise);
        compound.setLong(TagNames.heureEntreeAsc, heureEntreeAscenseur);
        compound.setLong(TagNames.heureSortieAsc, heureSortieAscenseur);
        compound.setLong(TagNames.heureInput, timeInput);
    }
    
    public void choisirDestination()
    {
        if(etageActuel.getNiveau() == destination)
        {
            //TODO faire un truc
        }
        else if(etageActuel.getNiveau()>destination && !etageActuel.boutonBasAllume())
        {
            ProgrammeEntryListener.appeler(etageActuel.getNiveau(),false);
        }
        else if(etageActuel.getNiveau()<destination && !etageActuel.boutonHautAllume())
        {
            ProgrammeEntryListener.appeler(etageActuel.getNiveau(),true);
        }
    }
    
    public boolean jeSortDeAscenseur(Etage etage)
    {
        if(etage.getNiveau() == destination)
        {
            new EvenementSortiePersonne(TimeConfig.getDureeSortieEntreePersonne() + GestionnaireDeTaches.getInnerTime(),id).print();
            return true;
        }
        else return false;
    }
    
    public void jentreDansAscenseur(AscenseurSimu ascenseur)
    {
        //Utils.msg(this, "ascenseur choisir entree deja entre? "  + deleteMeRe);
        new EvenementEntreePersonne(TimeConfig.getDureeSortieEntreePersonne()+GestionnaireDeTaches.getInnerTime(),
                id, ascenseur.getId(),etageActuel.getNiveau()).print();
        deleteMeRe = true;
    }
    
    public void entrerDansAscenseur(AscenseurSimu ascenseur)
    {
        heureEntreeAscenseur = GestionnaireDeTaches.getInnerTime();
        ascenseurUtilise = ascenseur.getId();
        ascenseur.entreeDe(this);
        this.ascenseur = ascenseur;
        //Utils.msg(this, "ascenseur " + ascenseur);
        etageActuel.nouvelleEntreePossible(ascenseur);
        ProgrammeEntryListener.onAppuiSurNiveau(destination, ascenseur.getId());
    }
    
    public void sortirDeAscenseur()
    {
        heureSortieAscenseur = GestionnaireDeTaches.getInnerTime();
        //Utils.msg(this, "ascenseurSortir " + ascenseur +" deja sorti " + deleteMe);
        ascenseur.sortieDe(this);
        ascenseur = null;
        deleteMe = true;
    }

}
