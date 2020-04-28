package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public abstract class PrintableEvenement extends Evenement
{

    protected PrintableEvenement(final long time)
    {
        super(time);
    }

    protected PrintableEvenement(final long time, final DataTagCompound compound)
    {
        super(time);
    }
    @Override
    public void print(final Simulation simu)
    {
        GestFichiers.printIn(simu.getJournalOutput(), getEventString(simu.getTime()));
    }



    protected abstract void printFieldsIn(DataTagCompound compound, long atTime);

    protected String getEventString(final long noticedTime)
    {
        final DataTagCompound compound = new DataTagCompound();
        printFieldsIn(compound, noticedTime);
        compound.setString(TagNames.type, Evenements.getType(this.getClass()));
        return "[" + Utils.getTimeString(noticedTime) + "]" + compound.getValueAsString();
    }

}
