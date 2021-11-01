package fr.fogux.lift_simulator.mind.ascenseurs;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public interface GloutonAsc
{
    void attribuer(AlgoPersonne p);
    int evaluer(AlgoPersonne p);
}
