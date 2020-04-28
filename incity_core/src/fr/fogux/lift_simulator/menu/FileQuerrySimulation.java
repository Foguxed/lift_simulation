package fr.fogux.lift_simulator.menu;

import java.io.File;
import java.io.IOException;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.fichiers.PrefixeFilter;
import fr.fogux.lift_simulator.screens.FileResearchScreen;

public class FileQuerrySimulation implements FileQuerryProtocol
{
    protected File config_simulation;

    @Override
    public void updateFileScreen(final FileResearchScreen screen, final File directory, final int researchDeep)
    {
        if(researchDeep == 3)// dans petit_immeuble_demo
        {
            screen.registerFichiersAsButton(directory.listFiles(new PrefixeFilter(NomsFichiers.partitions)));
            config_simulation = GestFichiers.getUniqueFile(directory, NomsFichiers.config_simulationn);
            screen.displayInfo("config_simulation selectionnee " + config_simulation.getName());
        }
        else
        {
            screen.registerFichiersAsButton(directory.listFiles());
        }
    }

    @Override
    public void onClic(final FileResearchScreen screen, final File f, final int researchDeep)
    {
        if(researchDeep == 4)// le dossier contenant la partition et les differents dossier par execution
        {
            try
            {
                Simulateur.executerSimulation(f, Simulateur.getAllAlgos(), config_simulation);
            } catch (final IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            screen.subSearch(f);
        }
    }

}
