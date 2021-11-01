package fr.fogux.lift_simulator.mind.planifiers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.ConfigAlgoCycleOption;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;

public class ContenuEtPrevision
{

    public final AlgoPersGroup prochainClient; // on prend au maximum contenu.placesDisponible de ce prochain client
    public final ContenuAsc contenu;

    public ContenuEtPrevision( final AlgoPersGroup prochainClient,final ContenuAsc contenu)
    {
        this.prochainClient = prochainClient;
        this.contenu = contenu;
    }

    public boolean destinationCompatible(final AlgoPersGroup p)
    {
        if(prochainClient == null)
        {
            return contenu.destinationCompatible(p);
        }
        else
        {
            return contenu.destinationCompatible(p) || prochainClient.getDestination() == p.getDestination();
        }
    }

    public int nbArrets()
    {
        final Set<Integer> tousLesArrets = new HashSet<>();
        if(prochainClient != null)
        {
            tousLesArrets.add(prochainClient.getDestination());
            tousLesArrets.add(prochainClient.getEtage());
        }
        contenu.livraisons.keySet().stream().forEach(l -> tousLesArrets.add(l));
        return tousLesArrets.size();
    }

    public int nbPlacesNonUtilises()
    {
        if(prochainClient == null)
        {
            return contenu.placesDispo();
        }
        else
        {
            return contenu.placesDispo() - prochainClient.size();
        }
    }

    public Cycle getSuiteEtages(final boolean actuellementEnMontee, final int etageActuel)
    {
        final ArrayList<Integer> retour = new ArrayList<>();
        final TreeSet<Integer> tousLesArrets = new TreeSet<>();
        if(prochainClient != null)
        {
            tousLesArrets.add(prochainClient.getDestination());
            tousLesArrets.add(prochainClient.getEtage());
        }
        contenu.livraisons.keySet().stream().forEach(l -> tousLesArrets.add(l));
        final List<Integer> avantDemiTour = new ArrayList<>();
        final List<Integer> apresDemiTour = new ArrayList<>();
        if(actuellementEnMontee)
        {
            tousLesArrets.tailSet(etageActuel).forEach(i -> avantDemiTour.add(i));
            tousLesArrets.descendingSet().tailSet(etageActuel-1).forEach(i -> apresDemiTour.add(i));
        }
        else
        {
            tousLesArrets.descendingSet().tailSet(etageActuel).forEach(i -> avantDemiTour.add(i));
            tousLesArrets.tailSet(etageActuel+1).forEach(i -> apresDemiTour.add(i));
        }
        return new Cycle(avantDemiTour,apresDemiTour,actuellementEnMontee);
    }

    public int nbPersonnesDesservies(final int nbPlacesAscenseur)
    {
        int b = 0;
        if(prochainClient != null)
        {
            b = prochainClient.size();
        }
        return Math.min(nbPlacesAscenseur, nbPlacesAscenseur - contenu.placesDispo + b);
    }

    public float getTotalScoreDeplacement(final int etageActuel, final ConfigAlgoCycleOption config)
    {
        int v = 0;
        for(final TrueLivraison l : contenu.livraisons.values())
        {
            v += l.nb*(Math.abs(l.destination - etageActuel) + config.flatBonusLivraisonPersonne);
        }
        if(prochainClient != null)
        {
            v += prochainClient.size()*Math.abs(prochainClient.getEtage() - prochainClient.getDestination());
        }

        return v;
    }

    @Override
    public String toString()
    {
        return "CetP contenu " + contenu + " pClient " + prochainClient;
    }



    public class Cycle
    {

        public List<Integer> avantDemiTour;
        public List<Integer> apresDemiTour;
        public final boolean cycleMontee;


        public Cycle(final List<Integer> avantDemiTour, final List<Integer> apresDemiTour, final boolean cycleMontee)
        {
            this.avantDemiTour = avantDemiTour;
            this.apresDemiTour = apresDemiTour;
            this.cycleMontee = cycleMontee;
        }

        public int nbArrets()
        {
            return avantDemiTour.size() + apresDemiTour.size();
        }

        public int getTotalParcourt(final int etageActuel)
        {
            if(avantDemiTour.isEmpty())
            {
                if(apresDemiTour.isEmpty())
                {
                    return 0;
                }
                else
                {
                    return Math.abs(etageActuel - apresDemiTour.get(apresDemiTour.size() - 1));
                }
            }
            else
            {
                int v =0;
                v += Math.abs(avantDemiTour.get(avantDemiTour.size() - 1) - etageActuel);
                if(apresDemiTour.isEmpty())
                {
                    return v;
                }
                else
                {
                    return v + Math.abs(apresDemiTour.get(apresDemiTour.size() - 1) - avantDemiTour.get(avantDemiTour.size() - 1));
                }
            }

        }

        public int pointCulminent()
        {
            int r;
            if(cycleMontee)
            {
                r = Integer.MIN_VALUE;
                if(!apresDemiTour.isEmpty())
                {
                    r = Math.max(apresDemiTour.get(0), avantDemiTour.get(avantDemiTour.size() -1));
                }
            }
            else
            {
                r = Integer.MAX_VALUE;
                if(!apresDemiTour.isEmpty())
                {
                    r = Math.min(apresDemiTour.get(0), avantDemiTour.get(avantDemiTour.size() -1));
                }
            }
            return r;
        }

        public int getEtageMin()
        {
            if(cycleMontee)
            {
                int r = Integer.MAX_VALUE - 5;
                if(!avantDemiTour.isEmpty())
                {
                    r = Math.min(r, avantDemiTour.get(0));
                }
                if(!apresDemiTour.isEmpty())
                {
                    r = Math.min(r, apresDemiTour.get(apresDemiTour.size() -1));
                }
                return r;
            }
            else
            {
                if(avantDemiTour.isEmpty())
                {
                    if(apresDemiTour.isEmpty())
                    {
                        return  Integer.MAX_VALUE - 5;
                    }
                    else
                    {
                        return apresDemiTour.get(0);
                    }
                }
                else return avantDemiTour.get(avantDemiTour.size()-1);

            }
        }

        public int getEtageMax()
        {
            if(cycleMontee)
            {
                if(avantDemiTour.isEmpty())
                {
                    if(apresDemiTour.isEmpty())
                    {
                        return Integer.MIN_VALUE +5;
                    }
                    else
                    {
                        return apresDemiTour.get(0);
                    }
                }
                return avantDemiTour.get(avantDemiTour.size()-1);
            }
            else
            {
                int r = Integer.MIN_VALUE +5;
                if(!avantDemiTour.isEmpty())
                {
                    r = Math.max(r, avantDemiTour.get(0));
                }
                if(!apresDemiTour.isEmpty())
                {
                    r = Math.max(r, apresDemiTour.get(apresDemiTour.size() -1));
                }
                return r;
            }
        }

        public boolean sansDemiTourForce()
        {
            return apresDemiTour.isEmpty();
        }

        @Override
        public String toString()
        {
            return "avantDemiTour " + avantDemiTour + " apresDemiTour " + apresDemiTour;
        }

        /**
         *
         * @param c
         * @return true si il risque d'y avoir une colision avec c si ce cycle effectue un demi tour
         */
        public boolean collisionEnCasDemiTour(final Cycle c)
        {
            if(cycleMontee)
            {
                //alors cycleAvecRisqueCollision est le cycle de l'ascenenseur inferieur
                return getEtageMax() <= c.getEtageMax();
            }
            else
            {
                return getEtageMin() >= c.getEtageMin();
            }
        }
    }
}
