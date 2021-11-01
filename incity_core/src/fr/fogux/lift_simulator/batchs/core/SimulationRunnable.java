package fr.fogux.lift_simulator.batchs.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.lift_simulator.PartitionSimu;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.FichierPartition;
import fr.fogux.lift_simulator.fichiers.FichierPartitionConfig;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
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


    public <T extends SimulationStat> SimulationRunnable(final BatchThreadManager manager,final SimulationStatCreator<?> collecter, final List<AlgoInstantiator> algos, final ConfigSimu configSimu, final PartitionGenerator partitionGen, final SimuTaskReceiver<?> taskCompletionChecker, final long randomSeed)
    {
        this.manager = manager;
        this.algos = algos;
        this.collecter = collecter;
        this.partitionGen = partitionGen;
        csimu = configSimu;
        this.randomSeed = randomSeed;
        tChecker = taskCompletionChecker;
    }

    @Override
    public void run()
    {
        try
        {
            innerRun();
        }
        catch(final Exception e)
        {
            manager.registerFatalException(e);
        }
    }

    private void innerRun()
    {
        final Random r = new Random(randomSeed);
        final PartitionSimu ps = partitionGen.generer(r);
        final List<Object> stats = new ArrayList<>();
        boolean failed = false;
        for(final AlgoInstantiator a : algos)
        {
            try
            {
                final Simulation s = new Simulation(a, csimu, ps);
                s.start();
                stats.add(collecter.produceStat(s));
            }
            catch(final Exception e)
            {
                failed = true;
                try
                {
                    final File dossierErreur = manager.getNewErrorDirectory(a);
                    final File f = GestFichiers.getErrorInfosFile(dossierErreur);
                    GestFichiers.writeErrorLogs(e, f);
                    exportSimulationState(dossierErreur, partitionGen.generer(new Random(randomSeed)));
                }
                catch (final Exception efatal)
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

    private void exportSimulationState(final File f, final PartitionSimu pSimu) throws IOException
    {
        GestFichiers.writeConfigSimu(csimu, f);
        final File dossierPartition = GestFichiers.createNewPartitionDossier(f);
        final File partition = new File(dossierPartition,NomsFichiers.partition + NomsFichiers.extension);
        System.out.println("pgenconfig " + partitionGen.getConfig());
        GestFichiers.writePartition(new FichierPartition(FichierPartitionConfig.fromConfig(partitionGen.getConfig()), pSimu), partition);

    }

}
