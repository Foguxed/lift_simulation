package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import fr.fogux.lift_simulator.mind.ascenseurs.AscPoolUser;
import fr.fogux.lift_simulator.mind.trajets.AlgoImmeuble;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class PoolBroadcasterAlgo extends AlgoImmeuble<AscPoolUser<?>>
{
    protected final List<BPersPool<AscPoolUser<?>>> pools;

    public PoolBroadcasterAlgo(final OutputProvider phys, final ConfigSimu c, final IndepAscInstantiator instantiator)
    {
        super(getMontees(phys, c, instantiator), phys, c);
        pools = formerPools();
    }

    protected abstract List<BPersPool<AscPoolUser<?>>> formerPools();

    @Override
    protected int algInit()
    {
        for(final Montee<AscPoolUser<?>> m : montees)
        {
            m.forEachAsc(a -> a.registerOwner(this));
        }
        return -1;
    }

    @Override
    public void ping()
    {

    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        final AlgoPersonne newPers = new AlgoPersonne(idPersonne, niveau, destination);
        final HashSet<AscPoolUser<?>> ascAUpdate = new LinkedHashSet<>();
        for(final BPersPool<AscPoolUser<?>> pool : pools)
        {
            if(pool.couldAccept(newPers))
            {
                pool.addToPool(newPers);
                ascAUpdate.addAll(pool.getAscConcernes());
            }
        }
        for(final AscPoolUser<?> asc : ascAUpdate)
        {
            asc.poolHasBeenUpdated();
        }
    }

    /**
     * A appeller depuis les ascenseurs
     * @param p
     */
    public void prisEnCharge(final AlgoPersonne p)
    {
        for(final PersPool pool : pools)
        {
            pool.removeFromPool(p);
        }
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

}
