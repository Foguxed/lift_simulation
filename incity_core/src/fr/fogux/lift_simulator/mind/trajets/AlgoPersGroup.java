package fr.fogux.lift_simulator.mind.trajets;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import fr.fogux.lift_simulator.utils.HashChainedList;

public class AlgoPersGroup implements Iterable<AlgoPersonne>
{


    private final int etage;
    private final int destination;
    private final HashChainedList<AlgoPersonne> contenu;

    public static final Comparator<AlgoPersGroup> sizeComparator =
        new Comparator<AlgoPersGroup>()
    {

        @Override
        public int compare(final AlgoPersGroup o1, final AlgoPersGroup o2)
        {
            return o1.size() - o2.size();
        }
    };

    public boolean monte()
    {
        return etage < destination;
    }

    public AlgoPersGroup(final AlgoPersGroup toShadow)
    {
        etage= toShadow.etage;
        destination = toShadow.destination;
        contenu = new HashChainedList<>(toShadow.contenu);
    }

    public AlgoPersGroup(final int etage, final int destination)
    {
        this.etage = etage;
        this.destination = destination;
        contenu = new HashChainedList<>();
    }

    public int getEtage()
    {
        return etage;
    }

    public int getDestination()
    {
        return destination;
    }

    public int size()
    {
        return contenu.size();
    }

    public void add(final AlgoPersonne p)
    {
        contenu.addFin(p);
    }

    public void remove(final AlgoPersonne p)
    {
        contenu.remove(p);
    }

    public boolean contains(final AlgoPersonne p)
    {
        return contenu.contains(p);
    }

    public boolean safeRemove(final AlgoPersonne p)
    {
        return contenu.safeRemove(p);
    }

    public AlgoPersGroup subGroup(int nb)
    {
        final AlgoPersGroup retour = new AlgoPersGroup(etage, destination);
        final Iterator<AlgoPersonne> iter = contenu.iterator();
        while(nb > 0)
        {
            retour.add(iter.next());
            nb --;
        }
        return retour;
    }

    public void dump(final Collection<AlgoPersonne> c, final int nbToDump)
    {
        contenu.dumpNFirst(c, nbToDump);
    }

    public boolean isEmpty()
    {
        //System.out.println(" isempty " + contenu.isEmpty());
        return contenu.isEmpty();
    }

    @Override
    public Iterator<AlgoPersonne> iterator()
    {
        return contenu.iterator();
    }

    @Override
    public String toString()
    {
        return "PersGroup etage " + etage + " dest " + destination + " size " + size();
    }

}
