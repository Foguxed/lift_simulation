package fr.fogux.lift_simulator.fichiers;

public class FichierPartitionConfig
{
    public final DataTagCompound partitionConfig;
    public final DataTagCompound immeubleConfig;

    public FichierPartitionConfig(final DataTagCompound partitionConfig, final DataTagCompound immeubleConfig)
    {
        this.partitionConfig = partitionConfig;
        this.immeubleConfig = immeubleConfig;
    }
}
