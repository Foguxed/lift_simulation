package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class ConfigPartitionOffline extends ConfigPartitionProfileUsing
{

    public ConfigPartitionOffline(final int niveauMin, final int niveauMax, final int[] repartAsc, final int nbPersonnesDeplacees,final long duree, final ProfileInputEventProvider provider)
    {
        super(niveauMin, niveauMax, repartAsc, nbPersonnesDeplacees,provider);
    }

    public ConfigPartitionOffline(final DataTagCompound compound)
    {
        super(compound);
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setString(TagNames.partitionGenType, OfflinePartGen.NAME);// on ovveride ici
    }
}
