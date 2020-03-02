package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.evenements.animation.EvenementCreationImmeuble;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;

public class ImmeubleSimu
{
    protected EtageSimu[] etages;
    protected AscenseurSimu[] ascenseurs;
    public final int niveauMin;

    public ImmeubleSimu(int niveauMin, int niveauMax, int nbAsc)
    {
        new EvenementCreationImmeuble(niveauMin, niveauMax, nbAsc).print();
        if (niveauMin > 0 || niveauMax < 0)
        {
            throw new SimulateurException("niveau 0 obligatoire");
        }
        int taille = niveauMax - niveauMin + 1;
        this.niveauMin = niveauMin;
        etages = new EtageSimu[taille];
        for (int i = 0; i < taille; i++)
        {
            etages[i] = new EtageSimu(i + niveauMin);
        }
        ascenseurs = new AscenseurSimu[nbAsc];
        for (int j = 0; j < nbAsc; j++)
        {
            ascenseurs[j] = new AscenseurSimu(j + 1, TimeConfig.nbPersMaxAscenseur());
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

    public void printAscStats()
    {
        for (AscenseurSimu asc : ascenseurs)
        {
            DataTagCompound compound = new DataTagCompound();
            asc.printStats(compound);
            GestionnaireDeFichiers.printStatAscenseur(compound.getValueAsString());
        }
    }

    public AscenseurSimu getAscenseur(int id)
    {
        return ascenseurs[id - 1];
    }

    public EtageSimu getEtage(int niveau)
    {
        return etages[niveau - niveauMin];
    }

    public boolean dansImmeuble(int niveau)
    {
        return niveau >= niveauMin && niveau < niveauMin + etages.length;
    }

}
