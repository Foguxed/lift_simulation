package fr.fogux.lift_simulator.menu;

import java.io.File;
import java.io.IOException;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.screens.FileResearchScreen;

public class FileQuerryPartitionGen implements FileQuerryProtocol
{

    @Override
    public void updateFileScreen(final FileResearchScreen screen, final File directory, final int researchDeep)
    {
        screen.registerFichiersAsButton(directory.listFiles());
    }

    @Override
    public void onClic(final FileResearchScreen screen, final File f, final int researchDeep)
    {
        if(researchDeep == 2)
        {
            try
            {
                Simulateur.executePartitionCreation(f);
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
