package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;

public interface FileOutput
{
    void printLine(DataTagCompound compound);
    void printLine(String str);
}
