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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.List;

public class GestionnaireDeFichiers
{
    protected static final String nomDuSousFichier = "fichiers";

    protected static String nomDeDossier;
    protected static String nomDuProgrammeEnCours;

    protected static BufferedReader partitionInput = null;
    protected static BufferedWriter journalOutput = null;
    protected static BufferedWriter personneStatOutput = null;
    protected static BufferedWriter ascenseurStatOutput = null;

    protected static LineNumberReader journalInput = null;

    protected static BufferedWriter partitionOutput = null;

    public static void setSimulationPath(String nomDossier, String nomProgramme)
    {
        System.out.println(
            "path output exmeple " + nomDuSousFichier + "/" + nomDossier + nomProgramme + "/journal.txt");

        nomDeDossier = nomDuSousFichier + "/" + nomDossier;
        nomDuProgrammeEnCours = nomProgramme;
    }

    public static void loadSimulationFiles()
    {
        // File f = new
        // File("C:/Users/Florent/DevEclipse/workspace/lift_simulator/partition.txt")
        System.out.println("t");
        File f = new File(nomDeDossier + "/" + nomDuProgrammeEnCours);
        if (!f.exists())
        {
            f.mkdirs();
        }
        final File partition = new File(nomDeDossier + "/partition.txt");
        final File journal = new File(nomDeDossier + "/" + nomDuProgrammeEnCours + "/journal.txt");

        try
        {
            partitionInput = new BufferedReader(new InputStreamReader(new FileInputStream(partition)));
            journalOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journal)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // File f = new File(GestionnaireDeFichiers.class.getResource().toString());

        System.out.println("Chemin absolu du fichier : " + partition.getAbsolutePath());

        System.out.println("Nom du fichier : " + partition.getName());

        System.out.println("Est-ce qu'il existe ? " + partition.exists());

        System.out.println("Est-ce un répertoire ? " + partition.isDirectory());

        System.out.println("Est-ce un fichier ? " + partition.isFile());

    }

    public static Path getBasePath()
    {
        return Paths.get(Gdx.files.getLocalStoragePath() + nomDuSousFichier);
    }

    public static void loadVisualisationFile(File dossier)
    {
        System.out.println(dossier.getPath());
        loadVisualisationFile(dossier.getPath());
    }

    public static void loadVisualisationFile(String dossierName, String prgmName)
    {
        loadVisualisationFile(nomDuSousFichier + "/" + dossierName + "/" + prgmName);
    }

    protected static void loadVisualisationFile(String relativePath)
    {
        File journal = new File(relativePath + "/journal.txt");
        System.out.println(
            journal + " " + partitionInput + " " + journalOutput + " " + personneStatOutput + " " + ascenseurStatOutput
                + " " + journalInput);
        try
        {
            journalInput
                = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(journal))));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadStatFiles()
    {
        try
        {
            personneStatOutput = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        new File(nomDeDossier + "/" + nomDuProgrammeEnCours + "/statisitques_pers.txt"))));
            ascenseurStatOutput = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        new File(nomDeDossier + "/" + nomDuProgrammeEnCours + "/statisitques_asc.txt"))));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static String getNextPartitionLine()
    {
        try
        {
            return partitionInput.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
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
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllJournalLines()
    {
        return (List<String>) journalInput.lines().collect(Collectors.toList());
    }

    public static void unloadSimulationFiles()
    {
        try
        {
            partitionInput.close();
            partitionInput = null;
            journalOutput.close();
            journalOutput = null;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void unloadVisualisationFiles()
    {
        try
        {
            journalInput.close();
            journalInput = null;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void unloadStatFiles()
    {
        try
        {
            personneStatOutput.close();
            personneStatOutput = null;
            ascenseurStatOutput.close();
            ascenseurStatOutput = null;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void noticeNewLineInJournal(String str)
    {
        // System.out.println("newLine " + str);
        try
        {
            journalOutput.write(str);
            journalOutput.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void printStatAscenseur(String str)
    {
        try
        {
            ascenseurStatOutput.write(str);
            ascenseurStatOutput.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void printStatPersonne(String str)
    {
        try
        {
            personneStatOutput.write(str);
            personneStatOutput.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void dispose()
    {
        if (partitionInput != null)
        {
            unloadSimulationFiles();
        }
        if (journalInput != null)
        {
            unloadVisualisationFiles();
        }
    }

    public static void loadPartitionCreationFile()
    {
        final File partition = new File(getBasePath() + "/partition.txt");
        try
        {
            partitionOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partition)));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void unloadPartitionCreationFile()
    {
        try
        {
            partitionOutput.close();
            partitionInput = null;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void newPartitionLine(String str)
    {
        try
        {
            partitionOutput.write(str);
            partitionOutput.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
