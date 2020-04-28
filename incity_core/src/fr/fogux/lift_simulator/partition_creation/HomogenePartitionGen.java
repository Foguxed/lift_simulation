package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public class HomogenePartitionGen implements PartitionGenerator
{
    public static final String NAME = "homogenePartitionGen";

    protected final ConfigPartitionHomogene config;
    protected final int hauteurImmeuble;

    public HomogenePartitionGen(final DataTagCompound compoundConfig)
    {
        config = new ConfigPartitionHomogene(compoundConfig);
        hauteurImmeuble = config.getNiveauMax() - config.getNiveauMin()+1;
    }

    @Override
    public PartitionSimu generer(final Random r)
    {
        final List<EvenementPersonnesInput> pInputs = new ArrayList<>();
        for(int i = 0; i < config.nbPersonnesDeplacees; i ++)
        {
            final int etage = config.getNiveauMin() + r.nextInt(hauteurImmeuble);
            pInputs.add(new EvenementPersonnesInput(r.nextInt((int)config.duree), 1, getRandomEtage(etage,r), etage));
        }
        java.util.Collections.sort(pInputs,Simulateur.EVENT_COMPARATOR);
        return new PartitionSimu(pInputs);
    }

    protected int getRandomEtage(final int etageExclu, final Random r)
    {
        int val = config.getNiveauMin() + r.nextInt(hauteurImmeuble - 1);
        if(val >= etageExclu)
        {
            val ++;
        }
        return val;
    }

    @Override
    public PartitionGenerator getNewInstanceWithConfig(final DataTagCompound newConfig, final String updatedKey)
    {
        return new HomogenePartitionGen(newConfig);
    }

}
