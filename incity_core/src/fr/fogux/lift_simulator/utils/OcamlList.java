package fr.fogux.lift_simulator.utils;

public class OcamlList<E>
{
    public final E head;
    public final OcamlList<E> queue;

    public OcamlList()
    {
        head = null;
        queue = null;
    }

    private OcamlList(final E head, final OcamlList<E> queue)
    {
        this.head = head;
        this.queue = queue;
    }

    public OcamlList<E> add(final E element)
    {
        return new OcamlList<>(element,this);
    }

    public boolean isEmpty()
    {
        return queue == null;
    }

    @Override
    public String toString()
    {
        if(isEmpty())
        {
            return "()";
        }
        else
        {
            return head + "->" + queue;
        }
    }

    public OcamlList<E> reverse()
    {
        return innerReverse(this,new OcamlList<E>());
    }

    private static <C> OcamlList<C> innerReverse(final OcamlList<C> reversed, final OcamlList<C> retour)
    {
        if(reversed.isEmpty())
        {
            return retour;
        }
        else
        {
            return innerReverse(reversed.queue,retour.add(reversed.head));
        }
    }
}
