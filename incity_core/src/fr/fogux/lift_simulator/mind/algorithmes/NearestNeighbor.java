package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.ascenseurs.AscPoolUser;
import fr.fogux.lift_simulator.mind.ascenseurs.NearestNeighborAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.IntEnsemble;
import fr.fogux.lift_simulator.utils.Utils;

public class NearestNeighbor extends PoolBroadcasterAlgo
{
    


    public NearestNeighbor(final OutputProvider phys, final ConfigSimu c)
    {
        super(phys, c, new IndepAscInstantiator() {

            @Override
            public NearestNeighborAsc getNewInstance(final AscId id, final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent) {
                return new NearestNeighborAsc(id, config, phys, ascPrecedent);
            }
        });
    }

    @Override
    protected List<BPersPool<AscPoolUser<?>>> formerPools()
    {
        return formerStandardLargesPools();
    }

    /**
     * toutes les montees doivent avoir le même nombre d'ascenseurs
     * @return
     */
    protected List<BPersPool<AscPoolUser<?>>> formerStandardLargesPools()
    {
        final List<BPersPool<AscPoolUser<?>>> list = new ArrayList<>();
        final List<List<AscPoolUser<?>>> lignesDascenseurs = new ArrayList<>();
        final int nbAscParMonte = config.getRepartAscenseurs()[0];
        for(final Montee<AscPoolUser<?>> m : montees)
        {
            for(int i = 0; i < m.ascenseurs.size();i++)
            {
                if(i >= lignesDascenseurs.size())
                {
                    lignesDascenseurs.add(new ArrayList<>());
                }
                lignesDascenseurs.get(i).add(m.ascenseurs.get(i));
            }
        }
        for(int i = 0; i < lignesDascenseurs.size(); i ++)
        {
            list.add(new SetPool(lignesDascenseurs.get(i), new IntEnsemble(config.getNiveauMin()+i, config.getNiveauMax()+1 - (nbAscParMonte - (i +1)))));
        }
        return list;
    }

    protected List<BPersPool<AscPoolUser<?>>> formerPoolsLunchtimeIncity()
    {
        final List<BPersPool<AscPoolUser<?>>> list = new ArrayList<>();
        final List<AscPoolUser<?>> ascenseursHaut = new ArrayList<>();
        final List<AscPoolUser<?>> ascenseursBas = new ArrayList<>();
        for(final Montee<AscPoolUser<?>> m : montees)
        {
            ascenseursBas.add(m.ascenseurs.get(0));
            ascenseursHaut.add(m.ascenseurs.get(1));
        }

        list.add(new SetPool(ascenseursBas, Utils.incityInferieur));
        list.add(new SetPool(ascenseursHaut, Utils.incitySuperieur));
        return list;
    }

}
