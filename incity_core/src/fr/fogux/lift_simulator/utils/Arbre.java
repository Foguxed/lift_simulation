package fr.fogux.lift_simulator.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Arbre<E> implements OcamlList<E>,Iterable<Arbre<E>>
{
    protected E noeud;
    protected Arbre<E> pere;
    protected List<Arbre<E>> fils;



    public Arbre()
    {
        fils = new ArrayList<>(2);
    }

    /**
     *
     * @param pere ne doit pas être null
     * @param noeud
     */
    private Arbre(final Arbre<E> pere, final E noeud)
    {
        this();
        this.noeud = noeud;
        this.pere = pere;
    }

    public List<Arbre<E>> getFils()
    {
        return fils;
    }

    public void setHead(final E v)
    {
        this.noeud = v;
    }

    public boolean estRacine()
    {
        return pere == null;
    }

    public void popFromPere()
    {
        pere.fils.remove(this);
        pere = null;
    }

    @Override
    public Arbre<E> add(final E element)
    {
        final Arbre<E> newf = new Arbre<>(this,element);
        fils.add(newf);
        return newf;
    }

    @Override
    public boolean isEmpty()
    {
        return estRacine();
    }

    public boolean estFeuille()
    {
        return fils.isEmpty();
    }

    @Override
    public E getHead()
    {
        return noeud;
    }

    @Override
    public Arbre<E> getQueue()
    {
        return pere;
    }

    public void stripBranch()
    {
        if(estRacine())
        {
            fils.clear();
        }
        else
        {
            if(pere.fils.size() > 1)
            {
                pere.fils.remove(this);
            }
            else
            {
                pere.stripBranch();
            }
        }

    }

    @Override
    public Iterator<Arbre<E>> iterator()
    {
        return new PrefixeArbreIterator<>(this);
    }

    public int countNbFeuilles()
    {
        int n = 0;
        for(final Arbre<E> a : this)
        {
            if(a.estFeuille())
            {
                n ++;
            }
        }
        return n;
    }

    public Arbre<E> getRacine()
    {
        Arbre<E> r = this;
        while(!r.estRacine())
        {
            r = r.getQueue();
        }
        return r;
    }


    public String toString(final int nb)
    {
        if(nb <= 0)
        {
            return "";
        }

        return " v: " + noeud  + " pere " + (pere == null ? null : pere.toString(nb-1));
    }
}
