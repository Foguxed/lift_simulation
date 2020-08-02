package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.Random;

import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class IntervalleDateProvider implements DateProvider, Compoundable
{
	private final long dateMin;
	private final int longueur;
	
	public IntervalleDateProvider(long dateMin, long dateMax)
	{
		this.dateMin = dateMin;
		this.longueur = (int)(dateMax - dateMin + 1);
	}
	
	public IntervalleDateProvider(DataTagCompound compound)
	{
		this(compound.getLong(TagNames.min), compound.getLong(TagNames.max));
	}
	
	@Override
	public long getRandomDate(Random r) 
	{
		return dateMin + r.nextInt(longueur);
	}

	@Override
	public void printFieldsIn(DataTagCompound compound) 
	{
		compound.setLong(TagNames.min, dateMin);
		compound.setLong(TagNames.min, dateMin + longueur - 1);
		compound.setByte(TagNames.dateProviderType, (byte)1);
	}
}
