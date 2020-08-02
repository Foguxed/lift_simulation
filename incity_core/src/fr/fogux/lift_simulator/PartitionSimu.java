package fr.fogux.lift_simulator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;

public class PartitionSimu
{
    protected final List<EvenementPersonnesInput> personneInputs;

    public PartitionSimu(final List<EvenementPersonnesInput> inputs)
    {
    	Collections.sort(inputs);
    	personneInputs = inputs;
    }
    
    public Iterator<EvenementPersonnesInput> getInputIterator()
    {
    	return personneInputs.iterator();
    }
    
    public List<EvenementPersonnesInput> getEvents()
    {
    	return personneInputs;
    }
}
