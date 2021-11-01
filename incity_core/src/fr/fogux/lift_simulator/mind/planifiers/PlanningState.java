package fr.fogux.lift_simulator.mind.planifiers;

import java.util.List;

import fr.fogux.lift_simulator.mind.algorithmes.BestInstert;

public class PlanningState<T extends Comparable<T>> extends PlanningOperation<T>
{
    public List<List<List<AlgoRequete>>> state;


    public PlanningState(final T resultat, final List<List<List<AlgoRequete>>> state)
    {
        super(resultat);
        this.state = state;
    }

    @Override
    public void apply(final BestInstert<T> p)
    {
        p.applyState(state);
    }

}
