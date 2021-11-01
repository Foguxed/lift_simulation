package fr.fogux.lift_simulator.mind.trajets;

import java.util.Comparator;
import java.util.function.Predicate;

public class AlgoPersonne
{
    public static final Predicate<AlgoPersonne> MONTE = (p -> p.monte());
    public static final Predicate<AlgoPersonne> DESCEND = (p -> p.descend());

    public static Predicate<AlgoPersonne> getFiltreSens(final boolean monte)
    {
        if(monte)
        {
            return MONTE;
        }
        else
        {
            return DESCEND;
        }
    }

    public boolean monte()
    {
        return destination > depart;
    }


    public boolean descend()
    {
        return destination < depart;
    }

    public static final Comparator<AlgoPersonne> destinationComparator =
        new Comparator<AlgoPersonne>()
    {
        @Override
        public int compare(final AlgoPersonne arg0, final AlgoPersonne arg1)
        {
            return arg0.destination - arg1.destination;
        }
    };

    public static final Comparator<AlgoPersonne> departComparator =
        new Comparator<AlgoPersonne>()
    {
        @Override
        public int compare(final AlgoPersonne arg0, final AlgoPersonne arg1)
        {
            return arg0.depart - arg1.depart;
        }
    };

    public final int id;
    public final int destination;
    public final int depart;

    public static Predicate<AlgoPersonne> filterDestination(final Predicate<Integer> predicateEtage)
    {
        return new Predicate<AlgoPersonne>() {

            @Override
            public boolean test(final AlgoPersonne p)
            {
                return predicateEtage.test(p.destination);
            }};
    }
    public static Predicate<AlgoPersonne> filterDepart(final Predicate<Integer> predicateEtage)
    {
        return new Predicate<AlgoPersonne>() {

            @Override
            public boolean test(final AlgoPersonne p)
            {
                return predicateEtage.test(p.depart);
            }};
    }



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
