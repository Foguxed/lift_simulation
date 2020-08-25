package fr.fogux.lift_simulator.mind.independant;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public interface IndepAscInstantiator
{
    AlgoIndependentAsc getNewInstance(final AscId id,final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent);

    IndepAscInstantiator CYCLIQUE = new IndepAscInstantiator()
    {
        @Override
        public AlgoIndependentAsc getNewInstance(final AscId id, final ConfigSimu config, final OutputProvider phys,
            final VoisinAsc ascPrecedent)
        {
            return new AlgoAscCycliqueIndependant(id, config, phys, ascPrecedent);
        }
    };
}
