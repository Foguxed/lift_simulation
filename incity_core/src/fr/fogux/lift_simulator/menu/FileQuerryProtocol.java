package fr.fogux.lift_simulator.menu;

import java.io.File;

import fr.fogux.lift_simulator.screens.FileResearchScreen;

public interface FileQuerryProtocol
{
    /**
     *
     * @param screen
     * @param directory
     * @param researchDeep fichiers is 1
     */
    void updateFileScreen(FileResearchScreen screen, File directory, int researchDeep);

    void onClic(FileResearchScreen screen,File f, int researchDeep);
}
