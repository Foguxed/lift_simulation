package fr.fogux.lift_simulator.stats;

public class LongStats
{
    public final long moyenne;
    public final long variance;
    public final long max;


    public LongStats(final long moyenne, final long variance, final long max)
    {
        this.moyenne = moyenne;
        this.variance = variance;
        this.max = max;
    }

    public long getEcartType()
    {
        return (long)Math.sqrt(variance);
    }

    public String toString(final String separator)
    {
        return moyenne + separator + getEcartType() + separator + max + separator;
    }
}
