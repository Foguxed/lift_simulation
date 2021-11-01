package fr.fogux.lift_simulator.fichiers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;

import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.utils.Utils;

public class GestFichiers
{


    protected static LineNumberReader journalInput = null;


    public static Path getBasePath()
    {
        return Paths.get(Gdx.files.getLocalStoragePath() + NomsFichiers.fichiers);
    }

    public static void loadJournalInput(final File journal) throws FileNotFoundException
    {
        journalInput = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(journal))));
    }


    /*
     * public static String getPreviousJournalLine() { try {
     * journalInput.setLineNumber((journalInput.getLineNumber()-2)); return
     * journalInput.readLine(); } catch (IOException e) { e.printStackTrace(); }
     * return null; }
     */

    public static String getNextJournalLine()
    {
        try
        {
            return journalInput.readLine();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllJournalLines()
    {
        return journalInput.lines().collect(Collectors.toList());
    }


    public static void unloadVisualisationFiles() throws IOException
    {
        journalInput.close();
        journalInput = null;
    }


    public static void printIn(final BufferedWriter output, final String line)
    {
        try
        {
            output.write(line);
            output.newLine();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public static final File getRootFichier()
    {
        final File fichiers = new File(new File(Gdx.files.getLocalStoragePath()),NomsFichiers.fichiers);
        if(!fichiers.exists())
        {
            createBaseFile(fichiers);
        }
        return fichiers;
    }

    private static void createBaseFile(final File fichiers)
    {
        fichiers.mkdir();
        new File(fichiers, NomsFichiers.batchs).mkdir();
        new File(fichiers, NomsFichiers.simulations).mkdir();
        new File(fichiers, NomsFichiers.templates).mkdir();
    }

    public static BufferedWriter getJournalWriter(final File emptyFile, final DataTagCompound config) throws FileNotFoundException
    {
        final BufferedWriter writer = getNewWriter(emptyFile);
        printIn(writer, config.getValueAsString());
        return writer;
    }

    public static BufferedWriter gePersonnetStatWriter(final File emptyFile, final DataTagCompound config, final DataTagCompound globalPersResult) throws FileNotFoundException
    {
        final BufferedWriter writer = getNewWriter(emptyFile);
        printIn(writer, config.getValueAsString());
        printIn(writer, globalPersResult.getValueAsString());
        return writer;
    }

    public static FichierPartitionConfig getFichierPartitionConfig(final File partitionConfigFile) throws IOException
    {
        final BufferedReader r = getNewReader(partitionConfigFile);
        final DataTagCompound partitionConfig = new DataTagCompound(r.readLine());
        final DataTagCompound immeubleConfig = new DataTagCompound(r.readLine());
        r.close();
        return new FichierPartitionConfig(partitionConfig, immeubleConfig);
    }

    public static void writePartitionConfig(final FichierPartitionConfig fichierPartition, final File f) throws IOException
    {
        final BufferedWriter w = getNewWriter(f);
        printIn(w,fichierPartition.partitionConfig.getValueAsString());
        printIn(w,fichierPartition.immeubleConfig.getValueAsString());
        w.close();
    }

    public static FichierPartition getPartition(final File partitionFile) throws IOException
    {
        final BufferedReader r = getNewReader(partitionFile);
        final DataTagCompound partitionConfig = new DataTagCompound(r.readLine());
        final DataTagCompound immeubleConfig = new DataTagCompound(r.readLine());
        final List<EvenementPersonnesInput> events = new ArrayList<>();
        String str = r.readLine();
        while (str != null)
        {
            final EvenementPersonnesInput e = (EvenementPersonnesInput)Evenement.genererEvenement(str);
            events.add(e);
            str = r.readLine();
        }
        r.close();
        return new FichierPartition(partitionConfig, immeubleConfig, events);
    }
    
    public static FichierGraphBatchConfig getGraphBatchConfig(final File graphBatchConfig) throws IOException
    {
    	final BufferedReader r = getNewReader(graphBatchConfig);
    	FichierGraphBatchConfig retour = new FichierGraphBatchConfig(
    			r.readLine(),
    			r.readLine(),
    			r.readLine());
    	r.close();
    	return retour;
    }
    
    public static void writePartition(final FichierPartition fichierPartition, final File f) throws IOException
    {
        final BufferedWriter w = getNewWriter(f);
        printIn(w,fichierPartition.partitionConfig.getValueAsString());
        printIn(w,fichierPartition.immeubleConfig.getValueAsString());
        for(final EvenementPersonnesInput e : fichierPartition.evenements)
        {
            printIn(w,e.getPartitionEventString());
        }
        w.close();
    }

    public static void writeConfigSimu(ConfigSimu csimu, final File dossierParent) throws IOException
    {
    	final DataTagCompound simuC = new DataTagCompound();
    	csimu.printOnlySimuFieldsIn(simuC);
    	printFirstLine(getConfigSimuFile(dossierParent), simuC.getValueAsString());
    }
    
    public static BufferedReader getNewReader(final File f) throws FileNotFoundException
    {
        return new BufferedReader(new InputStreamReader(new FileInputStream(f)));
    }

    public static BufferedWriter getNewWriter(final File f) throws FileNotFoundException
    {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
    }

    public static File createNewPartitionDossier(final File situationFile)
    {
        final File partitions = new File(situationFile,NomsFichiers.partitions);
        partitions.mkdir();
        final File f = getNewIndetedFile(partitions, NomsFichiers.partition, "");
        creerDossier(f);
        return f;
    }
    
    public static synchronized File getNewErrorDirectory(File dossierErreurs, AlgoInstantiator algoInstantiator)
    {
    	final File dossier = new File(dossierErreurs, algoInstantiator.getName());
    	if(!dossier.exists())
    	{
        	creerDossier(dossier);
    	}
    	final File f = getNewIndetedFile(dossier, NomsFichiers.erreur, "");
    	creerDossier(f);
        return f;
    }

    public static File createPartitionTxt(final File parentFile)
    {
        return new File(parentFile, NomsFichiers.partition + NomsFichiers.extension);
    }

    private static File getNewIndetedFile(final File dossier, final String prefixe, final String suffixe)
    {
        final File[] fs = dossier.listFiles();
        
        int maxIndex = 0;
        System.out.println("dossier " + dossier);
        for (final File element : fs)
        {
            final String str = element.getName();
            if(str.startsWith(prefixe) && str.endsWith(suffixe))
            {
                final String val = str.substring(str.lastIndexOf('_')+1, str.length() - suffixe.length());
                final Integer v = Utils.safeParseInt(val);
                if(v!= null &&v > maxIndex)
                {
                    maxIndex = v;
                }
            }
        }
        return new File(dossier,prefixe +"_" + (maxIndex + 1) + suffixe);
    }

    public static File createExecutionDirectory(final File dossierPartition)
    {
        final File f = getNewIndetedFile(dossierPartition, NomsFichiers.execution, "");
        creerDossier(f);
        return f;
    }
    
    public static File createBatchErrorLogDirectory(final File dossierExecBatch)
    {
        final File f = new File(dossierExecBatch,NomsFichiers.errors);
        deleteIfExists(f);
        creerDossier(f);
        return f;
    }
    
    public static File createGraphBatchResultFile(final File dossierExecBatch)
    {
    	final File f = new File(dossierExecBatch, NomsFichiers.graph + NomsFichiers.extension);
    	deleteIfExists(f);
    	return f;
    }
    
    public static File createBatchResultFile(final File dossierExecBatch)
    {
    	final File f = new File(dossierExecBatch, NomsFichiers.resultats + NomsFichiers.extension);
    	deleteIfExists(f);
    	return f;
    }

    public static File createSimuPRGMdirectory(final File dossierExecution, final AlgoInstantiator algorithme)
    {
        final File f = new File(dossierExecution,NomsFichiers.algo + "_" + algorithme.getName() + NomsFichiers.extension);
        creerDossier(f);
        return f;
    }

    public static File getErrorInfosFile(final File dossier)
    {
    	return new File(dossier, NomsFichiers.err_infos + NomsFichiers.extension);
    }
    
    public static void writeErrorLogs(Exception e, File fileErrorLogger) throws FileNotFoundException
    {
    	PrintStream p = new PrintStream(fileErrorLogger);
    	e.printStackTrace(p);
    	p.close();
    }
    
    public static File createJournalFile(final File dossierPrgmExecution)
    {
        final File f = new File(dossierPrgmExecution,NomsFichiers.journal + "_" + dossierPrgmExecution.getName());

        return f;
    }
    
    public static File copyFileWithPrefixe(final File toBeCopied, String prefix)
    {
    	if(prefix == "")
    	{
    		throw new IllegalArgumentException("prefix should not be null");
    	}
    	else
    	{
    		return new File(toBeCopied.getParentFile(),prefix + toBeCopied.getName());
    	}
    }

    public static File createStatsPersonnesFile(final File dossierPrgmExecution)
    {
        final File f = new File(dossierPrgmExecution,NomsFichiers.stats_personnes + "_" + dossierPrgmExecution.getName());

        return f;
    }
    
    public static File getConfigSimuFile(final File directory)
    {
    	final File f = new File(directory,NomsFichiers.config_simulationn + NomsFichiers.extension);
    	return f;
    }
    
    private static void creerDossier(final File f)
    {
        if(!f.mkdir())
        {
            throw new SimulateurException("Le dossier suivant ne devrait pas exister " + f.getAbsolutePath());
        }
    }
    
    private static void deleteIfExists(final File f)
    {
    	if(f.exists())
    	{
    		f.delete();
    	}
    }
    
    public static String getFirstLine(final File f) throws IOException
    {
        final BufferedReader r = getNewReader(f);
        final String str = r.readLine();
        r.close();
        return str;
    }
    
    public static DataTagCompound getFirstCompound(final File f) throws IOException
    {
    	return new DataTagCompound(getFirstLine(f));
    }

    public static void printFirstLine(final File f, final String str) throws IOException
    {
        final BufferedWriter w = getNewWriter(f);
        printIn(w, str);
        w.close();
    }
    private static File[] getFiles(final File directory, final String prefixe)
    {
        return directory.listFiles(new PrefixeFilter(prefixe));
    }

    
    public static File getUniqueFile(final File directory, final String prefixe)
    {
        final File[] farray = getFiles(directory,prefixe);
        if(farray.length != 1)
        {
            throw new SimulateurException("Il devrait y avoir un unique " + prefixe + " dans " + directory);
        }
        else
        {
            return farray[0];
        }
    }
}
