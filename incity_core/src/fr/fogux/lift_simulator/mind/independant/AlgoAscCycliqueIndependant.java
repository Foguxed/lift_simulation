package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.EtatContenuAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class AlgoAscCycliqueIndependant extends AlgoIndependentAsc implements GloutonAsc
{
    public EtatContenuAsc etat = new EtatContenuAsc();

    protected boolean monte;
    protected Predicate<AlgoPersonne> pDirectionPredicate;
    protected Integer extremumObjectif = null;

    protected static final Predicate<AlgoPersonne> MONTE = (p -> p.destination > p.depart);
    protected static final Predicate<AlgoPersonne> DESCEND = (p -> p.destination < p.depart);

    protected static final Comparator<Integer> INTCOMPARATOR = new Comparator<Integer>()
    {

        @Override
        public int compare(final Integer o1, final Integer o2)
        {
            return Integer.compare(o1, o2);
        }
    };

    public AlgoAscCycliqueIndependant(final AscId id, final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent)
    {
        super(id, config, phys, ascPrecedent);
        monte = true;
        pDirectionPredicate = MONTE;
    }

    protected Predicate<Integer> getPredicateAtteignablilite(final int niveauAtteignable)
    {
        if(monte)
        {
            return (i -> i >= niveauAtteignable);
        }
        else
        {
            return (i -> i <= niveauAtteignable);
        }

    }

    @Override
    public List<Integer> getInvites(final int niveau, final int placesDispo)
    {
        //phys.println(" extremumObjectif " + extremumObjectif + " niveau " + niveau);
        if(extremumObjectif != null && niveau == extremumObjectif)
        {
            changerDeSens();
            extremumObjectif = null;
        }
        etat.contenuAsc.removeIf(p -> p.destination == niveau);
        final List<AlgoPersonne> invites = new ArrayList<>();
        etat.aDelivrer.stream().filter(p -> p.depart == niveau).filter(pDirectionPredicate).forEach(p -> invites.add(p));
        final List<Integer> invitesIds =  new ArrayList<>();
        for(final AlgoPersonne p : invites)
        {
            if(invitesIds.size() < placesDispo)
            {
                etat.entre(p);
                invitesIds.add(p.id);
            }
            else
            {
                break;
            }
        }
        //phys.println(" niv " + niveau + " listeInv " + invites + " etat " + etat);
        return invitesIds;
    }

    @Override
    public void attribuer(final AlgoPersonne p)
    {
        etat.arrive(p);
        if(phys().getEtat(id).etat != EtatAscenseur.BLOQUE )
        {
            reflechir();
        }
    }

    protected Predicate<Integer> filtreDirectionnel(final EtatAsc etat)
    {
        if(monte)
        {
            if(etat.etat == EtatAscenseur.BLOQUE || etat.etat == EtatAscenseur.ARRET )
            {
                return (i -> i >= etat.positionActuelle);
            }
            else
            {
                return (i -> i >= etat.premierEtageAtteignable);
            }
        }
        else
        {
            if(etat.etat == EtatAscenseur.BLOQUE || etat.etat == EtatAscenseur.ARRET )
            {
                return (i -> i <= etat.positionActuelle);
            }
            else
            {
                return (i -> i <= etat.premierEtageAtteignable);
            }
        }
    }

    @Override
    public Integer prochainArret(Predicate<Integer> filtrage)
    {
        final EtatAsc etatphys = phys().getEtat(id);
        Optional<Integer> retour = getOptional(filtrage,etatphys);
        //System.out.println(" etatphys " + etatphys);
        filtrage = filtrage.and(etatphys.filtreAntiDemiTour());
        if(retour.isPresent())
        {
            extremumObjectif = null;
            phys().println("retour1 present");
            return retour.get();
        }
        else
        {
            retour = getExtremumAutreSens(filtrage,etatphys);
            phys().println(" extremum " + retour.isPresent());
            if(retour.isPresent())
            {
                extremumObjectif = retour.get();
                return retour.get();
            }
            else
            {
                changerDeSens();
                retour = getExtremumAutreSens(filtrage,etatphys);
                if(retour.isPresent())
                {
                    extremumObjectif = retour.get();
                    return retour.get();
                }
                else
                {
                    return null;
                }
            }
        }

    }

    protected Optional<Integer> getExtremumAutreSens(final Predicate<Integer> filtre, final EtatAsc etatPhys)
    {
        final ArrayList<Integer> possibilites = new ArrayList<>();
        etat.contenuAsc.stream().forEach(p -> possibilites.add(p.destination));
        if(monte)
        {
            etat.aDelivrer.stream().filter(DESCEND).forEach(p-> possibilites.add(p.depart));
            return possibilites.stream().filter(filtre).max(INTCOMPARATOR);

        }
        else
        {
            etat.aDelivrer.stream().filter(MONTE).forEach(p-> possibilites.add(p.depart));
            return possibilites.stream().filter(filtre).min(INTCOMPARATOR);
        }
    }

    protected void changerDeSens()
    {
        phys().println("chgmtsens ");
        monte = !monte;
        if(monte)
        {
            pDirectionPredicate = MONTE;
        }
        else
        {
            pDirectionPredicate = DESCEND;
        }
    }

    protected Optional<Integer> getOptional(final Predicate<Integer> filtrage,final EtatAsc etatphys)
    {
        final Set<Integer> arretsPossibles = new HashSet<>();
        final Predicate<Integer> filtreDirectionnel = filtreDirectionnel(etatphys).and(filtrage);
        etat.contenuAsc.stream().forEach(p -> arretsPossibles.add(p.destination));
        if(etat.contenuAsc.size() < config.nbPersMaxAscenseur())
        {
            etat.aDelivrer.stream().filter(pDirectionPredicate).forEach(p -> arretsPossibles.add(p.depart));
        }

        phys().println("arretsPossibles " + arretsPossibles + " monte " + monte);
        if(monte)
        {
            return arretsPossibles.stream().filter(filtreDirectionnel).min(INTCOMPARATOR);
        }
        else
        {
            phys().println("descend " + arretsPossibles.stream().filter(filtreDirectionnel).count());
            return arretsPossibles.stream().filter(filtreDirectionnel).max(INTCOMPARATOR);
        }
    }

    @Override
    public int evaluer(final AlgoPersonne p)
    {
        if(!atteignable(p))
        {
            return Integer.MAX_VALUE;
        }
        final int v = Math.abs(objectifActuel - p.depart) + Math.abs(objectifActuel - p.destination) + etat.nbSteps();
        if(busy)
        {
            return v;
        }
        else return Integer.MIN_VALUE + v;
    }

}
