package fr.fogux.lift_simulator.mind;


import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public interface AlgoInstantiator
{
    Algorithme getPrgm(OutputProvider output, ConfigSimu c);

    String getName();
}
