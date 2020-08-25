package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public interface PartitionGenerator
{
    PartitionSimu generer(Random r);
    ConfigPartitionGen getConfig();

    static PartitionGenerator fromCompound(final DataTagCompound c)
    {
        final String type = c.getString(TagNames.partitionGenType);
        switch(type)
        {
            case HomogenePartitionGen.NAME:
                return new HomogenePartitionGen(c);
            case ProfileUsingPartGen.NAME:
                return new ProfileUsingPartGen(c);
            case OfflinePartGen.NAME:
                return new OfflinePartGen(c);
            default:
                throw new SimulateurException("partition generator type " + type + " is unknown");
        }
    }
}
