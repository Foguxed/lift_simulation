package fr.fogux.lift_simulator.utils;

public interface OcamlList<E>
{
    OcamlList<E> add(final E element);
    boolean isEmpty();
    E getHead();
    OcamlList<E> getQueue();

    default E lastItem()
    {
        OcamlList<E> c = this;
        E last = null;
        while(!c.isEmpty())
        {
            last = c.getHead();
            c = c.getQueue();
        }
        return last;
    }
}
