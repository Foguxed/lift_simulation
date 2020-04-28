package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public interface PartitionGenerator
{
    PartitionSimu generer(Random r);
    PartitionGenerator getNewInstanceWithConfig(DataTagCompound newConfig, String updatedKey);
}
