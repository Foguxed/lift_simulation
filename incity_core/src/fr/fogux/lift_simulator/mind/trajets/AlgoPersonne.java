package fr.fogux.lift_simulator.mind.trajets;

public class AlgoPersonne
{
    public final int id;
    public final int destination;
    public final int depart;

    public AlgoPersonne(final int id, final int depart, final int destination)
    {
        this.id = id;
        this.destination = destination;
        this.depart = depart;
    }

    @Override
    public String toString()
    {
        return "personne: dest = " + destination + " depart = " + depart + " id = " + id;
    }
}
