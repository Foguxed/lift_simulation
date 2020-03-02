package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public abstract class PrintableEvenement extends Evenement
{

    protected PrintableEvenement(long time, boolean doExecuteInTasks)
    {
        super(time, doExecuteInTasks);
    }

    protected PrintableEvenement(long time, DataTagCompound compound)
    {
        super(time, true);
    }

    public void print()
    {
        GestionnaireDeFichiers.noticeNewLineInJournal(getEventString(GestionnaireDeTaches.getInnerTime()));
    }

    protected abstract void printFieldsIn(DataTagCompound compound);

    protected String getEventString(long noticedTime)
    {
        DataTagCompound compound = new DataTagCompound();
        printFieldsIn(compound);
        compound.setString(TagNames.type, Evenements.getType(this.getClass()));
        return "[" + Utils.getTimeString(noticedTime) + "]{" + compound.getValueAsString() + "}";
    }

    @Override
    public void simuRun()
    {
        print();
    }

}
