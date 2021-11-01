package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;

public interface IdAscPersPool extends PersPool
{
    List<AscId> getAscs();
    boolean contains(AlgoPersonne p);
}
