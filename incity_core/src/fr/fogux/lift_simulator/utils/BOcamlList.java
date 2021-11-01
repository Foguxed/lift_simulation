package fr.fogux.lift_simulator.utils;

import java.util.function.Function;

public class BOcamlList<E> implements OcamlList<E>
{
    public final E head;
    public final BOcamlList<E> queue;

    public BOcamlList()
    {
        head = null;
        queue = null;
    }

    private BOcamlList(final E head, final BOcamlList<E> queue)
    {
        this.head = head;
        this.queue = queue;
    }

    @Override
    public BOcamlList<E> add(final E element)
    {
        return new BOcamlList<>(element,this);
    }

    @Override
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

    public BOcamlList<E> reverse()
    {
        return innerReverse(this,new BOcamlList<E>());
    }

    private static <C> BOcamlList<C> innerReverse(final BOcamlList<C> reversed, final BOcamlList<C> retour)
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
    
    public static <E,B> BOcamlList<B> map(OcamlList<E> l,Function<E,B> f)
    {
    	return innerMap(f,new BOcamlList<>(),l).reverse();
    }
    
    private static <E,B> BOcamlList<B> innerMap(Function<E,B> f,BOcamlList<B> retour, OcamlList<E> l)
    {
    	if(l.isEmpty())
    	{
    		return retour;
    	}
    	else
    	{
    		return innerMap(f,retour.add(f.apply(l.getHead())),l.getQueue());
    	}
    }

    @Override
    public E getHead()
    {
        return head;
    }

    @Override
    public BOcamlList<E> getQueue()
    {
        return queue;
    }
}
