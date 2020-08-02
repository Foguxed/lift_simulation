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
	private ExecutorService service;
	private int nbExceptions = 0;
	private final File dossierErreurs;
	private final Random masterRandom;
	private AtomicInteger nbThreadsAwaiting;
	private int nbThreadDone = 0;
	private boolean mainThreadWaiting;
	
	
	public BatchThreadManager(File dossierErreurs, long randomSeed)
	{
		service = Executors.newFixedThreadPool(50);
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
	
	public synchronized File getNewErrorDirectory(AlgoInstantiator algoInstantiator)
	{
		return GestFichiers.getNewErrorDirectory(dossierErreurs,algoInstantiator);
	}
	
	public synchronized void registerFatalException(Exception e)
	{
		File fatalErrorFile = new File(dossierErreurs,NomsFichiers.fatalError + NomsFichiers.extension);
		try {
			GestFichiers.writeErrorLogs(e,fatalErrorFile);
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		System.out.println("fatal error " + e + " registered, shutdown");
		if(!service.isShutdown())
		{
			e.printStackTrace();
			shutdown();
		}
	}
	
	public synchronized void plannifySimulation(SimulationStatCreator<?> creator,List<AlgoInstantiator> algos, PartitionGenerator partitionGen, ConfigSimu configSimu,SimuTaskReceiver<?> taskReceiver)
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
		int v = nbThreadsAwaiting.incrementAndGet();
		if(v > 120)
		{
			try 
			{
				mainThreadWaiting = true;
				wait();
				mainThreadWaiting = false;
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void decrementThreadsAwaiting()
	{
		int v = nbThreadsAwaiting.decrementAndGet();
		nbThreadDone ++;
		if(nbThreadDone % 50 == 0)
		{
			System.out.println("taskDone " + nbThreadDone);
		}
		if(v < 60 & mainThreadWaiting)
		{
			notify();
		}
	}
	
	public boolean closeAndWait(long timeoutInSeconds) throws InterruptedException
	{
		service.shutdown();
		return service.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS);
	}
	
	public void shutdown()
	{
		service.shutdownNow();
	}
}
