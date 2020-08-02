package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.Random;

import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class IntervalleIntProvider implements IntProvider, Compoundable
{
	private final int min;
	private final int longueur;
	
	public IntervalleIntProvider(int min, int mx)
	{
		this.min = min;
		this.longueur = mx - min + 1;
	}
	
	public IntervalleIntProvider(DataTagCompound compound)
	{
		this(compound.getInt(TagNames.min),compound.getInt(TagNames.max));
	}

	@Override
	public int getRandomInt(Random r) 
	{
		return min + r.nextInt(longueur);
	}
	

	public int getRandomIntExcept(Random r, int notAllowed)
	{
		if(notAllowed < min | notAllowed >= min + longueur)
		{
			return getRandomInt(r);
		}
		else
		{
			int resultat = min + r.nextInt(longueur - 1);
			if(resultat >= notAllowed)
			{
				resultat ++;
				return resultat;
			}
			else
			{
				return resultat;
			}
		}
		
		
	}
	
	@Override
	public void printFieldsIn(DataTagCompound compound) 
	{
		compound.setInt(TagNames.min, min);
		compound.setInt(TagNames.min, min + longueur - 1);
	}
	
}
