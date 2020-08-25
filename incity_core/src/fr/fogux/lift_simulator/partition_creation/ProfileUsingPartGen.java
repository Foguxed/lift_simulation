package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public class ProfileUsingPartGen implements PartitionGenerator
{
    protected final int nbPersToMove;
    protected ProfileInputEventProvider inputEventProvider;
    protected ConfigPartitionGen c;

    public static final String NAME = "profileUsingPartGen";

    public ProfileUsingPartGen(final DataTagCompound c)
    {
        this(new ConfigPartitionProfileUsing(c));
    }

    public ProfileUsingPartGen(final ConfigPartitionProfileUsing config)
    {
        nbPersToMove = config.nbPersonnesDeplacees;
        inputEventProvider = config.eventProvider;
        c = config;
    }

    @Override
    public PartitionSimu generer(final Random r)
    {
        return new PartitionSimu(inputEventProvider.getRandomEvents(nbPersToMove, r));
    }

    @Override
    public ConfigPartitionGen getConfig()
    {
        return c;
    }

}
