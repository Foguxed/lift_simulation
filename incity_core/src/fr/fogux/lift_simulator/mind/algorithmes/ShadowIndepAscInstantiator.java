package fr.fogux.lift_simulator.mind.algorithmes;

import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePoolInstantiator;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public interface ShadowIndepAscInstantiator<T extends AlgoIndependentAsc> extends IndepAscInstantiator<T>, FewUpdatePoolInstantiator
{
    T shadowInstantiate(T toShadow, final ConfigSimu config, final OutputProvider phys, final VoisinAsc ascPrecedent);
}
