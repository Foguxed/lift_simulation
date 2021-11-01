package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.algorithmes.PersPool;
import fr.fogux.lift_simulator.mind.algorithmes.PoolBroadcasterAlgo;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class AscPoolUser<P extends PersPool> extends AlgoIndependentAsc
{
    protected List<P> pools;
    protected PoolBroadcasterAlgo mainAlgo;

    public AscPoolUser(final AscId id, final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(id, config, outputProvider, ascPrecedent);
        pools = new ArrayList<>();
    }

    public void registerOwner(final PoolBroadcasterAlgo mainAlgo)
    {
        this.mainAlgo = mainAlgo;
    }


    public void registerPool(final P pool)
    {
        pools.add(pool);
    }

    @SuppressWarnings("unchecked")
    public void rawregisterPool(final PersPool pool)
    {
        pools.add((P)pool);
    }

    protected void prendreEnCharge(final AlgoPersonne p)
    {
        mainAlgo.prisEnCharge(p);
    }

    public abstract void poolHasBeenUpdated();
}
