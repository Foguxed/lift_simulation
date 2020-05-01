package fr.fogux.lift_simulator.mind.independant;

public class AlgoPersonne implements Comparable<AlgoPersonne>
{
    public final int id;
    public final int destination;
    public final int depart;

    public AlgoPersonne(final int id, final int destination, final int depart)
    {
        this.id = id;
        this.destination = destination;
        this.depart = depart;
    }



    @Override
    public int compareTo(final AlgoPersonne o)
    {
        return id + destination - o.id - o.destination;
    }


}
