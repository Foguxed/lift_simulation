package fr.fogux.lift_simulator.fichiers.variators;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class FloatCompoundVariator extends CompoundVariator
{
	public static final String Name = "Float";
	
	protected float min;
	protected float current;
	protected float step;
	protected float max;
	
	public FloatCompoundVariator(DataTagCompound c)
	{
		max = c.getFloat(TagNames.max);
		min = c.getFloat(TagNames.min);
		step = (max - min)/getNbSteps(c);	}
	
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
	
}
