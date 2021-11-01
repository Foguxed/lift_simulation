package fr.fogux.lift_simulator.evenements;

import java.lang.reflect.InvocationTargetException;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.GestionnaireDeTachesSimu;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public abstract class Evenement
{
    protected long time;

    protected Evenement(final long time)
    {
        this.time = time;
    }

    public void runOn(final Simulation simu)
    {
        simu.getGestio().executerA(this, time);
    }

    public void print(final Simulation simu)
    {
    }

    public void onPrintRegister(final GestionnaireDeTachesSimu gestio, final long registeredTime)
    {
    }

    public void onPrintCancel(final GestionnaireDeTachesSimu gestio, final long cancelRegisteredTime)
    {
    }



    public Evenement(final long time, final DataTagCompound compound)
    {
        this.time = time;
    }

    public static long time(final String fullLine)
    {
        return Utils.timeInMilis(fullLine.substring(fullLine.indexOf("[") + 1, fullLine.indexOf("]")));
    }

    public static Evenement genererEvenement(final String data)
    {
        if (data == null || data.charAt(0) != '[')
        {
            return null;
        }

        final long time = time(data);

        final DataTagCompound tag = new DataTagCompound(data);
        try
        {
            return Evenements
                .getEvenement(tag.getString(TagNames.type))
                .getDeclaredConstructor(long.class, DataTagCompound.class)
                .newInstance(time, tag);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public long getTime()
    {
        return time;
    }

    public void cancel(final Simulation simulation)
    {
        simulation.getGestio().CancelEvenement(this, getTime());
    }

    public abstract void simuRun(Simulation simulation);

    /**
     * the event has been interrupted on the simulation / a previous copy of the simulation, action to do:
     * @param simulation
     */
    public abstract void reRun(Simulation simulation);

    public abstract void visuRun(AnimationProcess animation);

    public abstract boolean shadowable(long registeredTime,EventRunPolicy policy);
}
