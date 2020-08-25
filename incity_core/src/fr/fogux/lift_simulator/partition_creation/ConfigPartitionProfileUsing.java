package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class ConfigPartitionProfileUsing extends ConfigPartitionGen
{
    protected final ProfileInputEventProvider eventProvider;

    public ConfigPartitionProfileUsing(final int niveauMin, final int niveauMax, final int[] repartAsc, final int nbPersonnesDeplacees, final ProfileInputEventProvider provider)
    {
        super(niveauMin, niveauMax, repartAsc, nbPersonnesDeplacees);
        eventProvider = provider;
    }

    public ConfigPartitionProfileUsing(final DataTagCompound compound)
    {
        super(compound);
        eventProvider = new ProfileInputEventProvider(compound.getCompound(TagNames.inputEventProvider));
    }

    public ConfigPartitionProfileUsing(final DataTagCompound compound, final ConfigPartitionProfileUsing previous, final DataTagCompound previousCompound)
    {
        super(compound);
        if(compound.getString(TagNames.inputEventProvider).equals(previousCompound.getString(TagNames.inputEventProvider)))
        {
            eventProvider = previous.eventProvider;
        }
        else
        {
            eventProvider = new ProfileInputEventProvider(compound.getCompound(TagNames.inputEventProvider));
        }
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setCompound(TagNames.inputEventProvider, Compoundable.compound(eventProvider));
        compound.setString(TagNames.partitionGenType, ProfileUsingPartGen.NAME);
    }


}
