package fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;
import fr.fogux.lift_simulator.mind.algorithmes.ShadowIndepAscInstantiator;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.mind.pool.ByEtageFewUpdatePool;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.mind.pool.IdByEtagePool;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public class AscCycleOptionInstantiator implements ShadowIndepAscInstantiator<AscCycleOption>
{

    @Override
    public AscCycleOption getNewInstance(final AscId id, final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent)
    {
        return new AscCycleOption(id, config, phys, ascPrecedent);
    }

    @Override
    public FewUpdatePool newPoolInstance(final IdAscPersPool innerPool)
    {
        return new ByEtageFewUpdatePool((IdByEtagePool)innerPool);
    }

    @Override
    public FewUpdatePool poolShadow(final FewUpdatePool toShadow)
    {
        return new ByEtageFewUpdatePool((ByEtageFewUpdatePool)toShadow);
    }

    @Override
    public AscCycleOption shadowInstantiate(final AscCycleOption toShadow, final ConfigSimu config, final OutputProvider phys,
        final VoisinAsc ascPrecedent)
    {
        return new AscCycleOption(toShadow, config, phys, ascPrecedent);
    }

}
