package fr.fogux.lift_simulator.mind.independant;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public interface GloutonAsc
{
    void attribuer(AlgoPersonne p);
    int evaluer(AlgoPersonne p);
}
