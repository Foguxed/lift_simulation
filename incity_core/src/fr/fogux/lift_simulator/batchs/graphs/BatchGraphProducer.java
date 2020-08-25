package fr.fogux.lift_simulator.batchs.graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.batchs.core.SimuBatch;
import fr.fogux.lift_simulator.batchs.core.SimuTaskReceiver;
import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.DynamicString;
import fr.fogux.lift_simulator.fichiers.FichierGraphBatchConfig;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.fichiers.strings.StringProvider;
import fr.fogux.lift_simulator.fichiers.strings.StringStringProvider;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.partition_creation.PartitionGenInstantiator;
import fr.fogux.lift_simulator.partition_creation.PartitionGenerator;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.stats.StandardSimulationStat;
import fr.fogux.lift_simulator.stats.StandardStatCreator;

public class BatchGraphProducer extends SimuBatch
{

    public static final String Name = "graphProducer";

    protected DynamicString strImmeuble;
    protected DynamicString strPartition;
    protected DynamicString strSimu;

    protected BufferedWriter graphWriter;
    protected final int echantillonage;

    protected int identifiantPoint = 0;
    protected int nextToWrite = 0;

    protected PriorityQueue<PointGraph> queue;


    public BatchGraphProducer(final File dossierDuBatch, final DataTagCompound batchConfig) throws IOException
    {
        super(dossierDuBatch,batchConfig.getLong(TagNames.seed), batchConfig.getInt(TagNames.nbThreads));
        echantillonage = batchConfig.getInt(TagNames.echantillonage);
        final File configFile = GestFichiers.getUniqueFile(dossierDuBatch, NomsFichiers.graph_batch_config);
        final FichierGraphBatchConfig graphBatchConfigFile = GestFichiers.getGraphBatchConfig(configFile);
        graphWriter = getGraphOutput();
        strImmeuble = new DynamicString(graphBatchConfigFile.config_immeuble_raw);
        strPartition = new DynamicString(graphBatchConfigFile.config_partition_raw);
        strSimu = new DynamicString(graphBatchConfigFile.config_simu_raw);
        queue = new PriorityQueue<>();
        System.out.println("RandomSeed is " + batchConfig.getLong(TagNames.seed));
    }

    private List<StringProvider> getAbscisse()
    {
        final StringStringProvider separationValeurs = new StringStringProvider(",");
        final StringStringProvider separationSections = new StringStringProvider(" ");
        final List<List<? extends StringProvider>> lists = new ArrayList<>();
        lists.add(StringProvider.separe(strImmeuble.getVariators(), separationValeurs));
        lists.add(StringProvider.separe(strPartition.getVariators(), separationValeurs));
        lists.add(StringProvider.separe(strSimu.getVariators(), separationValeurs));
        return StringProvider.concatener(lists, separationSections);
    }

    private BufferedWriter getGraphOutput() throws FileNotFoundException
    {
        final File graphFile = GestFichiers.createGraphBatchResultFile(dossier);
        return GestFichiers.getNewWriter(graphFile);
    }

    protected void tryVidageQueue()
    {
        while(!queue.isEmpty() && queue.peek().id == nextToWrite)
        {
            // threadsafe car il n'y a qu'un seul premier element correct
            try
            {
                graphWriter.write(queue.poll().getString() + "\n");
            } catch (final IOException e)
            {
                e.printStackTrace();
                throw new SimulateurException("unexpected IO Exception while writing graph batch results " + e.getMessage());
            }
            nextToWrite ++;
        }
    }

    public synchronized void registerCompleted(final PointGraph point)
    {
        queue.add(point);
        tryVidageQueue();
    }

    private void writeAlgoOrder(final List<AlgoInstantiator> algos)
    {
        String str = "";
        for(final AlgoInstantiator a : algos)
        {
            str += a.getName() + ",";
        }
        str += "\n";
        try
        {
            graphWriter.write(str);
        } catch (final IOException e)
        {
            e.printStackTrace();
            throw new SimulateurException("unexpected IO Exception");
        }
    }

    @Override
    public void runBatch()
    {
        final long initialTime = System.currentTimeMillis();
        try
        {

            final boolean b = dessiner(60*60*1000);
            if(!b)
            {
                System.out.println("timeout des points");
            }
            try
            {
                manager.closeAndWait(20*60);
                System.out.println("completed in " + (System.currentTimeMillis() - initialTime) + " millis");
            } catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        catch(final Exception e)
        {
            e.printStackTrace();
        }


        try {
            graphWriter.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        manager.shutdown();
    }

    protected boolean dessiner(final long timeOut)
    {
        final long initialTime = System.currentTimeMillis();
        final List<AlgoInstantiator> algos = Simulateur.getAllAlgos();
        writeAlgoOrder(algos);

        final SimulationStatCreator<StandardSimulationStat> creator = new StandardStatCreator();

        final PartitionGenInstantiator pgenInstantiator = new PartitionGenInstantiator(strPartition,new DataTagCompound(strImmeuble.getString()));

        final List<StringProvider> abscisses = getAbscisse();
        do
        {
            final DataTagCompound compoundImmeuble = new DataTagCompound(strImmeuble.getString());
            do
            {
                final PartitionGenerator pGen = pgenInstantiator.getPartitionGen();
                do
                {
                    if(System.currentTimeMillis() - initialTime > timeOut)
                    {
                        return false;
                    }
                    final DataTagCompound compoundSimu = new DataTagCompound(strSimu.getString());
                    compoundSimu.mergeWith(compoundImmeuble);
                    final ConfigSimu cSimu = new ConfigSimu(compoundSimu);
                    final SimuTaskReceiver<?> point = new PointGraph(this, echantillonage, identifiantPoint,"(" + StringProvider.getFullString(abscisses) + ")");
                    identifiantPoint ++;
                    for(int i = 0; i < echantillonage; i ++)
                    {
                        manager.plannifySimulation(creator, algos, pGen, cSimu, point);
                    }
                }
                while(!strSimu.next());
            }
            while(!pgenInstantiator.cycleNext(compoundImmeuble));
        }
        while(!strImmeuble.next());
        return true;
    }
}
