package fr.fogux.lift_simulator.utils;

import java.util.Iterator;
import java.util.Stack;

public class PrefixeArbreIterator<E> implements Iterator<Arbre<E>>
{
    protected Stack<Arbre<E>> stack = new Stack<>();

    public PrefixeArbreIterator(final Arbre<E> arbre)
    {
        stack.add(arbre);
    }

    @Override
    public boolean hasNext()
    {
        return !stack.isEmpty();
    }

    @Override
    public Arbre<E> next()
    {
        final Arbre<E> a = stack.pop();
        stack.addAll(a.fils);
        return a;
    }

}
