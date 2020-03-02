package fr.fogux.lift_simulator.structure;

public abstract class Personne
{
    protected final int destination;
    protected final int id;

    public Personne(int destination, int id)
    {
        this.destination = destination;
        this.id = id;
    }

    public int getDestination()
    {
        return destination;
    }

}
