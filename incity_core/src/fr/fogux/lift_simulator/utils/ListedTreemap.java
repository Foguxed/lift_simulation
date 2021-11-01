package fr.fogux.lift_simulator.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class ListedTreemap<K,V> implements Iterable<V>
{
    public final TreeMap<K,Set<V>> treemap;
    public int size = 0;

    public ListedTreemap()
    {
        treemap = new TreeMap<>();
    }

    public void put(final K key, final V value)
    {
        Set<V> lis = treemap.get(key);
        if(lis != null)
        {
            if(lis.add(value))
            {
                size ++;
            }
        }
        else
        {
            lis = new HashSet<>(1);
            size ++;
            lis.add(value);
            treemap.put(key,lis);
        }
    }

    public boolean remove(final K key, final V value)
    {
        final Set<V> lis = treemap.get(key);
        if(lis != null)
        {
            final boolean b = lis.remove(value);
            if(b)
            {
                size --;
            }
            if(lis.isEmpty())
            {
                treemap.remove(key);
            }
            return b;
        }
        return false;
    }

    public V pollAnyFirst()
    {
        final Set<V> s = treemap.firstEntry().getValue();
        size --;
        final V v = s.iterator().next();
        s.remove(v);
        if(s.isEmpty())
        {
            treemap.pollFirstEntry();
        }
        return v;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return treemap.isEmpty();
    }

    public V getAnyFirst()
    {
        return treemap.firstEntry().getValue().iterator().next();
    }

    @Override
    public Iterator<V> iterator()
    {
        return new ListedTreeMapIterator<>(this,true);
    }
}
