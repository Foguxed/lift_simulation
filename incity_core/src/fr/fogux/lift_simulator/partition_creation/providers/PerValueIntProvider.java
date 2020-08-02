package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.Random;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.partition_creation.PerValueWeighter;

public class PerValueIntProvider extends PerValueWeighter<Integer> implements IntProvider
{	
	public PerValueIntProvider(DataTagCompound c) 
	{
		super(c);
	}

	@Override
	public int getRandomInt(Random r) 
	{
		return picker.getRandomObject(r);
	}

	@Override
	protected Integer extractFromCompound(DataTagCompound c, String key) 
	{
		return c.getInt(key);
	}

	@Override
	protected void setInCompound(DataTagCompound c, String key, Integer value) {
		c.setInt(key, value);
	}
	
}
