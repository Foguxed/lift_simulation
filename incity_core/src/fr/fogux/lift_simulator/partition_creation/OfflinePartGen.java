package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public class OfflinePartGen implements PartitionGenerator
{
    public static final String NAME = "OfflinePartGen";
    protected final ProfileUsingPartGen innerGen;
    protected final int nbPers;

    public OfflinePartGen(final DataTagCompound c)
    {
        this(new ConfigPartitionOffline(c));
    }

    public OfflinePartGen(final ConfigPartitionOffline config)
    {
        innerGen = new ProfileUsingPartGen(config);
        nbPers = innerGen.nbPersToMove;
    }

    @Override
    public PartitionSimu generer(final Random r)
    {
        int countPers = nbPers-1;
        final List<EvenementPersonnesInput> events = new ArrayList<>();
        while(countPers>0)
        {
            EvenementPersonnesInput newone = innerGen.inputEventProvider.getRandomEvent(r);
            countPers -= newone.nbPersonnes;
            if(countPers < 0)
            {
                newone = new EvenementPersonnesInput(newone, newone.nbPersonnes + countPers,0);
            }
            else
            {
                newone = new EvenementPersonnesInput(newone, newone.nbPersonnes,0);
            }
            events.add(newone);
        }
        final EvenementPersonnesInput last = innerGen.inputEventProvider.getRandomEvent(r);
        events.add(new EvenementPersonnesInput(last, 1,1));
        return new PartitionSimu(events);
    }

    @Override
    public ConfigPartitionGen getConfig()
    {
        return null;
    }

}
