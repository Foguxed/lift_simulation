package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigImmeuble;

public class ConfigPartitionGen extends ConfigImmeuble implements Compoundable
{
    protected final int nbPersonnesDeplacees;



    public ConfigPartitionGen(final int niveauMin, final int niveauMax, final int[] repartAsc, final int nbPersonnesDeplacees)
    {
        super(niveauMin, niveauMax, repartAsc);
        this.nbPersonnesDeplacees = nbPersonnesDeplacees;
        if(nbPersonnesDeplacees == 0)
        {
        	throw new SimulateurException("nbPersDeplaceees should never be " +nbPersonnesDeplacees );
        }
    }

    public ConfigPartitionGen(final DataTagCompound compound)
    {
        super(compound);
        nbPersonnesDeplacees = compound.getInt(TagNames.nbPersonnesDeplacees);
        if(nbPersonnesDeplacees == 0)
        {
        	throw new SimulateurException("nbPersDeplaceees should never be " +nbPersonnesDeplacees );
        }
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setInt(TagNames.nbPersonnesDeplacees, nbPersonnesDeplacees);
    }
}
