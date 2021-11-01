package fr.fogux.lift_simulator.mind.trajets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class EtatContenuAsc
{
    public final Set<AlgoPersonne> aDelivrer;
    public final Set<AlgoPersonne> contenuAsc;

    public EtatContenuAsc()
    {
        this(new HashSet<AlgoPersonne>(),new HashSet<AlgoPersonne>());
    }

    protected EtatContenuAsc(final Set<AlgoPersonne> aDelivrer, final Set<AlgoPersonne> contenuAsc)
    {
        this.aDelivrer = aDelivrer;
        this.contenuAsc = contenuAsc;
    }

    public EtatContenuAsc(final EtatContenuAsc cloned)
    {
        aDelivrer = new HashSet<>(cloned.aDelivrer);
        contenuAsc = new HashSet<>(cloned.contenuAsc);
    }

    public void arrive(final AlgoPersonne p)
    {
        aDelivrer.add(p);
    }

    @Override
    public boolean equals(final Object o)
    {
        if(o instanceof EtatContenuAsc)
        {
            final EtatContenuAsc contenu = (EtatContenuAsc) o;
            return contenu.aDelivrer.equals(aDelivrer) && contenu.contenuAsc.equals(contenuAsc);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(aDelivrer,contenuAsc);
    }

    @Override
    public String toString()
    {
        return "{ contenu " + contenuAsc + "aDelivrer" + aDelivrer +"}";
    }

    public void entre(final AlgoPersonne p)
    {
        if(!aDelivrer.remove(p))
        {
            throw new SimulateurException("p " + p +" doit être a delivrer");
        }
        contenuAsc.add(p);
    }

    public void sortieDe(final AlgoPersonne p)
    {
        if(!contenuAsc.remove(p))
        {
            throw new SimulateurException("p " + p +" doit etre dans l'ascenseur");
        }
    }



    public int nbSteps()
    {
        return 2*aDelivrer.size() + contenuAsc.size();
    }

    /**
     * Probablement faux
     * @param c
     * @return
     */
    @Deprecated
    public long getMinorantTotalTrajetTime(final ConfigSimu c)
    {
        final Map<Integer,Boolean> escalesmap = new HashMap<>();
        for(final AlgoPersonne p : aDelivrer)
        {
            escalesmap.put(p.destination,true);
            escalesmap.put(p.depart,false);
        }
        for(final AlgoPersonne p : contenuAsc)
        {
            escalesmap.put(p.destination, true);
        }
        final int nbEscalesSorties = (int)escalesmap.entrySet().stream().filter(e -> e.getValue()).count();
        final int nbEscalesEntrees = escalesmap.size() - nbEscalesSorties - 1;// -1 pour le cas ou l'ascenseur est déjà ouvert et va faire entrer une personne sans ouvrir les portes
        final int k = contenuAsc.size();
        final int r = aDelivrer.size();
        return (k*r + ((k+1)*k + (r+1)*r)/2)*c.getDureeSortieEntreePersonne() + ((nbEscalesEntrees)*(nbEscalesEntrees + 1) + nbEscalesSorties*(nbEscalesSorties - 1))*c.getDureePortes();

    }
    /*
    public int getMinorantCompletionTime(final ConfigSimu c)
    {
        final Set<Integer> escales = new HashSet<>();
        aDelivrer.stream().forEach(p -> {escales.add(p.depart); escales.add(p.destination);});

        return (2*aDelivrer.size() + contenuAsc.size())*((int)c.getDureeSortieEntreePersonne()) + (2*escales.size()-1)*((int)c.getDureePortes());
    }*/
}
