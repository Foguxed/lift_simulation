package fr.fogux.lift_simulator.utils;

public class Ref<A>
{
    protected A v;
    public Ref(final A v)
    {
        this.v = v;
    }

    public void set(final A v)
    {
        this.v = v;
    }

    public A get()
    {
        return v;
    }
}
