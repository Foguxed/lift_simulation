package fr.fogux.lift_simulator.evenements;

public class EventPingWithData<A> extends EvenementSimplePing
{
    public final A data;
    public EventPingWithData(final long time, final A data)
    {
        super(time);
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "Ping data " + data;
    }
}
