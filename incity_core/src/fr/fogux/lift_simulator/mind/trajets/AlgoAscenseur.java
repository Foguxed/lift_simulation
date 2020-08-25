package fr.fogux.lift_simulator.mind.trajets;

import java.util.List;

public interface AlgoAscenseur
{

    void escaleTerminee();
    void init();

    List<Integer> getInvites(final int niveau, final int placesDispo);

}
