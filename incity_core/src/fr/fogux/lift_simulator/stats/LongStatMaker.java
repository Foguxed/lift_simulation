package fr.fogux.lift_simulator.stats;

public class LongStatMaker
{
    protected int count = 0;
    protected long total = 0;
    protected long sommeDesCarres = 0;
    protected long maximum = Long.MIN_VALUE;


    public void registerVal(final long val)
    {
        count ++;
        total += val;
        sommeDesCarres += val*val;
        if(val > maximum)
        {
            maximum = val;
        }
    }

    public void addAllStats(final LongStatMaker statMaker)
    {
        count += statMaker.count;
        total += statMaker.total;
        if(statMaker.maximum > maximum)
        {
            maximum = statMaker.maximum;
        }
        sommeDesCarres += statMaker.sommeDesCarres;
    }

    public LongStats produceLongStats()
    {
        final long moyenne = total/count;
        final long variance = sommeDesCarres/count + moyenne*moyenne;
        return new LongStats(moyenne, variance, maximum);
    }

    public int getCount()
    {
        return count;
    }

    public long getTotal()
    {
        return total;
    }

    public long getMaximum()
    {
        return maximum;
    }

}
