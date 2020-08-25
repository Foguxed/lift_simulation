package fr.fogux.lift_simulator.batchs.core;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public interface MinorableSimulStatCreator<S> extends SimulationStatCreator<S>
{
    S getMinorant(Simulation simu, EtatMonoAsc etat, ConfigSimu c);
}
