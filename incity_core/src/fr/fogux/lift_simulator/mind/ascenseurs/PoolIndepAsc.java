package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;
import fr.fogux.lift_simulator.mind.algorithmes.PersPool;
import fr.fogux.lift_simulator.mind.algorithmes.PoolFillerAlgo;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class PoolIndepAsc<P extends IdAscPersPool,Alg extends PoolFillerAlgo<P,?>> extends AlgoIndependentAsc
{

    protected Alg alg;
    protected List<P> pools = new ArrayList<>();

    public PoolIndepAsc(final AlgoIndependentAsc shadowed,final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(shadowed,config,outputProvider,ascPrecedent);
        
    }

    public PoolIndepAsc(final AscId id, final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(id, config, outputProvider, ascPrecedent);
    }

    public void registerPool(P pool)
    {
    	pools.add(pool);
    }
    
    public void registerAlgo(final PoolFillerAlgo alg)
    {
        this.alg = (Alg)alg;
    }


    public abstract List<AlgoPersonne> getPersInvites(int niveau, int placesDispo);

    @Override
    public List<Integer> getInvites(final int niveau, final int placesDispo)
    {
        final List<AlgoPersonne> l = getPersInvites(niveau,placesDispo);
        if(l!=null)
        {
            final List<Integer> returnList = new ArrayList<>();
            for(final AlgoPersonne p : l)
            {
                returnList.add(p.id);
            }
            return returnList;
        }
        else
        {
            return null;
        }
    }

}
