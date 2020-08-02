package fr.fogux.lift_simulator.menu;

import java.io.File;
import java.io.IOException;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.screens.FileResearchScreen;

public class FileQuerryToArray implements FileQuerryProtocol
{
    protected File config_simulation;

    @Override
    public void updateFileScreen(final FileResearchScreen screen, final File directory, final int researchDeep)
    {
        screen.registerFichiersAsButton(directory.listFiles());
    }

    @Override
    public void onClic(final FileResearchScreen screen, final File f, final int researchDeep)
    {
    	System.out.println("Onclick " + f.getName());
        if(f.getName().endsWith(NomsFichiers.extension))// le dossier contenant la partition et les differents dossier par execution
        {
        	System.out.println("goCopy " + f.getName());
            try
            {
                Simulateur.getArrayCopy(f);
            } catch (final IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(f.isDirectory())
        {
            screen.subSearch(f);
        }
    }
}
