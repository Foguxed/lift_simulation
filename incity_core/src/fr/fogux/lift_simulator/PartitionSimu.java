package fr.fogux.lift_simulator;

import java.util.List;

import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;

public class PartitionSimu
{
    protected final List<EvenementPersonnesInput> inputs;

    public PartitionSimu(final List<EvenementPersonnesInput> inputs)
    {
        this.inputs = inputs;
    }

    public List<EvenementPersonnesInput> getInputs()
    {
        return inputs;
    }


}
