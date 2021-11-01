package fr.fogux.lift_simulator.mind.trajets;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class AlgoEtage
{
    protected Map<Integer,AlgoPersGroup> enMontee = new TreeMap<>();
    protected Map<Integer,AlgoPersGroup> enDescente = new TreeMap<>();

    public static final Function<Entry<Integer,AlgoPersGroup>,AlgoPersGroup> f = new Function<Map.Entry<Integer,AlgoPersGroup>, AlgoPersGroup>()
    {

        @Override
        public AlgoPersGroup apply(final Entry<Integer, AlgoPersGroup> t)
        {
            return t.getValue();
        }
    };

    protected final int etage;


    public AlgoEtage(final int etage)
    {
        this.etage = etage;
    }

    public void add(final AlgoPersonne p)
    {
        if(p.monte())
        {
            addToSet(p,enMontee);
        }
        else
        {
            addToSet(p,enDescente);
        }
    }

    public Stream<AlgoPersGroup> getPersGroupStream(final boolean monte)
    {
        if(monte)
        {
            return enMontee.entrySet().stream().map(f);
        }
        else
        {
            return enDescente.entrySet().stream().map(f);
        }
    }

    public AlgoPersGroup getGroup(final AlgoPersonne p)
    {
        if(p.monte())
        {
            return enMontee.get(p.destination);
        }
        else
        {
            return enDescente.get(p.destination);
        }
    }

    public void remove(final AlgoPersonne p)
    {
        if(p.monte())
        {
            removeFromMap(p, enMontee);
        }
        else
        {
            removeFromMap(p, enDescente);
        }
    }

    public boolean contains(final AlgoPersonne p)
    {
        if(p.monte())
        {
            return enMontee.containsKey(p.destination) && enMontee.get(p.destination).contains(p);
        }
        else
        {
            return enDescente.containsKey(p.destination) && enDescente.get(p.destination).contains(p);
        }
    }

    protected AlgoPersGroup addToSet(final AlgoPersonne p, final Map<Integer,AlgoPersGroup> map)
    {
        final AlgoPersGroup g = map.get(p.destination);
        if(g != null)
        {
            g.add(p);
        }
        else
        {
            final AlgoPersGroup newGroup = new AlgoPersGroup(etage,p.destination);
            newGroup.add(p);
            map.put(p.destination, newGroup);
        }
        return g;
    }

    protected void removeFromMap(final AlgoPersonne p, final Map<Integer,AlgoPersGroup> map)
    {
        final AlgoPersGroup g = map.get(p.destination);
        g.remove(p);
        if(g.isEmpty())
        {
            map.remove(p.destination);
        }
    }

    @Override
    public String toString()
    {
        return " etage " + etage + " ( haut " + enMontee  + " bas " + enDescente + ")";
    }

    public boolean isEmpty()
    {
        return enMontee.isEmpty() && enDescente.isEmpty();
    }
}
