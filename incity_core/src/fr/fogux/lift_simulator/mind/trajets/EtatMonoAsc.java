package fr.fogux.lift_simulator.mind.trajets;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EtatMonoAsc extends EtatContenuAsc
{
    protected int ei;

    public EtatMonoAsc(final int etatInitial)
    {
        this(new HashSet<AlgoPersonne>(),new HashSet<AlgoPersonne>(), etatInitial);
    }

    private EtatMonoAsc(final Set<AlgoPersonne> aDelivrer, final Set<AlgoPersonne> contenuAsc, final int ascEtage)
    {
        super(aDelivrer,contenuAsc);
        ei = ascEtage;
    }

    public EtatMonoAsc(final EtatMonoAsc cloned)
    {
        super(cloned);
        ei = cloned.ei;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(aDelivrer,contenuAsc,ei);
    }

    @Override
    public boolean equals(final Object o)
    {
        if(o instanceof EtatMonoAsc)
        {
            final EtatMonoAsc t = (EtatMonoAsc)o;
            return ei == t.ei && aDelivrer.equals(t.aDelivrer) && contenuAsc.equals(t.contenuAsc);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return "{ei " + ei + super.toString() + "}";
    }

    public void setEtage(final int etage)
    {
        ei = etage;
    }

    public int getNiveau()
    {
        return ei;
    }
}
