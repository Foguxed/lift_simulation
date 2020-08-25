package fr.fogux.lift_simulator.population;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.EvenementEntreePersonne;
import fr.fogux.lift_simulator.evenements.EvenementSortiePersonne;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.AscenseurSimu;
import fr.fogux.lift_simulator.stats.StatCarrier;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.Personne;

public class PersonneSimu extends Personne implements StatCarrier
{
    protected final Simulation simu;

    protected int etageActuel;
    protected boolean enAttentePalier;

    protected final long timeInput;
    protected long heureEntreeAscenseur;
    protected long heureSortieAscenseur;
    protected AscId ascenseurUtilise;

    public PersonneSimu(final Simulation simu,final int id, final int destination, final int etageActuel)
    {
        super(destination, id);
        this.simu = simu;
        enAttentePalier = true;
        timeInput = simu.getTime();
        this.etageActuel = etageActuel;
    }

    public PersonneSimu(final PersonneSimu shadowed, final Simulation newSimu)
    {
        super(shadowed.destination,shadowed.id);
        simu = newSimu;
        etageActuel = shadowed.etageActuel;
        enAttentePalier = shadowed.enAttentePalier;
        timeInput = shadowed.timeInput;
        heureEntreeAscenseur = shadowed.heureEntreeAscenseur;
        heureSortieAscenseur = shadowed.heureSortieAscenseur;
        ascenseurUtilise = shadowed.ascenseurUtilise;
    }

    public boolean livree()
    {
        return heureSortieAscenseur > 0;
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
        simu.getPrgm().appelExterieur(id, etageActuel, destination);
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
        else if(etageActuel != niveau)
        {
            throw new SimulateurAcceptableException(this + " est au niveau " + etageActuel + " impossible de la faire entrer dans " + ascenseur + " au niveau " + niveau);
        }
        new EvenementEntreePersonne(
            simu.getTime(),simu.getConfig(), id, ascenseur.getId(),
            etageActuel).runOn(simu);
        enAttentePalier = false;
    }

    public void entrerDansAscenseur(final AscenseurSimu ascenseur)
    {
        heureEntreeAscenseur = simu.getTime();
        ascenseurUtilise = ascenseur.getId();
        ascenseur.estEntre(this);
    }

    public void sortirDeAscenseur()
    {
        heureSortieAscenseur = simu.getTime();
        // Utils.msg(this, "ascenseurSortir " + ascenseur +" deja sorti " + deleteMe);
        simu.getImmeubleSimu().getAscenseur(ascenseurUtilise).sortieDe(this);
    }

    public void rerunSortieDeAscenseur()
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurUtilise).reRunDemandeDeListe();
    }

    public long getTempsTrajet()
    {
        if(livree())
        {
            return heureSortieAscenseur - timeInput;
        }
        else
        {
            return simu.getTime() - timeInput;
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " waiting " + enAttentePalier;
    }
}
