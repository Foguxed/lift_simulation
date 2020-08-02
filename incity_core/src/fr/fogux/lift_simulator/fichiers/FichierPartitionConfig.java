package fr.fogux.lift_simulator.fichiers;

import fr.fogux.lift_simulator.partition_creation.ConfigPartitionGen;

public class FichierPartitionConfig
{
    public final DataTagCompound partitionConfig;
    public final DataTagCompound immeubleConfig;
    
    public static FichierPartitionConfig fromConfig(final ConfigPartitionGen config)
    {
    	final DataTagCompound immeubleC = new DataTagCompound();
    	config.printOnlyImmeubleFieldsIn(immeubleC);
    	
    	final DataTagCompound partiC = new DataTagCompound();
    	config.printFieldsIn(partiC);

        partiC.removeKeysCommunes(immeubleC);
        
        return new FichierPartitionConfig(partiC, immeubleC);
    }
    
    public FichierPartitionConfig(final DataTagCompound partitionConfig, final DataTagCompound immeubleConfig)
    {
        this.partitionConfig = partitionConfig;
        this.immeubleConfig = immeubleConfig;
    }
}
