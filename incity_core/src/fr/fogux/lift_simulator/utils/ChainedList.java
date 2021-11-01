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

    public Maillon<obj> addFin(final obj value)
    {
        final Maillon<obj> nouveauMaillon = new Maillon<>(value);
        if (isEmpty())
        {
            premier = nouveauMaillon;
        } else
        {
            dernier.suivant = nouveauMaillon;
            nouveauMaillon.precedent = dernier;
        }
        dernier = nouveauMaillon;
        return nouveauMaillon;
    }

    public Maillon<obj> addDebut(final obj value)
    {
        final Maillon<obj> nouveauMaillon = new Maillon<>(value);
        if (isEmpty())
        {
            dernier = nouveauMaillon;
        } else
        {
            premier.precedent = nouveauMaillon;
            nouveauMaillon.suivant = premier;
        }
        premier = nouveauMaillon;
        return nouveauMaillon;
    }

    public void removeFirst()
    {
        premier = premier.suivant;
    }

    public void removeLast()
    {
        dernier = dernier.precedent;
    }

    public void remove(final Maillon<obj> m)
    {
        if(premier == m)
        {
            premier = m.suivant;
        }
        if(dernier == m)
        {
            dernier = m.precedent;
        }
        if(m.getSuivant() != null)
        {
            m.getSuivant().precedent = m.getPrecedent();
        }
        if(m.getPrecedent() != null)
        {
            m.getPrecedent().suivant = m.getSuivant();
        }
    }

    public void cutListToEnd(final Maillon<obj> newFirstMaillon)
    {
        newFirstMaillon.precedent = null;
        premier = newFirstMaillon;
    }

    public void cutListToStart(final Maillon<obj> newLastMaillon)
    {
        newLastMaillon.suivant = null;
        dernier = newLastMaillon;
    }

    public Maillon<obj> getFirst()
    {
        return premier;
    }

    public void addAll(final Iterable<obj> objs)
    {
        if (dernier == null)
        {
            final obj ev = objs.iterator().next();
            addFin(ev);
        }
        Maillon<obj> precedent = dernier;
        for (final obj o : objs)
        {
            final Maillon<obj> maillon = new Maillon<>(precedent, o);
            precedent.suivant = maillon;
            precedent = maillon;
        }
        dernier = precedent;
    }

    @Override
    public Iterator<obj> iterator()
    {
        return new ChainedIterator<>(premier);
    }

    class ChainedIterator<T extends Object> implements Iterator<T>
    {
        protected Maillon<T> maillon;

        public ChainedIterator(final Maillon<T> premier)
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
            final T val = maillon.getValue();
            maillon = maillon.suivant;
            return val;
        }
    }
}
