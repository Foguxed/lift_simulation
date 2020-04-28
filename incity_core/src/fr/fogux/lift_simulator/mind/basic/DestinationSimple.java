package fr.fogux.lift_simulator.mind.basic;

import java.util.ArrayList;
import java.util.List;

public class DestinationSimple
{
    public final int niveau;
    public final List<Integer> invites;
    public DestinationSimple(final int niveau)
    {
        this.niveau = niveau;
        invites = new ArrayList<>();
    }
    public DestinationSimple(final int niveau, final int persId)
    {
        this(niveau);
        invites.add(persId);
    }


}
