package fr.fogux.lift_simulator.utils;

import java.util.Iterator;

public class IteratorAndThen<A> implements Iterator<A>
{
    protected A element;
    protected boolean fired;
    private final Iterator<A> suite;

    public IteratorAndThen(final A element,final Iterator <A> suite)
    {
        this.element = element;
        this.suite = suite;
        fired = false;
    }


    @Override
    public boolean hasNext()
    {
        return !fired || suite.hasNext();
    }

    @Override
    public A next()
    {
        if(fired)
        {
            return suite.next();
        }
        else
        {
            fired = true;
            return element;
        }
    }

}
