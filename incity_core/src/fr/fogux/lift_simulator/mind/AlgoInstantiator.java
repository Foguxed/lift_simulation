package fr.fogux.lift_simulator.mind;


import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public interface AlgoInstantiator
{
    Algorithme getPrgm(OutputProvider output, ConfigSimu c);

    String getName();
}
