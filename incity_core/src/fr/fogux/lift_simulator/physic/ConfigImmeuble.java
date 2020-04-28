package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class ConfigImmeuble
{
    protected final int niveauMin;
    protected final int niveauMax;
    protected final int[] repartAsc;

    public ConfigImmeuble(final int niveauMin, final int niveauMax, final int[] repartAsc)
    {
        this.niveauMin = niveauMin;
        this.niveauMax = niveauMax;
        this.repartAsc = repartAsc;
    }

    public ConfigImmeuble(final DataTagCompound compound)
    {
        niveauMin = compound.getInt(TagNames.niveauMin);
        niveauMax = compound.getInt(TagNames.niveauMax);
        final List<DataTagCompound> list = compound.getCompoundList(TagNames.repartAscenseurs);
        repartAsc = new int[list.size()];
        for(int i = 0; i < repartAsc.length; i ++)
        {
            repartAsc[i] = list.get(i).getInt(TagNames.val);
        }
    }

    public void printFieldsIn(final DataTagCompound compound)
    {
        printOnlyImmeubleFieldsIn(compound);
    }

    public void printOnlyImmeubleFieldsIn(final DataTagCompound compound)
    {
        compound.setInt(TagNames.niveauMin, niveauMin);
        compound.setInt(TagNames.niveauMax, niveauMax);

        final List<DataTagCompound> list = new ArrayList<>(repartAsc.length);
        for (final int element : repartAsc)
        {
            final DataTagCompound d = new DataTagCompound();
            d.setInt(TagNames.val, element);
            list.add(d);
        }
        compound.setCompoundList(TagNames.repartAscenseurs, list);
    }

    public int getNiveauMin()
    {
        return niveauMin;
    }

    public int getNiveauMax()
    {
        return niveauMax;
    }

    public int[] getRepartAscenseurs()
    {
        return repartAsc;
    }

    @Override
    public String toString()
    {
        final DataTagCompound c = new DataTagCompound();
        printFieldsIn(c);
        return c.getValueAsString();
    }
}
