package fr.fogux.lift_simulator.mind.algorithmes;

import fr.fogux.lift_simulator.mind.ascenseurs.AlgoAscCycliqueIndependant;
import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public interface IndepAscInstantiator<T extends AlgoIndependentAsc>
{
    T getNewInstance(final AscId id,final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent);

    IndepAscInstantiator<AlgoAscCycliqueIndependant> CYCLIQUE = new IndepAscInstantiator<AlgoAscCycliqueIndependant>()
    {
        @Override
        public AlgoAscCycliqueIndependant getNewInstance(final AscId id, final ConfigSimu config, final OutputProvider phys,
            final VoisinAsc ascPrecedent)
        {
            return new AlgoAscCycliqueIndependant(id, config, phys, ascPrecedent);
        }
    };
}
