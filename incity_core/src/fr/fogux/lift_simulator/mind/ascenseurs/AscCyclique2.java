package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.mind.algorithmes.BPersPool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class AscCyclique2 extends AscPoolUser<BPersPool>
{

    protected boolean sansTache = true;

    protected Set<AlgoPersonne> contenu;
    protected Set<AlgoPersonne> reservees;
    protected int nextStep; // si non enDeplacement, n'est pas définit
    protected boolean enDeplacement;

    // les toutes prochaines personnes qui vont entrer dans l'ascenseur (ne contient qu'une personne sauf si il s'agit de plusieurs personnes au même étage)

    public AscCyclique2(final AscId id, final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(id, config, outputProvider, ascPrecedent);
        contenu = new HashSet<>();
        reservees = new HashSet<>();
    }
    /*
    protected int getRangSortie(final AlgoPersonne trajet, final boolean monte)
    {
        final int s = trajet.destination;
        final ArrayList<Integer> l = new ArrayList<>();
    }*/

    protected AscCyclique2 ascsuperieur()
    {
        return (AscCyclique2)ascenseurSuperieur;
    }

    protected AscCyclique2 ascinferieur()
    {
        return (AscCyclique2)ascenseurSuperieur;
    }

    @Override
    public List<Integer> getInvites(final int niveau, final int placesDispo)
    {
        final List<Integer> invites = new ArrayList<>();
        contenu.removeIf(p -> p.destination == niveau);
        return null;
    }

    @Override
    public void poolHasBeenUpdated()
    {
        if(sansTache)
        {
            sansTache = false;
            reflechir();
        }
    }

    protected boolean determinerSensPrincipal(final Stream<AlgoPersonne> contenuAConsiderer, final Stream<AlgoPersonne> reserveesAConsiderer, final EtatAsc etat)
    {
        if(etat.etat == EtatAscenseur.MONTEE)
        {
            return true;
        }
        else if(etat.etat == EtatAscenseur.DESCENTE)
        {
            return false;
        }
        int etageMin = Integer.MAX_VALUE;
        int etageMax = Integer.MIN_VALUE;

        final Optional<AlgoPersonne> opt = contenuAConsiderer.min(AlgoPersonne.destinationComparator);
        final Optional<AlgoPersonne> optmax = contenuAConsiderer.max(AlgoPersonne.destinationComparator);
        if(opt.isPresent())
        {
            etageMin = min(opt.get().destination,etageMin);
            etageMax = max(optmax.get().destination,etageMax);
        }

        final Optional<AlgoPersonne> opt1 = reserveesAConsiderer.min(AlgoPersonne.destinationComparator);
        final Optional<AlgoPersonne> opt1max = reserveesAConsiderer.max(AlgoPersonne.destinationComparator);
        final Optional<AlgoPersonne> opt2 = reserveesAConsiderer.min(AlgoPersonne.departComparator);
        final Optional<AlgoPersonne> opt2max = reserveesAConsiderer.max(AlgoPersonne.departComparator);
        if(opt1.isPresent())
        {
            etageMin = min(opt1.get().destination,etageMin);
            etageMax = max(opt1max.get().destination,etageMax);
            etageMin = min(opt2.get().depart,etageMin);
            etageMax = max(opt2max.get().depart,etageMax);
        }
        if(etageMin == Integer.MAX_VALUE)
        {
            return true;
        }
        else
        {
            final int etage = Math.round(etat.positionActuelle);
            if(etageMax <= etage)
            {
                return false;
            }
            else if(etageMin >= etage)
            {
                return true;
            }
            return Math.abs(etage - etageMax) <= Math.abs(etage - etageMin);
        }
    }

    private static final int min(final int a, final int b)
    {
        if(a < b)
        {
            return a;
        }
        return b;
    }
    private static final int max(final int a, final int b)
    {
        if(a > b)
        {
            return a;
        }
        return b;
    }

    protected void updateReservations(final EtatAsc etat,final boolean monte)
    {
        final Predicate<Integer> filtreDirectionnel = AlgoAscCycliqueIndependant.getFiltreMouvement(etat, monte);
        final Predicate<AlgoPersonne> filtreDeplacementContenu = AlgoPersonne.filterDestination(getPredicateEtagesAtteignables().and(filtreDirectionnel));
        final Integer prochainArret = innerProchainArret(AlgoPersonne.filterDestination(getPredicateEtagesAtteignables()).and(filtreDeplacementContenu),monte);
        Predicate<AlgoPersonne> concernees = AlgoPersonne.getFiltreSens(monte).and(AlgoPersonne.filterDepart(filtreDirectionnel));
        if(prochainArret != null)
        {
            Predicate<Integer> predicateEtage;
            if(monte)
            {
                predicateEtage = (i -> i <= prochainArret);
            }
            else
            {
                predicateEtage = (i -> i >= prochainArret);
            }
            concernees = concernees.and(AlgoPersonne.filterDepart(predicateEtage));
        }


    }



    protected Integer innerProchainArret(final Predicate<AlgoPersonne> fullFiltreContenu, final boolean montee)
    {
        if(!reservees.isEmpty())
        {
            return reservees.stream().findFirst().get().depart;
        }
        else
        {
            Optional<AlgoPersonne> opt;
            if(montee)
            {
                opt = contenu.stream().filter(fullFiltreContenu).min(AlgoPersonne.destinationComparator);
            }
            else
            {
                opt = contenu.stream().filter(fullFiltreContenu).max(AlgoPersonne.destinationComparator);
            }
            if(opt.isPresent())
            {
                return opt.get().depart;
            }
            else
            {
                return null;
            }
        }
    }

    @Override
    public Integer prochainArret(final Predicate<Integer> aFiltrer)
    {
        final EtatAsc etat = phys().getEtat(id);
        final Predicate<AlgoPersonne> pdestination = AlgoPersonne.filterDestination(aFiltrer);
        final Predicate<AlgoPersonne> pdepartetarrivee = pdestination.and(AlgoPersonne.filterDepart(aFiltrer));
        final boolean monte = determinerSensPrincipal(contenu.stream().filter(pdestination), reservees.stream().filter(pdepartetarrivee), etat);
        final Predicate<Integer> fullConditions = aFiltrer.and(AlgoAscCycliqueIndependant.getFiltreMouvement(etat, monte));

        final Integer reponse = null;
        if(contenu.size() < config.nbPersMaxAscenseur())
        {

        }
        else
        {

        }
        if(reponse == null && contenu.size() < config.nbPersMaxAscenseur())
        {
            sansTache = true;
        }
        return null;
    }

    @Override
    public Integer positionDattente()
    {
        return 0;
    }

}
