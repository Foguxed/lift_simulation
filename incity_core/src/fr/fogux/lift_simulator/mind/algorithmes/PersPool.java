package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;

public interface PersPool
{

    void addToPool(AlgoPersonne newPers);

    /**
     *
     * @param pers, pas forcément présente dans la pool
     */
    void removeFromPool(AlgoPersonne pers);

    boolean couldAccept(AlgoPersonne newPers);
}
