package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class ConfigPartitionHomogene extends ConfigPartitionGen
{
    protected final long duree;


    public ConfigPartitionHomogene(final int niveauMin, final int niveauMax, final int[] repartAsc, final int nbPersonnesDeplacees,final long duree)
    {
        super(niveauMin, niveauMax, repartAsc, nbPersonnesDeplacees);
        this.duree = duree;
    }


    public ConfigPartitionHomogene(final DataTagCompound compound)
    {
        super(compound);
        duree = compound.getLong(TagNames.dureePartition);
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setLong(TagNames.dureePartition, duree);
    }
}
