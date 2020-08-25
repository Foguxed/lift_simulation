package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class AlgoB2Ascenseur extends AlgoIndependentAsc implements GloutonAsc
{
    protected final List<Integer> chargementsEtDechargements;
    protected final List<Integer> dechargements;

    protected final List<AlgoPersonne> personnesACharger;

    protected static final int monteeSurvey = AlgoIndependentAsc.debugMonteeSurvey;

    public AlgoB2Ascenseur(final AscId id,final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent)
    {
        super(id,config,phys,ascPrecedent);
        personnesACharger = new ArrayList<>();
        chargementsEtDechargements = new ArrayList<>();
        dechargements = new ArrayList<>();
        busy = false;
    }


    @Override
    public void setAscenseurSuperieur(final VoisinAsc asc)
    {
        ascenseurSuperieur = asc;
    }

    @Override
    public void attribuer(final AlgoPersonne personne)
    {
        if(id.monteeId == monteeSurvey)
        {
            phys().println("se voit attribue " + this + " ceci " + personne);
        }
        chargementsEtDechargements.add(personne.depart);
        personnesACharger.add(personne);
        Collections.sort(chargementsEtDechargements);
        if(!busy)
        {
            reflechir();
        }
    }

    @Override
    public List<Integer> getInvites(final int niveau,final int placesDispo)
    {
        return getPersonnesDontLeNiveaEst(niveau, placesDispo);
    }

    @Override
    public void escaleTerminee()
    {
        reflechir();
    }

    protected List<Integer> getPersonnesDontLeNiveaEst(final int niv, final int placesDispo)
    {
        final List<AlgoPersonne> invitesP = listInvites(niv, placesDispo);
        final List<Integer> invitesId = toIds(invitesP);
        if(id.monteeId == monteeSurvey)
        {
            phys().println("choisiInvites " + this + " invites " + invitesP);
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
        if(id.monteeId == monteeSurvey)
        {
            phys().println("resultat de la reflexion " + this);
        }

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

    @Override
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
    public String toString()
    {
        return id + " d:" + dechargements + " cd:" + chargementsEtDechargements;
    }

    @Override
    public Integer prochainArret(final Predicate<Integer> filtre)
    {
        if(estPleins())
        {
            return dechargements.stream().filter(filtre).findFirst().orElse(null);
        }
        else
        {
            return chargementsEtDechargements.stream().filter(filtre).findFirst().orElse(null);
        }
    }


    @Override
    public int evaluer(final AlgoPersonne p)
    {
        if(!atteignable(p))
        {
            return Integer.MAX_VALUE;
        }
        else
        {
            return chargementsEtDechargements.size();
        }
    }
}
