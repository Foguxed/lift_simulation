package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import fr.fogux.lift_simulator.Simulation;

public interface SimuEvaluator<T>
{
    T evaluate(Simulation s , int time);

    T evaluateTerminated(Simulation s);

    T evaluateAbsolute(Simulation s, int time);
}
