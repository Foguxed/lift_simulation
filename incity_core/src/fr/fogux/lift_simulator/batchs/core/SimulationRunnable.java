package fr.fogux.lift_simulator.batchs.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sun.org.glassfish.external.statistics.impl.StatsImpl;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.animation.EvenementErreur;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.FichierPartition;
import fr.fogux.lift_simulator.fichiers.FichierPartitionConfig;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.partition_creation.PartitionCreator;
import fr.fogux.lift_simulator.partition_creation.PartitionGenerator;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.stats.SimulationStat;

public class SimulationRunnable implements Runnable
{
	private final BatchThreadManager manager;
	private final SimulationStatCreator<?> collecter;
	private final List<AlgoInstantiator> algos;
	private final ConfigSimu csimu;
	private final PartitionGenerator partitionGen;
	private final SimuTaskReceiver<?> tChecker;
	private final long randomSeed;
	
	
	public <T extends SimulationStat> SimulationRunnable(BatchThreadManager manager,SimulationStatCreator<?> collecter, List<AlgoInstantiator> algos, ConfigSimu configSimu, PartitionGenerator partitionGen, SimuTaskReceiver<?> taskCompletionChecker, long randomSeed) 
	{
		this.manager = manager;
		this.algos = algos;
		this.collecter = collecter;
		this.partitionGen = partitionGen;
		this.csimu = configSimu;
		this.randomSeed = randomSeed;
		this.tChecker = taskCompletionChecker;
	}
	
	@Override
	public void run() 
	{
		try
		{
			innerRun();
		}
		catch(Exception e)
		{
			manager.registerFatalException(e);
		}
	}
	
	private void innerRun()
	{
		final Random r = new Random(randomSeed);
		PartitionSimu ps = partitionGen.generer(r);
		List<Object> stats = new ArrayList<>();
		boolean failed = false;
		for(AlgoInstantiator a : algos)
		{
			try
			{
				Simulation s = new Simulation(a, csimu, ps);
				s.run();
				stats.add(collecter.produceStat(s));
			}
			catch(Exception e)
			{
				failed = true;
				try 
				{
					File dossierErreur = manager.getNewErrorDirectory(a);
					File f = GestFichiers.getErrorInfosFile(dossierErreur);
					GestFichiers.writeErrorLogs(e, f);
					exportSimulationState(dossierErreur, partitionGen.generer(new Random(randomSeed)));
				} 
				catch (Exception efatal) 
				{
					manager.registerFatalException(efatal);
				}
				
				if(e instanceof SimulateurAcceptableException)
				{
					manager.registerException();
				}
				else
				{
					manager.registerFatalException(e);
				}
			}
		}
		if(failed)
		{
			tChecker.taskFailed();
		}
		else
		{
			tChecker.uncheckedTaskCompleted(stats);
		}
		manager.decrementThreadsAwaiting();
	}
	
	private void exportSimulationState(File f, PartitionSimu pSimu) throws IOException
	{
		GestFichiers.writeConfigSimu(csimu, f);
		File dossierPartition = GestFichiers.createNewPartitionDossier(f);
		File partition = new File(dossierPartition,NomsFichiers.partition + NomsFichiers.extension);
		GestFichiers.writePartition(new FichierPartition(FichierPartitionConfig.fromConfig(partitionGen.getConfig()), pSimu), partition);
	
	}
	
}
