package fr.fogux.lift_simulator.fichiers.variators;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.fichiers.strings.StringProvider;

public abstract class CompoundVariator implements StringProvider
{	
	protected static int getNbSteps(DataTagCompound compound)
	{
		return compound.getInt(TagNames.nbSteps);
	}
	
	public static CompoundVariator fromString(String variatorStr)
	{
		DataTagCompound c = new DataTagCompound(variatorStr);
		String type = c.getString(TagNames.variatorType);
		switch(type)
		{
			case FloatCompoundVariator.Name:
				return new FloatCompoundVariator(c);
			case IntCompoundVariator.Name:
				return new IntCompoundVariator(c);
			default:
				throw new SimulateurException("variator type " + type + " is unknown");
		}
	}
	
	public String toString()
	{
		return getString();
	}
	
	public abstract String getString();
	public abstract boolean nextStep();
}
