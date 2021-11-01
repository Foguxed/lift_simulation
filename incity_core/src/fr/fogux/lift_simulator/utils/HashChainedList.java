package fr.fogux.lift_simulator.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HashChainedList<A>
{
    protected ChainedList<A> clist;
    protected Map<A,Maillon<A>> map;


    public HashChainedList(final HashChainedList<A> toCpy)
    {
        this();
        for(final A v : toCpy.clist)
        {
            addFin(v);
        }
    }

    public HashChainedList()
    {
        this.clist = new ChainedList<>();
        this.map = new HashMap<>();
    }

    public void remove(final A obj)
    {
        if(map.get(obj) == null)
        {
            System.out.println("pers probleme " + obj);
        }
        clist.remove(map.get(obj));
        map.remove(obj);
    }

    public boolean contains(final A obj)
    {
        return map.containsKey(obj);
    }

    public boolean safeRemove(final A obj)
    {
        if(map.containsKey(obj))
        {
            remove(obj);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void addFin(final A obj)
    {
        final Maillon<A> m = clist.addFin(obj);
        map.put(obj, m);
    }

    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return clist.isEmpty();
    }

    public void dumpNFirst(final Collection<A> collection,int nb)
    {
        final Iterator<A> iter = clist.iterator();
        while(nb > 0)
        {
            collection.add(iter.next());
            nb --;
        }
    }

    public Iterator<A> iterator()
    {
        return clist.iterator();
    }
}
