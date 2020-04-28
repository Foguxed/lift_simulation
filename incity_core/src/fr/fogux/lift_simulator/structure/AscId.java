package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class AscId
{
    public final int monteeId;
    public final int stackId;

    public AscId(final int monteeId, final int stackId)
    {
        this.monteeId = monteeId;
        this.stackId = stackId;
    }

    public void printIn(final DataTagCompound compound)
    {
        compound.setInt(TagNames.monteeId, monteeId);
        compound.setInt(TagNames.ascStackId, stackId);
    }

    public static AscId fromCompound(final DataTagCompound compound)
    {
        return new AscId(compound.getInt(TagNames.monteeId), compound.getInt(TagNames.ascStackId));
    }

    @Override
    public String toString()
    {
        return "ascId :[" + monteeId +"," + stackId + "]";
    }
}
