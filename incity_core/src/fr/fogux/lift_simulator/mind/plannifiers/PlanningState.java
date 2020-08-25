package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.List;

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
