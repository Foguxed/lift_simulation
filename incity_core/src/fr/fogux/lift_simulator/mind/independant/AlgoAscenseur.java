package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class AlgoAscenseur implements VoisinAsc
{
    protected final List<Integer> chargementsEtDechargements;
    protected final List<Integer> dechargements;

    protected final List<AlgoPersonne> personnesACharger;

    protected VoisinAsc ascenseurSuperieur;
    protected final VoisinAsc ascenseurInferieur;
    protected final AscId id;

    protected final ConfigSimu config;
    protected final InterfacePhysique phys;

    protected boolean busy;

    protected int objectifActuel;

    public AlgoAscenseur(final AscId id,final ConfigSimu config, final InterfacePhysique phys, final VoisinAsc ascPrecedent)
    {
        ascenseurInferieur = ascPrecedent;
        personnesACharger = new ArrayList<>();
        chargementsEtDechargements = new ArrayList<>();
        dechargements = new ArrayList<>();
        this.config = config;
        this.phys = phys;
        this.id = id;
        busy = false;
    }

    @Override
    public void setAscenseurSuperieur(final VoisinAsc asc)
    {
        ascenseurSuperieur = asc;
    }

    public void attribuer(final AlgoPersonne personne)
    {
        chargementsEtDechargements.add(personne.depart);
        personnesACharger.add(personne);
        Collections.sort(chargementsEtDechargements);
        if(!busy)
        {
            reflechir();
        }
    }

    public List<Integer> getInvites(final int niveau,final int placesDispo)
    {
        return getPersonnesDontLeNiveaEst(niveau, placesDispo);
    }

    public void escaleTerminee()
    {
        reflechir();
    }

    protected List<Integer> getPersonnesDontLeNiveaEst(final int niv, final int placesDispo)
    {
        final List<AlgoPersonne> invitesP = listInvites(niv, placesDispo);
        final List<Integer> invitesId = toIds(invitesP);
        if(id.stackId == 0 && id.monteeId == 0)
        {
            phys.println("travail " + dechargements + " " +chargementsEtDechargements );
        }
        for(final AlgoPersonne p : invitesP)
        {
            personnesACharger.remove(p);
            chargementsEtDechargements.remove(((Integer) niv));
            chargementsEtDechargements.add(p.destination);
            dechargements.add(p.destination);
        }
        dechargements.forEach(n ->
        {
            if(n == niv)
            {
                chargementsEtDechargements.remove((Integer)niv);
            }
        });
        dechargements.removeIf(niveau -> niveau == niv);
        Collections.sort(dechargements);

        return invitesId;
    }

    protected static List<Integer> toIds(final List<AlgoPersonne> l)
    {
        final List<Integer> retour = new ArrayList<>();
        for(final AlgoPersonne p : l)
        {
            retour.add(p.id);
        }
        return retour;
    }

    protected List<AlgoPersonne> listInvites(final int niveau, final int placesDispo)
    {
        final List<AlgoPersonne> retour = new ArrayList<>();
        for(final AlgoPersonne p : personnesACharger)
        {
            if(retour.size() == placesDispo)
            {
                return retour;
            }
            if(p.depart == niveau)
            {
                retour.add(p);
            }
        }
        return retour;
    }

    protected boolean estPleins()
    {
        return dechargements.size() == config.nbPersMaxAscenseur();
    }

    protected void reflechir()
    {

        Integer prochainArret;
        if(estPleins())
        {
            prochainArret = getPremierNiveauQuiConvient(dechargements);
        }
        else
        {
            prochainArret = getPremierNiveauQuiConvient(chargementsEtDechargements);
        }

        if(prochainArret == null)
        {
            if(busy)
            {
                if(id.stackId == 0 && id.monteeId == 0)
                {
                    phys.println("devient afk " + id  + " " + dechargements + " " + chargementsEtDechargements);
                }
                busy = false;
                updateVoisins();
            }
            else
            {
                if(id.stackId == 0 && id.monteeId == 0)
                {
                    phys.println("reste afk " + id + " " + dechargements + " " + chargementsEtDechargements);
                }
                phys.changerDestination(id, ascenseurInferieur.getLimitSup() + 1, false);
            }
        }
        else
        {
            if(id.stackId == 0 && id.monteeId == 0)
            {
                phys.println("trouve escale " + id + " " + dechargements + " " + chargementsEtDechargements + " dest " + prochainArret);
            }
            busy = true;
            objectifActuel = prochainArret;
            phys.changerDestination(id, prochainArret, true);
            updateVoisins();
        }
    }

    protected void updateVoisins()
    {
        ascenseurSuperieur.updateLimitVoisin(false);
        ascenseurInferieur.updateLimitVoisin(true);
    }

    public Integer getPremierNiveauQuiConvient(final List<Integer> ints)
    {
        for(final Integer integer : ints)
        {
            if(integer < ascenseurSuperieur.getLimitInf() && integer > ascenseurInferieur.getLimitSup())
            {
                return integer;
            }
        }
        return null;
    }

    @Override
    public int getLimitSup()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurInferieur.getLimitSup() + 1;
        }
    }

    @Override
    public int getLimitInf()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurSuperieur.getLimitInf() - 1;
        }
    }

    @Override
    public void updateLimitVoisin(final boolean isSup)
    {
        if(!busy)
        {
            reflechir();
            if(!busy)// il ne s'est donc rien passé
            {
                if(isSup)
                {
                    ascenseurInferieur.updateLimitVoisin(true);
                }
                else
                {
                    ascenseurSuperieur.updateLimitVoisin(false);
                }
            }
        }
    }

}
