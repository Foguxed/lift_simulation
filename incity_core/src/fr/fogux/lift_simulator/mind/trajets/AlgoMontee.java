package fr.fogux.lift_simulator.mind.trajets;

import java.util.List;
import java.util.function.Consumer;

public abstract class AlgoMontee<A extends AlgoAscenseur>
{
    public final List<A> ascenseurs;

    public AlgoMontee(final List<A> ascenseurs)
    {
        this.ascenseurs = ascenseurs;
    }

    public List<Integer> invites(final int niveau,final int stackId, final int placesDispo)
    {
        return ascenseurs.get(stackId).getInvites(niveau,placesDispo);
    }

    public void escaleTerminee(final int stackId)
    {
        ascenseurs.get(stackId).escaleTerminee();
    }

    public void init()
    {
        ascenseurs.stream().forEach(a -> a.init());
    }

    public void forEachAsc(final Consumer<A> consumer)
    {
        ascenseurs.stream().forEach(a -> consumer.accept(a));
    }
}
