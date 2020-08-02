package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public class ProfileInputEventProvider extends PerValueWeighter<PassengerProfile>
{

	public ProfileInputEventProvider(DataTagCompound c) 
	{
		super(c);
	}

	@Override
	protected PassengerProfile extractFromCompound(DataTagCompound c, String key) 
	{
		return new PassengerProfile(c.getCompound(key));
	}

	@Override
	protected void setInCompound(DataTagCompound c, String key, PassengerProfile value) 
	{
		c.setCompound(key, Compoundable.compound(value));
	}
	
	public EvenementPersonnesInput getRandomEvent(Random r)
	{
		return picker.getRandomObject(r).getRandomEvent(r);
	}
}
