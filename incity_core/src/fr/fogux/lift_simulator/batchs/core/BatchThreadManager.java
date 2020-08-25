package fr.fogux.lift_simulator.batchs.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.partition_creation.PartitionGenerator;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class BatchThreadManager
{
    private final ExecutorService service;
    private int nbExceptions = 0;
    private final File dossierErreurs;
    private final Random masterRandom;
    private final AtomicInteger nbThreadsAwaiting;
    private int nbThreadDone = 0;
    private boolean mainThreadWaiting;
    private final int nbThreads;

    public BatchThreadManager(final File dossierErreurs, final long randomSeed, final int nbThreads)
    {
        service = Executors.newFixedThreadPool(nbThreads);
        this.nbThreads = nbThreads;
        this.dossierErreurs = dossierErreurs;
        masterRandom = new Random(randomSeed);
        nbThreadsAwaiting = new AtomicInteger(0);
        mainThreadWaiting = false;
    }

    public synchronized void registerException()
    {
        nbExceptions ++;
        if(nbExceptions > 20)
        {
            throw new SimulateurException("Exception batch counter exceeds " + 20);
        }
    }

    public synchronized File getNewErrorDirectory(final AlgoInstantiator algoInstantiator)
    {
        return GestFichiers.getNewErrorDirectory(dossierErreurs,algoInstantiator);
    }

    public synchronized void registerFatalException(final Exception e)
    {
        final File fatalErrorFile = new File(dossierErreurs,NomsFichiers.fatalError + NomsFichiers.extension);
        try {
            GestFichiers.writeErrorLogs(e,fatalErrorFile);
        }
        catch (final FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        System.out.println("fatal error " + e + " registered, shutdown");
        if(!service.isShutdown())
        {
            e.printStackTrace();
            shutdown();
            notify();
        }
    }

    public synchronized void plannifySimulation(final SimulationStatCreator<?> creator,final List<AlgoInstantiator> algos, final PartitionGenerator partitionGen, final ConfigSimu configSimu,final SimuTaskReceiver<?> taskReceiver)
    {
        if(service.isShutdown())
        {
            throw new SimulateurException("Service is shutdowned, see fatalError file");
        }
        if(mainThreadWaiting)
        {
            throw new IllegalStateException("plannifySimulation should be always called by main thread");
        }
        service.execute(new SimulationRunnable(this, creator, algos, configSimu, partitionGen,taskReceiver, masterRandom.nextLong()));
        final int v = nbThreadsAwaiting.incrementAndGet();
        if(v > 3*nbThreads)
        {
            try
            {
                mainThreadWaiting = true;
                wait();
                mainThreadWaiting = false;
            } catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void decrementThreadsAwaiting()
    {
        final int v = nbThreadsAwaiting.decrementAndGet();
        nbThreadDone ++;
        if(nbThreadDone % 1 == 0)
        {
            System.out.println("taskDone " + nbThreadDone);
        }
        if(v < 2*nbThreads & mainThreadWaiting)
        {
            notify();
        }
    }

    public boolean closeAndWait(final long timeoutInSeconds) throws InterruptedException
    {
        service.shutdown();
        return service.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS);
    }

    public void shutdown()
    {
        service.shutdownNow();
    }
}
