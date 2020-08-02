package fr.fogux.lift_simulator.population;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.EvenementEntreePersonne;
import fr.fogux.lift_simulator.evenements.EvenementSortiePersonne;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.AscenseurSimu;
import fr.fogux.lift_simulator.physic.EtageSimu;
import fr.fogux.lift_simulator.stats.StatCarrier;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.Personne;

public class PersonneSimu extends Personne implements StatCarrier
{
    protected final Simulation simu;

    protected EtageSimu etageActuel;
    protected boolean enAttentePalier;
    protected AscenseurSimu ascenseur;

    protected final long timeInput;
    protected long heureEntreeAscenseur;
    protected long heureSortieAscenseur;
    protected AscId ascenseurUtilise;

    public PersonneSimu(final Simulation simu,final int id, final int destination, final EtageSimu etageActuel)
    {
        super(destination, id);
        this.simu = simu;
        enAttentePalier = true;
        timeInput = simu.getTime();
        this.etageActuel = etageActuel;
    }

    public boolean livree()
    {
        return heureSortieAscenseur > 0;
    }

    public long getTransportTime()
    {
        return heureSortieAscenseur - timeInput;
    }


    @Override
    public void printStats(final DataTagCompound compound)
    {
        if(ascenseurUtilise != null)
        {
            ascenseurUtilise.printIn(compound);
        }
        compound.setLong(TagNames.heureEntreeAsc, heureEntreeAscenseur);
        compound.setLong(TagNames.heureSortieAsc, heureSortieAscenseur);
        compound.setLong(TagNames.heureInput, timeInput);
    }

    public void choisirDestination()
    {
        simu.getPrgm().appelExterieur(id, etageActuel.getNiveau(), destination);
    }

    public boolean jeSortDeAscenseur(final int etage)
    {
        if (etage == destination)
        {
            new EvenementSortiePersonne(simu.getTime(),
                simu.getConfig(), id).runOn(simu);
            return true;
        } else
            return false;
    }

    public void tenterEntrerAscenseur(final AscenseurSimu ascenseur, final int niveau) throws SimulateurAcceptableException
    {
        if(!enAttentePalier)
        {
            throw new SimulateurAcceptableException(this + " est déja dans " + ascenseur + " impossible de la faire entrer dans " + ascenseur);
        }
        else if(etageActuel.getNiveau() != niveau)
        {
            throw new SimulateurAcceptableException(this + " est au niveau " + etageActuel.getNiveau() + " impossible de la faire entrer dans " + ascenseur + " au niveau " + niveau);
        }
        new EvenementEntreePersonne(
            simu.getTime(),simu.getConfig(), id, ascenseur.getId(),
            etageActuel.getNiveau()).runOn(simu);
        enAttentePalier = false;
    }

    public void entrerDansAscenseur(final AscenseurSimu ascenseur)
    {
        heureEntreeAscenseur = simu.getTime();
        ascenseurUtilise = ascenseur.getId();
        this.ascenseur = ascenseur;
        ascenseur.estEntre(this);
        // Utils.msg(this, "ascenseur " + ascenseur);
        simu.getPrgm().appelInterieur(destination, ascenseur.getId());
    }

    public void sortirDeAscenseur()
    {
        heureSortieAscenseur = simu.getTime();
        // Utils.msg(this, "ascenseurSortir " + ascenseur +" deja sorti " + deleteMe);
        ascenseur.sortieDe(this);
        ascenseur = null;
    }

}
