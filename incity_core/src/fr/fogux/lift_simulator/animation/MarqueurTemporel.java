package fr.fogux.lift_simulator.animation;

public class MarqueurTemporel
{
    protected final long time;

    public MarqueurTemporel(long time)
    {
        this.time = time;
    }

    public long intervale(long timeActuel)
    {
        return timeActuel - time;
    }

}
