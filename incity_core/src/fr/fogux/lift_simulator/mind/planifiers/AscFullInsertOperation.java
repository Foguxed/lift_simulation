package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.algorithmes.BestInstert;
import fr.fogux.lift_simulator.structure.AscId;

public class AscFullInsertOperation<T extends Comparable<T>> extends PlanningOperation<T>
{
    private final AscId ascId;
    private final int entreeIndex;
    private final int sortieIndex;


    public AscFullInsertOperation(final AscId ascId,final int entreeIndex, final int sortieIndex, final T resultat)
    {
        super(resultat);
        this.ascId = ascId;
        this.entreeIndex = entreeIndex;
        this.sortieIndex = sortieIndex;
    }

    @Override
    public void apply(final BestInstert<T> p)
    {
        // TODO Auto-generated method stub

    }

}
