package fr.fogux.lift_simulator.fichiers;

import java.io.File;
import java.io.FilenameFilter;

public class PrefixeFilter implements FilenameFilter
{
    protected final String prefixe;
    public PrefixeFilter(final String prefixe)
    {
        this.prefixe = prefixe;
    }

    @Override
    public boolean accept(final File dir, final String name)
    {
        return name.startsWith(prefixe);
    }

}
