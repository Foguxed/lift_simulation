package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigImmeuble;

public class ConfigPartitionGen extends ConfigImmeuble
{
    protected final int nbPersonnesDeplacees;



    public ConfigPartitionGen(final int niveauMin, final int niveauMax, final int[] repartAsc, final int nbPersonnesDeplacees)
    {
        super(niveauMin, niveauMax, repartAsc);
        this.nbPersonnesDeplacees = nbPersonnesDeplacees;
    }

    public ConfigPartitionGen(final DataTagCompound compound)
    {
        super(compound);
        nbPersonnesDeplacees = compound.getInt(TagNames.nbPersonnesDeplacees);
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setInt(TagNames.nbPersonnesDeplacees, nbPersonnesDeplacees);
    }
}
