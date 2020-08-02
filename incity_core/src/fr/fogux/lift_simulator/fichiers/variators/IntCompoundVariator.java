package fr.fogux.lift_simulator.fichiers.variators;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class IntCompoundVariator extends CompoundVariator
{
	public static final String Name = "Int";
	
	protected int min;
	protected int current;
	protected int step;
	protected int max;
	
	public IntCompoundVariator(DataTagCompound c)
	{
		max = c.getInt(TagNames.max);
		min = c.getInt(TagNames.min);
		step = (max - min)/getNbSteps(c);	
		current = min;
		if(step <= 0)
		{
			throw new SimulateurException("step value is" + step + " , max = " + max + " min = " + min + " nbSteps = " + getNbSteps(c));
		}
	}
	
	private void reset()
	{
		current = min;
	}
	
	/**
	 * 
	 * @return true if got reset
	 */
	public boolean nextStep()
	{
		current += step;
		if(current >= max)
		{
			reset();
			return true;
		}
		return false;
	}
	
	@Override
	public String getString() 
	{
		return String.valueOf(current);
	}
	
	public String toString()
	{
		return "Int variator {max = " + max + " min = " + min + " current = " + current + " step = " + step + "}";
	}
	
}
