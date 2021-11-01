package fr.fogux.lift_simulator.mind.planifiers;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public class ContenuAsc
{
    public final TreeMap<Integer,TrueLivraison> livraisons;
    protected int placesDispo;

    public ContenuAsc(final int placesDispo)
    {
        livraisons = new TreeMap<>();
        this.placesDispo = placesDispo;
    }

    public ContenuAsc(final ContenuAsc toShadow)
    {
        livraisons = new TreeMap<>(toShadow.livraisons);
        placesDispo = toShadow.placesDispo();
    }

    public boolean contientPersDansSens(final boolean monte, final int niveauActuel)
    {
        if(monte)
        {
            return livraisons.ceilingEntry(niveauActuel +1) != null;
        }
        else
        {
            return livraisons.floorKey(niveauActuel -1) != null;
        }

    }

    public Optional<TrueLivraison> getProchainArret(final int etageActuel, final boolean monte)
    {
        Entry<Integer,TrueLivraison> e;
        if(monte)
        {
            e = livraisons.ceilingEntry(etageActuel);
        }
        else
        {
            e = livraisons.floorEntry(etageActuel);
        }
        if(e == null)
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(e.getValue());
        }
    }

    public boolean destinationCompatible(final AlgoPersGroup p)
    {
        return livraisons.containsKey(p.getDestination());
    }

    public void mergeLivraisons(final List<TrueLivraison> incominglivraisons)
    {
        for(final TrueLivraison tl : incominglivraisons)
        {
            placesDispo -= tl.nb;
            TrueLivraison t = livraisons.get(tl.destination);
            if(t==null)
            {
                t = tl;
            }
            else
            {
                t = t.merge(tl);
            }
            livraisons.put(t.destination,t);
        }
    }

    public void dumpDifference(final ContenuAsc refIncluseDansLesLivraisons, final Collection<AlgoPersonne> c)
    {
        for(final Entry<Integer,TrueLivraison> e : livraisons.entrySet())
        {
            final TrueLivraison reft = refIncluseDansLesLivraisons.livraisons.get(e.getKey());
            if(reft!=null)
            {
                e.getValue().dumpDifference(reft, c);
            }
            else
            {
                e.getValue().dump(c);
            }
        }
    }

    public boolean estPleins()
    {
        return placesDispo == 0;
    }

    public boolean contient(final int persDepart,final int persDestination, final int nb)
    {
        final TrueLivraison t = livraisons.get(persDestination);
        if(t == null)
        {
            return false;
        }
        else
        {
            if(t.nb != nb)
            {
                return false;
            }
            else
            {
                return t.contenu.stream().anyMatch(p -> p.depart == persDepart);
            }
        }
    }

    public void addMax(final AlgoPersGroup group)
    {
        add(group,Math.min(group.size(), placesDispo()));
    }

    public void noticeArriveEtage(final int etage)
    {
        final TrueLivraison t = livraisons.remove(etage);
        if(t != null)
        {
            placesDispo += t.nb;
        }
    }

    public int placesDispo()
    {
        return placesDispo;
    }

    public TrueLivraison add(final AlgoPersGroup group, final int nb)
    {
        placesDispo -= nb;
        final TrueLivraison aChanger = livraisons.get(group.getDestination());
        TrueLivraison newT = new TrueLivraison(group, nb);
        if(aChanger != null)
        {
            newT = aChanger.merge(newT);
        }
        livraisons.put(group.getDestination(),newT);
        return newT;
    }

    public boolean isEmpty()
    {
        return livraisons.isEmpty();
    }

    @Override
    public String toString()
    {
        return "places " + placesDispo + " livr " + livraisons.toString();
    }
}
