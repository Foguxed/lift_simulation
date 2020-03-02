package fr.fogux.lift_simulator.utils;

import java.util.Iterator;

public class ChainedList<obj extends Object> implements Iterable<obj>
{
    protected Maillon<obj> premier;
    protected Maillon<obj> dernier;

    public ChainedList()
    {
        premier = null;
        dernier = null;
    }

    public boolean isEmpty()
    {
        return premier == null;
    }

    public void addFin(obj value)
    {
        final Maillon<obj> nouveauMaillon = new Maillon<obj>(value);
        if (isEmpty())
        {
            premier = nouveauMaillon;
        } else
        {
            dernier.suivant = nouveauMaillon;
        }
        dernier = nouveauMaillon;
    }

    public void addDebut(obj value)
    {
        final Maillon<obj> nouveauMaillon = new Maillon<obj>(value);
        if (isEmpty())
        {
            dernier = nouveauMaillon;
        } else
        {
            premier.precedent = nouveauMaillon;
            nouveauMaillon.suivant = premier;
        }
        premier = nouveauMaillon;
    }

    public void removeFirst()
    {
        premier = premier.suivant;
    }

    public void removeLast()
    {
        dernier = dernier.precedent;
    }

    public void subListToEnd(Maillon<obj> newFirstMaillon)
    {
        newFirstMaillon.precedent = null;
        premier = newFirstMaillon;
    }

    public void subListToStart(Maillon<obj> newLastMaillon)
    {
        newLastMaillon.suivant = null;
        dernier = newLastMaillon;
    }

    public Maillon<obj> getFirst()
    {
        return premier;
    }

    public void addAll(Iterable<obj> objs)
    {

        if (dernier == null)
        {
            obj ev = objs.iterator().next();
            addFin(ev);
        }
        Maillon<obj> precedent = dernier;
        for (obj o : objs)
        {
            Maillon<obj> maillon = new Maillon<obj>(precedent, o);
            precedent.suivant = maillon;
            precedent = maillon;
        }
        dernier = precedent;
    }

    public Iterator<obj> iterator()
    {
        return new ChainedIterator<obj>(premier);
    }

    class ChainedIterator<T extends Object> implements Iterator<T>
    {
        protected Maillon<T> maillon;

        public ChainedIterator(Maillon<T> premier)
        {
            this.maillon = premier;
        }

        @Override
        public boolean hasNext()
        {
            return maillon != null;
        }

        @Override
        public T next()
        {
            T val = maillon.getValue();
            maillon = maillon.suivant;
            return val;
        }
    }
}
