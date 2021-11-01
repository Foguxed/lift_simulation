package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;

import fr.fogux.lift_simulator.mind.ascenseurs.PoolIndepAsc;
import fr.fogux.lift_simulator.mind.trajets.AlgoImmeuble;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public abstract class PoolFillerAlgo<P extends IdAscPersPool,A extends PoolIndepAsc<P,?>> extends AlgoImmeuble<A>
{
    protected final List<P> pools;

    public PoolFillerAlgo(final OutputProvider phys, final ConfigSimu c, final IndepAscInstantiator<A> instantiator, final List<P> pools)
    {
        super(phys,c,instantiator);
        this.pools = pools;
        initPoolAsc();
    }

    public PoolFillerAlgo(final PoolFillerAlgo<P,A> toShadow,final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> instantiator, final List<P> pools)
    {
        super(toShadow, phys, c, instantiator);
        this.pools = pools;
        initPoolAsc();
    }

    private void initPoolAsc()
    {
        pools.stream().forEach(p -> p.getAscs().stream().forEach(id -> getAsc(id).registerPool(p)));
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        final AlgoPersonne newPers = new AlgoPersonne(idPersonne, niveau, destination);
        for(final PersPool pool : pools)
        {
            if(pool.couldAccept(newPers))
            {
                pool.addToPool(newPers);
            }
        }
    }

    /**
     * A appeller depuis les ascenseurs
     * @param p
     */
    public void prisEnCharge(final AlgoPersonne p)
    {
        for(final P pool : pools)
        {
            pool.removeFromPool(p);
        }
    }

    @Override
    public String toString()
    {
        return "pools " + pools + " " + super.toString();
    }
}
