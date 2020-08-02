package fr.fogux.lift_simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.batchs.core.SimuBatch;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.Evenements;
import fr.fogux.lift_simulator.evenements.animation.EvenementErreur;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.FichierPartition;
import fr.fogux.lift_simulator.fichiers.FichierPartitionConfig;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.BasicAlgoInstantiator;
import fr.fogux.lift_simulator.mind.RPsimpleAlgo2.RPsimpleAlgo2;
import fr.fogux.lift_simulator.mind.independant.AlgoBasique2;
import fr.fogux.lift_simulator.partition_creation.ConfigPartitionHomogene;
import fr.fogux.lift_simulator.partition_creation.HomogenePartitionGen;
import fr.fogux.lift_simulator.partition_creation.PartitionGenerator;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.screens.GameScreen;
import fr.fogux.lift_simulator.screens.MenuScreen;
import fr.fogux.lift_simulator.stats.StandardPersStatAccumulator;
import fr.fogux.lift_simulator.stats.StandardSimulationStat;
import fr.fogux.lift_simulator.stats.StandardStatCreator;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class Simulateur extends Game
{
    SpriteBatch batch;
    Texture img;
    protected static Simulateur instance;
    public static boolean animationTime = false;
    protected GameScreen scr;

    public static final Comparator<Evenement> EVENT_COMPARATOR = new Comparator<Evenement>()
    {

        @Override
        public int compare(final Evenement o1, final Evenement o2)
        {
            return (int)(o1.getTime() - o2.getTime());
        }
    };

    private static final Map<String,AlgoInstantiator> ALGORITHMES = initAlgorithmes();


    private static Map<String,AlgoInstantiator> initAlgorithmes()
    {
        final Map<String,AlgoInstantiator> map = new HashMap<>();
        //addAlg(ProgrammeBasique.class,"prgmBasique",map);
        //addAlg(RPsimpleAlgo2.class,"RPsimpleAlgo",map);
        addAlg(AlgoBasique2.class,"AlgoBasique2",map);
        return map;
    }

    private static void addAlg(final AlgoInstantiator algoInstantiator,final Map<String,AlgoInstantiator> map)
    {
        map.put(algoInstantiator.getName(), algoInstantiator);
    }

    private static void addAlg(final Class<? extends Algorithme> simpleAlgoClass, final String name, final Map<String,AlgoInstantiator> map)
    {
        addAlg(new BasicAlgoInstantiator(simpleAlgoClass, name),map);
    }

    private static void printPartitionGeneratorType(final PartitionGenerator pGen, final DataTagCompound compound)
    {
        String str = "INCONNU";
        if(pGen.getClass() == HomogenePartitionGen.class)
        {
            str = HomogenePartitionGen.NAME;
        }

        compound.setString(TagNames.partitionGenType, str);
    }

    public static AlgoInstantiator getInstantiator(final String name)
    {
        return ALGORITHMES.get(name);
    }

    public static List<AlgoInstantiator> getAllAlgos()
    {
        return new ArrayList<>(ALGORITHMES.values());
    }

    /*************************************************************************************************************************/


    public static Simulateur getInstance()
    {
        return instance;
    }

    @Override
    public void create()
    {
        lancerMenu();
    }


    private void tests()
    {
        final File f = GestFichiers.getRootFichier();
        System.out.println(f.getAbsolutePath());
    }

    private void lancerMenu()
    {
        instance = this;
        batch = new SpriteBatch();
        Evenements.init();
        menuScreen();
    }

    protected void menuScreen()
    {
        AssetsManager.loadMainMenuAssets();
        final MenuScreen menu = new MenuScreen(this);
        setScreen(menu);
        menu.init();
    }

    @Override
    public void render()
    {
        super.render();
        /*
         * Gdx.gl.glClearColor(1, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
         * batch.begin(); //batch.draw(img, 0, 0);
         *
         * batch.end();
         */
    }

    @Override
    public void dispose()
    {
        // batch.dispose();
        // img.dispose();
        // GestionnaireDeFichiers.unloadSimulationFiles();
    }

    public SpriteBatch getBatch()
    {
        return batch;
    }

    public static Simulateur getSimulateur()
    {
        return instance;
    }

    public GameScreen getGameScreen()
    {
        return scr;
    }

    public void doVisualisation(final File fileJournal) throws IOException
    {
        PersonneVisu.initClass();
        AssetsManager.loadAnimationAssets();

        GestFichiers.loadJournalInput(fileJournal);

        /*final DataTagCompound simuConfig = new DataTagCompound(GestFichiers.getNextJournalLine());
        final DataTagCompound immeublePartConfig = new DataTagCompound(GestFichiers.getNextJournalLine());
        simuConfig.mergeWith(immeublePartConfig);*/
        final ConfigSimu configSimu = new ConfigSimu(new DataTagCompound(GestFichiers.getNextJournalLine()));

        new AnimationProcess(configSimu);
        final AnimationProcess animp = new AnimationProcess(configSimu);
        scr = new GameScreen(this,animp );
        setScreen(scr);
        scr.init();

        GestFichiers.unloadVisualisationFiles();
        System.out.println("fin init animation");
    }
    
    public static void getArrayCopy(final File fichier) throws IOException
    {
    	toArrayTxt(fichier,GestFichiers.copyFileWithPrefixe(fichier, "as_array_"));
    }
    
    private static void toArrayTxt(final File input, final File output) throws IOException
    {
    	BufferedReader reader = GestFichiers.getNewReader(input);
    	BufferedWriter writer = GestFichiers.getNewWriter(output);
    	String str = reader.readLine();
    	String separator = "	";
    	DataTagCompound d = null;
    	while(str != null)
    	{
    		String lineToWrite = "";
    		if(str.contains("["))
    		{
    			long time = Evenement.time(str);
    			lineToWrite += time + separator;
    		}
    		d = new DataTagCompound(str);
    		writer.write(lineToWrite + d.toValues(separator,d.keysByAlphabeticalOrder()) + "\n");
    		str = reader.readLine();
    	}
    	writer.write(toString(d.keysByAlphabeticalOrder(),separator)+"\n");
    	reader.close();
    	writer.close();
    }
    
    private static String toString(List<String> strs,String separator)
    {
    	String result = "";
    	for(String str : strs)
    	{
    		result += str + separator;
    	}
    	return result;
    }
    
    public static void executePartitionCreation(final File dossierSituation) throws IOException
    {
        System.out.println("Debut partition creation");
        final File configPartition = GestFichiers.getUniqueFile(dossierSituation, NomsFichiers.config_gen_partition);
        final File partitionFile = GestFichiers.createPartitionTxt(GestFichiers.createNewPartitionDossier(dossierSituation));

        final FichierPartitionConfig fichierPConfig = GestFichiers.getFichierPartitionConfig(configPartition);

        final DataTagCompound totalData = fichierPConfig.partitionConfig.copy();
        totalData.mergeWith(fichierPConfig.immeubleConfig);

        System.out.println("partition compound " + totalData.getString(TagNames.inputEventProvider));
        final PartitionSimu simuParti = PartitionGenerator.fromCompound(totalData).generer(new Random());

        final FichierPartition fichierPartition = new FichierPartition(fichierPConfig, simuParti);
        
        
        System.out.println("fin partition creation");
        GestFichiers.writePartition(fichierPartition, partitionFile);
        System.out.println("fichier enregistre " + partitionFile.getAbsolutePath());
    }

    public static void launchSimulation(final File dossierPartition, final List<AlgoInstantiator> algorithmes, final File configSimu, final boolean animationApres)
    {
        try
        {
            executerSimulation(dossierPartition, algorithmes, configSimu,animationApres);
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void executerBatch(final File dossierDuBatch) throws IOException
    {
    	File configBatchFile = GestFichiers.getUniqueFile(dossierDuBatch, NomsFichiers.config_batch);
    	SimuBatch batch = SimuBatch.fromCompound(dossierDuBatch,GestFichiers.getFirstCompound(configBatchFile));
    	batch.run();
    }
    
    public static void executerSimulation(final File dossierPartition, final List<AlgoInstantiator> algorithmes, final File configSimu, final boolean animationApres) throws IOException
    {
        System.out.println("debut Simulation");
        final File partition = GestFichiers.getUniqueFile(dossierPartition, NomsFichiers.partition);
        final File executionFile = GestFichiers.createExecutionDirectory(dossierPartition);

        final FichierPartition fPartition = GestFichiers.getPartition(partition);
        final DataTagCompound config = new DataTagCompound(GestFichiers.getFirstLine(configSimu));
        config.mergeWith(fPartition.immeubleConfig);


        final ConfigSimu c = new ConfigSimu(config);

        System.out.println("nb algos simu "  + algorithmes.size());
        File dernierJournal = null;
        for(final AlgoInstantiator alg : algorithmes)
        {
            System.out.println("simu sur algo " + alg.getName());
            final File dossierPrgm = GestFichiers.createSimuPRGMdirectory(executionFile, alg);

            dernierJournal = GestFichiers.createJournalFile(dossierPrgm);

            final BufferedWriter journalOutput = GestFichiers.getJournalWriter(dernierJournal, config);
            final Simulation simu = new Simulation(alg, c, new PartitionSimu(fPartition.evenements), journalOutput);
            
            StandardSimulationStat stat = null;
            System.out.println("debut run simulation");
            try
            {
            	simu.run();
            	stat = new StandardStatCreator().produceStat(simu);
            }
		    catch (final SimulateurAcceptableException e)
		    {
		        new EvenementErreur(e.getMessage()).print(simu);
		    }
            /*try
            {

            }
            catch(final SimulateurException simuExep)
            {
                simuExep.printStackTrace();
            }*/
            System.out.println("fin run simulation");
            journalOutput.close();
            final SimuResult result;
            if(stat == null)
            {
            	result = new SimuResult(true, 0, 0, 0);
            }
            else
            {
            	result = new SimuResult(stat);
            }
             
            final DataTagCompound resultComPound = new DataTagCompound();
            result.printFieldsIn(resultComPound);

            final BufferedWriter personneStatsWriter = GestFichiers.gePersonnetStatWriter(GestFichiers.createStatsPersonnesFile(dossierPrgm), config,resultComPound);
            simu.printPersStats(personneStatsWriter);
            personneStatsWriter.close();
        }
        if(animationApres && dernierJournal != null)
        {
            System.out.println(dernierJournal.getName());
            instance.doVisualisation(dernierJournal);
        }
    }

    public static void safeGenerateTemplates()
    {
        try
        {
            generateTemplates();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void generateTemplates() throws IOException
    {
        final File fileTemplate = new File(GestFichiers.getRootFichier(),NomsFichiers.templates);
        final File fileSimuConfig = new File(fileTemplate,"template_" + NomsFichiers.config_simulationn + NomsFichiers.extension);
        final File filePartitionConfig = new File(fileTemplate,"template_"+ NomsFichiers.config_gen_partition +"_homogene" + NomsFichiers.extension);

        final float HAUTEUR_NIVEAU = 2.50f;


        final long DUREE_SORTIE_ENTREE_PERSONNES = 1200;
        final float ASCENSEUR_SPEED = 2.3f/(1000f); // niveau par mili-secondes
        final float ACCELERATION = (0.30f / HAUTEUR_NIVEAU) / (1000f * 1000f); // niveau par milis au carre
        final long DUREE_PORTES = 2000;
        final int NB_PERS_MAX_ASC = 5;


        final float MARGE_INTER_ASCENSEUR = (0.8f); // doit être inferieur a un etage
        final float MARGE_SUP_INTER_ASCENSEUR = MARGE_INTER_ASCENSEUR + 0.001f * MARGE_INTER_ASCENSEUR;


        final int NIVEAU_MIN = -3;
        final int NIVEAU_MAX = 20;
        final int[] ASCENSEURS = {2,2,2,2};

        final ConfigSimu templateConfig =
            new ConfigSimu(NIVEAU_MIN, NIVEAU_MAX, ASCENSEURS, ASCENSEUR_SPEED, ACCELERATION, DUREE_SORTIE_ENTREE_PERSONNES, DUREE_PORTES, NB_PERS_MAX_ASC, MARGE_INTER_ASCENSEUR);

        final DataTagCompound immeubleC = new DataTagCompound();
        templateConfig.printOnlyImmeubleFieldsIn(immeubleC);

        final DataTagCompound simuC = new DataTagCompound();
        templateConfig.printOnlySimuFieldsIn(simuC);

        final ConfigPartitionHomogene configPartition = new ConfigPartitionHomogene(NIVEAU_MIN, NIVEAU_MAX, ASCENSEURS, 500, 30*60*1000);

        final DataTagCompound partiC = new DataTagCompound();
        configPartition.printFieldsIn(partiC);

        partiC.removeKeysCommunes(immeubleC);

        System.out.println(partiC);
        System.out.println(immeubleC);

        final FichierPartitionConfig fichierPartitionConfig = new FichierPartitionConfig(partiC, immeubleC);
        GestFichiers.writePartitionConfig(fichierPartitionConfig, filePartitionConfig);
        GestFichiers.printFirstLine(fileSimuConfig, simuC.getValueAsString());
    }

    public static void println(final Object o)
    {
        //System.out.println(o);
    }
}
