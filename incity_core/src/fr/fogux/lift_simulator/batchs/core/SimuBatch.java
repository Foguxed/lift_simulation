package fr.fogux.lift_simulator.batchs.core;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import fr.fogux.lift_simulator.batchs.graphs.BatchGraphProducer;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;

public abstract class SimuBatch
{
    protected final File dossier;
    protected final BatchThreadManager manager;

    public SimuBatch(final File dossierDuBatch, final long randomSeed, final int nbThreads)
    {
        dossier = dossierDuBatch;
        manager = new BatchThreadManager(GestFichiers.createBatchErrorLogDirectory(dossierDuBatch),randomSeed,nbThreads);
    }

    public void run()
    {
        runBatch();
    }


    protected boolean stop()
    {
        if(new File(dossier,"stop.txt").exists())
        {
            return true;
        }
        return false;
    }

    protected abstract void runBatch();

    public static SimuBatch fromCompound(final File dossierBatch,final DataTagCompound batchConfig) throws IOException
    {
        final String type = batchConfig.getString(TagNames.batchType);
        if(!batchConfig.hasKey(TagNames.seed))
        {
            batchConfig.setLong(TagNames.seed, new Random().nextLong());
        }
        switch (type)
        {
            case BatchGraphProducer.Name:
                return new BatchGraphProducer(dossierBatch,batchConfig);
            default:
                throw new SimulateurException("btach type " + type + " is unknown");
        }
    }
}
