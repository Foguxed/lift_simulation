package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.partition_creation.providers.DateProvider;
import fr.fogux.lift_simulator.partition_creation.providers.IntProvider;
import fr.fogux.lift_simulator.partition_creation.providers.IntervalleIntProvider;
import fr.fogux.lift_simulator.partition_creation.providers.PerValueIntProvider;

public class PassengerProfile implements Compoundable
{
	private final DateProvider dateProvider;
	private final IntervalleIntProvider depart;
	private final IntProvider destination;
	private final IntProvider groupSize;
	
	public PassengerProfile(DataTagCompound compound)
	{
		dateProvider = DateProvider.fromCompound(compound.getCompound(TagNames.dateProvider));
		depart = new IntervalleIntProvider(compound.getCompound(TagNames.providerEtageDepart));
		destination = new IntervalleIntProvider(compound.getCompound(TagNames.providerEtageDestination));
		groupSize = new PerValueIntProvider(compound.getCompound(TagNames.providerGroupeSize));
	}

	@Override
	public void printFieldsIn(DataTagCompound compound) 
	{
		compound.setCompound(TagNames.dateProvider, Compoundable.compound(dateProvider));
		compound.setCompound(TagNames.providerEtageDepart, Compoundable.compound(depart));
		compound.setCompound(TagNames.providerEtageDestination, Compoundable.compound(destination));
		compound.setCompound(TagNames.providerGroupeSize, Compoundable.compound(groupSize));
	}
	
	public EvenementPersonnesInput getRandomEvent(Random r)
	{
		int dest = destination.getRandomInt(r);
		return new EvenementPersonnesInput(dateProvider.getRandomDate(r), groupSize.getRandomInt(r), dest, depart.getRandomIntExcept(r, dest));
	}
}
