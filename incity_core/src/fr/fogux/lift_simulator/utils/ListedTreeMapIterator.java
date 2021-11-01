package fr.fogux.lift_simulator.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ListedTreeMapIterator<K,T> implements Iterator<T>
{
    protected ListedTreemap<K, T> map;
    protected Iterator<Entry<K,Set<T>>> masterIterator;
    protected Iterator<T> currentSetIterator = new Iterator<T>() {

        @Override
        public boolean hasNext()
        {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    };


    public ListedTreeMapIterator(final ListedTreemap<K, T> map, final boolean ascending)
    {
        this.map = map;
        if(ascending)
        {
            masterIterator = map.treemap.entrySet().iterator();
        }
        else
        {
            masterIterator = map.treemap.descendingMap().entrySet().iterator();
        }
    }

    @Override
    public boolean hasNext()
    {
        final boolean b = currentSetIterator.hasNext();
        if(b)
        {
            return true;
        }
        else
        {
            return masterIterator.hasNext();
        }
    }

    @Override
    public T next()
    {
        final boolean b = currentSetIterator.hasNext();
        if(b)
        {
            return currentSetIterator.next();
        }
        else
        {
            currentSetIterator = masterIterator.next().getValue().iterator();
            return currentSetIterator.next();
        }
    }

}
