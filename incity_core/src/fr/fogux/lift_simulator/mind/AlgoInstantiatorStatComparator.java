package fr.fogux.lift_simulator.mind;

import java.lang.reflect.InvocationTargetException;

import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public class AlgoInstantiatorStatComparator implements AlgoInstantiator
{
    private final String name;
    private final Class<? extends Algorithme> algo;
    private final MinorableSimulStatCreator<?> statCreator;

    public AlgoInstantiatorStatComparator(final Class<? extends Algorithme> algo, final String name, final MinorableSimulStatCreator<? extends Comparable<?>> statCreator)
    {
        this.name = name;
        this.algo = algo;
        this.statCreator = statCreator;
    }

    @Override
    public Algorithme getPrgm(final OutputProvider output, final ConfigSimu c)
    {
        try
        {
            return algo.getDeclaredConstructor(OutputProvider.class, ConfigSimu.class,MinorableSimulStatCreator.class).newInstance(output,c,statCreator);
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
