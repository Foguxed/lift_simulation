package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.evenements.Evenement;

public interface EventRunPolicy
{
    void onSimuRun(Evenement e);
    void onRegister(Evenement e, GestionnaireDeTachesSimu gestio, long time);
    void onCancel(Evenement e, GestionnaireDeTachesSimu gestio, long time);
    boolean doPrint();
}
