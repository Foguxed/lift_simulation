package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class AscSoftDep extends AscState
{
	public final byte softDepType;//(1 = dep simple en 2 parties, 2 = dep en 3 parties,3 = dep avec tangeance à l'arrivée en 2 parties, 4 = dep avec tangeance à l'arrivée en 3 parties
	//negatif, de même avec a < 0
	public AscSoftDep(long tf, float xf, float vf,byte type)
	{
		super(tf,xf,vf);
		this.softDepType = type;
	}
	
	public AscSoftDep(DataTagCompound compound)
	{
		super(compound);
		this.softDepType = compound.getByte(TagNames.softDepType);
	}
	
	public boolean aPositif()
	{
		return softDepType > 0;
	}
	
	public String toString()
	{
		return "AscSoftDep { super = " + super.toString() + " softDepType = " + softDepType + "}";
	}
	
	public AscSoftDep copyTranslatedAndBounded(float xOffset)
	{
		final byte b;
		switch (softDepType)
		{
		case 1:
		case 2:
			b = (byte) (softDepType + 2);
			break;
		case -1:
		case -2:
			b = (byte) (softDepType - 2);
			break;
		default:
			b = softDepType;
		}
		return new AscSoftDep(t, x+xOffset, v, b);
	}
	
	@Override
	public void printFieldsIn(DataTagCompound compound)
	{
		super.printFieldsIn(compound);
		compound.setByte(TagNames.softDepType, softDepType);
	}
	
	public boolean is3Phases()
	{	
		switch (softDepType)
		{
		case 2:
		case 4:
		case -2:
		case -4:
			return true;
		default:
			return false;
		}
	}
}
