package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public abstract class AnimatedEvent extends PrintableEvenement
{
    protected long animationDuree;
    protected boolean isCreationNotice;

    public AnimatedEvent(long time, boolean doExecuteInTasks)
    {
        super(time, doExecuteInTasks);
        animationDuree = time - GestionnaireDeTaches.getInnerTime();
    }

    public AnimatedEvent(long time, DataTagCompound compound)
    {
        super(time, true);
        isCreationNotice = compound.getBoolean(TagNames.isCreationNotice);
        animationDuree = compound.getLong(TagNames.animationDuree);
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setBoolean(TagNames.isCreationNotice, GestionnaireDeTaches.getInnerTime() < getTime());
        compound.setLong(TagNames.animationDuree, animationDuree);
    }

    @Override
    public void visuRun()
    {
        if (isCreationNotice)
        {
            if (GestionnaireDeTaches.marcheArriere())
            {
                sortieAnimation();
            } else
            {
                runAnimation(time, animationDuree);
            }
        } else
        {
            if (GestionnaireDeTaches.marcheArriere())
            {
                runAnimation(time - animationDuree, animationDuree);
            } else
            {
                sortieAnimation();
            }
        }
    }

    protected abstract void runAnimation(long timeDebut, long duree);

    protected abstract void sortieAnimation();
}
