package fr.fogux.lift_simulator.mind.pool;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.mind.trajets.AlgoEtage;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.IntEnsemble;

public class IdByEtagePool extends BIdPersPool
{
    protected final TreeMap<Integer,AlgoEtage> etages;
    protected int margeBorneClients;
    protected int nbPersonnes = 0;

    public final int nbEtages;
    public final int distanceTotale;



    public IdByEtagePool(final IntEnsemble etagesAutorises, final List<AscId> ascsids)
    {
        super(ascsids);
        etages = fromIntEnsemble(etagesAutorises);
        margeBorneClients = 1;
        nbEtages = etages.size();
        distanceTotale = (etages.lastKey() - etages.firstKey());
    }



    public AlgoEtage getEtage(final int etage)
    {
        return etages.get(etage);
    }

    @Override
    public void addToPool(final AlgoPersonne newPers)
    {
        etages.get(newPers.depart).add(newPers);
        nbPersonnes ++;
    }

    @Override
    public void removeFromPool(final AlgoPersonne pers)
    {
        etages.get(pers.depart).remove(pers);
        nbPersonnes --;
    }

    @Override
    public boolean couldAccept(final AlgoPersonne newPers)
    {
        return etages.containsKey(newPers.depart) && etages.containsKey(newPers.destination);
    }

    public AlgoPersGroup getGroup(final AlgoPersonne pers)
    {
        return etages.get(pers.depart).getGroup(pers);
    }

    public Stream<AlgoPersGroup> getFullGroupStream(final boolean ascendant)
    {
        final SortedMap<Integer,AlgoEtage> subMap = ascendant ? etages : etages.descendingMap();
        final Iterator<Entry<Integer,AlgoEtage>> iter = subMap.entrySet().iterator();
        AlgoEtage e;
        e = iter.next().getValue();
        Stream<AlgoPersGroup> str = Stream.concat(e.getPersGroupStream(true),e.getPersGroupStream(false));
        while(iter.hasNext())
        {
            e = iter.next().getValue();
            str = Stream.concat(str,Stream.concat(e.getPersGroupStream(true),e.getPersGroupStream(false)));
        }
        return str;

    }

    public Stream<AlgoPersGroup> getGroupStream( final boolean monte, final boolean ascendant)
    {
        final SortedMap<Integer,AlgoEtage> subMap = ascendant ? etages : etages.descendingMap();

        return fromSortedMap(subMap,monte);
    }


    public Stream<AlgoPersGroup> getGroupStream(final int fromKeyIncluded, final int toKeyExcluded, final boolean monte)
    {
        final SortedMap<Integer,AlgoEtage> subMap = (fromKeyIncluded < toKeyExcluded) ? etages.subMap(fromKeyIncluded, toKeyExcluded) : etages.descendingMap().subMap(fromKeyIncluded,toKeyExcluded);
        return fromSortedMap(subMap,monte);
    }

    protected static final Stream<AlgoPersGroup> fromSortedMap(final SortedMap<Integer,AlgoEtage> subMap, final boolean monte)
    {
        if(subMap.isEmpty())
        {
            return Stream.empty();
        }
        final Iterator<Entry<Integer,AlgoEtage>> iter = subMap.entrySet().iterator();
        Stream<AlgoPersGroup> str = iter.next().getValue().getPersGroupStream(monte);
        while(iter.hasNext())
        {
            str = Stream.concat(str, iter.next().getValue().getPersGroupStream(monte));
        }
        return str;
    }

    protected TreeMap<Integer,AlgoEtage> fromIntEnsemble(final IntEnsemble intens)
    {
        final TreeMap<Integer,AlgoEtage> map = new TreeMap<>();
        intens.forEach(i -> map.put(i, new AlgoEtage(i)));
        return map;
    }



    @Override
    public boolean contains(final AlgoPersonne p)
    {
        final AlgoEtage e = etages.get(p.depart);
        return e != null && e.contains(p);
    }

    @Override
    public String toString()
    {

        String total = super.toString() + " firstkey " + etages.firstKey()+" lastKey "+ etages.lastKey() + " contenuEtages : (";
        for(final AlgoEtage et : etages.values())
        {
            if(!et.isEmpty())
            {
                total = total + et;
            }
        }
        return total + ")";
    }
}
