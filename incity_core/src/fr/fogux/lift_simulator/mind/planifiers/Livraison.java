package fr.fogux.lift_simulator.mind.planifiers;

import java.util.Comparator;

public class Livraison
{
    public final int destination;
    public final int nb;

    public static final Comparator<Livraison> DESTINATION_COMPARATOR = new Comparator<Livraison>()
    {

        @Override
        public int compare(final Livraison o1, final Livraison o2)
        {
            return o1.destination - o2.destination;
        }
    };

    public Livraison(final int destination, final int nb)
    {
        this.destination = destination;
        this.nb = nb;
    }


    @Override
    public String toString()
    {
        return "Livr d=" + destination + " n="+nb;
    }
}
