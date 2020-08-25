package fr.fogux.lift_simulator.mind.trajets;


public class Escale
{
    public final int etage;
    public long todeleteTime;
    public final AlgoPersonne invite;


    public Escale(final int etage, final AlgoPersonne invite, final long t)
    {
        this.etage = etage;
        this.invite = invite;
        todeleteTime = t;
    }

    public Escale(final int etage, final AlgoPersonne invite)
    {
        this.etage = etage;
        this.invite = invite;
    }

    @Override
    public String toString()
    {
        return "etage " + etage + " invite " + (invite != null) + " t " + todeleteTime;
    }
}
