package fr.fogux.lift_simulator.structure;

public abstract class Personne
{
    protected final int destination;
    protected final int id;

    public Personne(final int destination, final int id)
    {
        this.destination = destination;
        this.id = id;
    }

    public int getDestination()
    {
        return destination;
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "id " + id + " destination " + destination;
    }
}
