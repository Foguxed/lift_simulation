package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.FileOutput;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.structure.AscId;

public class ImmeubleSimu
{
    protected EtageSimu[] etages;
    protected List<AscenseurSimu>[] ascenseurs;

    public final int niveauMin;

    public ImmeubleSimu(final Simulation simu)
    {
        final int niveauMax = simu.getConfig().getNiveauMax();
        niveauMin = simu.getConfig().getNiveauMin();
        if (niveauMin > 0 || niveauMax < 0)
        {
            throw new SimulateurException("niveau 0 obligatoire");
        }
        final int taille = niveauMax - niveauMin + 1;
        etages = new EtageSimu[taille];
        for (int i = 0; i < taille; i++)
        {
            etages[i] = new EtageSimu(simu,i + niveauMin);
        }
        final int[] repartAsc = simu.getConfig().getRepartAscenseurs();
        ascenseurs = new ArrayList[repartAsc.length];

        for (int j = 0; j < repartAsc.length; j++)
        {
            AscenseurSimu ascPrecedent = null;
            ascenseurs[j] = new ArrayList<>(repartAsc[j]);
            for(int i = 0; i < repartAsc[j] ; i++ )
            {
                final AscenseurSimu newAsc = new AscenseurSimu(simu, new AscId(j, i),i);
                ascenseurs[j].add(newAsc);
                if(i >= 1)
                {
                    newAsc.setAscInferieur(ascenseurs[j].get(i-1));
                }
                if(ascPrecedent != null)
                {
                    ascPrecedent.setAscSuperieur(newAsc);
                }
                ascPrecedent = newAsc;
            }
        }
    }

    public int getEtageMin()
    {
        return niveauMin;
    }

    public int getEtageMaxNonInclu()
    {
        return niveauMin + etages.length;
    }

    public void printAscStats(final FileOutput output)
    {
        for(final List<AscenseurSimu> list : ascenseurs)
        {
            for (final AscenseurSimu asc : list)
            {
                final DataTagCompound compound = new DataTagCompound();
                asc.printStats(compound);
                output.printLine(compound);
            }
        }
    }

    public AscenseurSimu getAscenseur(final AscId id)
    {
        return ascenseurs[id.monteeId].get(id.stackId);
    }

    public EtageSimu getEtage(final int niveau)
    {
        return etages[niveau - niveauMin];
    }

    public boolean dansImmeuble(final int niveau)
    {
        return niveau >= niveauMin && niveau < niveauMin + etages.length;
    }

}
