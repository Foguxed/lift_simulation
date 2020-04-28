package fr.fogux.lift_simulator.fichiers;

import java.util.List;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;

public class FichierPartition
{
    public final DataTagCompound partitionConfig;
    public final DataTagCompound immeubleConfig;
    public final List<EvenementPersonnesInput> evenements;

    public FichierPartition(final DataTagCompound partitionConfig, final DataTagCompound immeubleConfig,
        final List<EvenementPersonnesInput> evenements)
    {
        this.partitionConfig = partitionConfig;
        this.immeubleConfig = immeubleConfig;
        this.evenements = evenements;
    }

    public FichierPartition(final FichierPartitionConfig configFichier, final PartitionSimu partitionSimu)
    {
        partitionConfig = configFichier.partitionConfig;
        immeubleConfig = configFichier.immeubleConfig;
        evenements = partitionSimu.getInputs();
    }
}
