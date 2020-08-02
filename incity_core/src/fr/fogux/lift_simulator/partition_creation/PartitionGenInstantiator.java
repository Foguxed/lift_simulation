package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.DynamicString;

public class PartitionGenInstantiator
{
	private DynamicString compoundCreator;
	private PartitionGenerator current;
	
	
	
	public PartitionGenInstantiator(DynamicString compoundCreator, DataTagCompound initialImmeubleCompound)
	{
		this.compoundCreator = compoundCreator;
		DataTagCompound initial = new DataTagCompound(compoundCreator.getString());
		System.out.println("initialCreator " + compoundCreator.getString());
		initial.mergeWith(initialImmeubleCompound);
		current = PartitionGenerator.fromCompound(initial);
	}
	
	public PartitionGenerator getPartitionGen()
	{
		return current;
	}
	
	public boolean cycleNext(DataTagCompound immeubleCompound)
	{
		if(current instanceof ProfileUsingPartGen)
		{
			DataTagCompound previous = new DataTagCompound(compoundCreator.getString());
			
			boolean b = compoundCreator.next();
			DataTagCompound c = new DataTagCompound(compoundCreator.getString());
			//System.out.println("ccreator str " + compoundCreator.getString());
			c.mergeWith(immeubleCompound);
			ConfigPartitionProfileUsing newC = new ConfigPartitionProfileUsing(c, (ConfigPartitionProfileUsing)current.getConfig(), previous);
			//System.out.println("configPartition " + newC);
			current = new ProfileUsingPartGen(newC);
			return b;
		}
		boolean b = compoundCreator.next();
		DataTagCompound c = new DataTagCompound(compoundCreator.getString());
		c.mergeWith(immeubleCompound);
		current = PartitionGenerator.fromCompound(c);
		return b;
		
	}
}
