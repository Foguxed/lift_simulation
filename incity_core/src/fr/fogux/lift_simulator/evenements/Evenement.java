package fr.fogux.lift_simulator.evenements;

import java.lang.reflect.InvocationTargetException;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public abstract class Evenement
{
    protected long time;

    protected Evenement(long time, boolean doRegisterAsTask)
    {
        this.time = time;
        if (doRegisterAsTask && !Simulateur.animationTime)
        {
            Simulateur.getGestionnaireDeTachesSimu().executerA(this, time);
        }
    }

    public Evenement(long time, DataTagCompound compound)
    {
        this.time = time;
    }

    public static Evenement genererEvenement(String data)
    {
        System.out.println(data);
        if (data == null)
        {

            return null;
        }
        System.out.println("substring " + data.substring(data.indexOf("[") + 1, data.lastIndexOf("]")));
        long time = Utils.timeInMilis(data.substring(data.indexOf("[") + 1, data.indexOf("]")));
        System.out.println(time);
        DataTagCompound tag = new DataTagCompound(data.substring(data.indexOf("{") + 1, data.lastIndexOf("}")));
        try
        {
            // System.out.println("event "
            // +Evenements.getEvenement(tag.getString(TagNames.type)));
            return Evenements.getEvenement(tag.getString(TagNames.type)).getDeclaredConstructor(
                long.class, DataTagCompound.class).newInstance(time, tag);
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

    public void cancel()
    {
        Simulateur.getGestionnaireDeTachesSimu().CancelEvenement(this);
    }

    public abstract void simuRun();

    public abstract void visuRun();
}
