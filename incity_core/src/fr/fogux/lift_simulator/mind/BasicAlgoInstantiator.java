package fr.fogux.lift_simulator.mind;

import java.lang.reflect.InvocationTargetException;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class BasicAlgoInstantiator implements AlgoInstantiator
{
    private final String name;
    private final Class<? extends Algorithme> algo;

    public BasicAlgoInstantiator(final Class<? extends Algorithme> algo, final String name)
    {
        this.name = name;
        this.algo = algo;
    }

    @Override
    public Algorithme getPrgm(final InterfacePhysique output, final ConfigSimu c)
    {
        try
        {
            return algo.getDeclaredConstructor(InterfacePhysique.class, ConfigSimu.class).newInstance(output,c);
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new SimulateurException("l'algorithme " + name + " doit admettre un constructeur de forme (InterfacePhysique output, ConfigSimu c)");
    }

    @Override
    public String getName()
    {
        return name;
    }
}
