package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.Random;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public interface DateProvider extends Compoundable
{
	public long getRandomDate(Random r);
	
	public static DateProvider fromCompound(DataTagCompound c)
	{
		byte b = c.getByte(TagNames.dateProviderType);
		switch (b)
		{
			case 1:
				return new IntervalleDateProvider(c);
			default:
				throw new SimulateurException("Date provider type " + b + " is unknown");
		}
	}
}
