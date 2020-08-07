package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public class ProfileUsingPartGen implements PartitionGenerator
{
	protected final int nbPersToMove;
	protected ProfileInputEventProvider inputEventProvider;
	protected ConfigPartitionGen c;
	
	public static final String Name = "profileUsingPartGen";
	
	public ProfileUsingPartGen(DataTagCompound c)
	{
		this(new ConfigPartitionProfileUsing(c));
	}
			
	public ProfileUsingPartGen(ConfigPartitionProfileUsing config)
	{
		this.nbPersToMove = config.nbPersonnesDeplacees;
		this.inputEventProvider = config.eventProvider;
		this.c = config;
	}
	
	@Override
	public PartitionSimu generer(Random r) 
	{
		return new PartitionSimu(inputEventProvider.getRandomEvents(nbPersToMove, r));
	}

	@Override
	public ConfigPartitionGen getConfig() 
	{
		return c;
	}

}
