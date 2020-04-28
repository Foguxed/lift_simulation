package fr.fogux.lift_simulator.mind;


import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public interface AlgoInstantiator
{
    Algorithme getPrgm(InterfacePhysique output, ConfigSimu c);

    String getName();
}
