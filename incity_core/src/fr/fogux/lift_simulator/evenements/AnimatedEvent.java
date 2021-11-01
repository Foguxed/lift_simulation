package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.GestionnaireDeTachesSimu;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public abstract class AnimatedEvent extends PrintableEvenement
{
    protected long debutTime;
    protected boolean isCreationNotice;

    public AnimatedEvent(final long endTime, final long debutTime)
    {
        super(endTime);
        this.debutTime = debutTime;
    }

    public AnimatedEvent(final long time, final DataTagCompound compound)
    {
        super(time);
        isCreationNotice = compound.getBoolean(TagNames.isCreationNotice);
        debutTime = time - compound.getLong(TagNames.animationDuree);
    }

    public boolean doNotSimuRun(final long runTime)
    {
        return runTime < getTime();
    }

    @Override
    public void onPrintRegister(final GestionnaireDeTachesSimu gestio, final long registeredTime)
    {
        if(registeredTime != debutTime)
        {
            //System.out.println("printRegister executer a " + debutTime + " event " + this + " registeredTime " + registeredTime);
            gestio.executerA(this, debutTime);
        }
    }

    @Override
    public void onPrintCancel(final GestionnaireDeTachesSimu gestio, final long cancelRegisteredTime)
    {
        if(cancelRegisteredTime != debutTime)
        {
            gestio.CancelEvenement(this, debutTime);
        }
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long atTime)
    {
        compound.setBoolean(TagNames.isCreationNotice, atTime < getTime());
        compound.setLong(TagNames.animationDuree, time - debutTime);
    }

    @Override
    public void visuRun(final AnimationProcess animation)
    {
        if (isCreationNotice)
        {
            if (animation.gestioTaches().marcheArriereEnCours())
            {
                sortieAnimation(animation);
            } else
            {
                runAnimation(animation, time, time - debutTime);
            }
        } else
        {
            if (animation.gestioTaches().marcheArriereEnCours())
            {
                runAnimation(animation, debutTime, time - debutTime);
            } else
            {
                sortieAnimation(animation);
            }
        }
    }

    @Override
    public boolean shadowable(final long registeredT,EventRunPolicy newPolicy)
    {
    	if(newPolicy.doPrint())
    	{
    		return true;
    	}
    	else
    	{
            return !doNotSimuRun(registeredT);
    	}
    }

    protected abstract void runAnimation(AnimationProcess animProcess, long timeDebut, long duree);

    protected abstract void sortieAnimation(AnimationProcess animProcess);
}
