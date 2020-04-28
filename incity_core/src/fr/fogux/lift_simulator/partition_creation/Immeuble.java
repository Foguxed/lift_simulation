package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Immeuble
{
    protected List<EtageDeDepart> departs;
    protected double valMax;
    protected Random r;

    public Immeuble(final List<EtageDeDepart> etageDeparts)
    {
        departs = etageDeparts;
        valMax = 0;
        for (final EtageDeDepart depart : etageDeparts)
        {
            valMax += depart.getProba();
        }
        r = new Random();
    }

    public void genererPersonnesInput(final int nbPersonnesDeplacees, final long dureeMilis)
    {

        final List<PersonneInput> retour = new ArrayList<>();
        int nbADeplacer = nbPersonnesDeplacees;
        PersonneInput temp;
        while (nbADeplacer > 0)
        {
            temp = getRandomEtage().getRandomPersonInupt((long) (r.nextDouble() * dureeMilis));
            retour.add(temp);
            nbADeplacer -= temp.getNbPersonnes();
        }
        Collections.sort(retour);
        //retour.stream().forEach(p -> GestFichiers.newPartitionLine(p.getStringVal()));
    }

    protected EtageDeDepart getRandomEtage()
    {
        double val = r.nextDouble() * valMax;
        for (final EtageDeDepart etage : departs)
        {
            val -= etage.getProba();
            if (val < 0)
            {
                return etage;
            }
        }
        System.out.println("PAS NORMAL");
        return null;
    }
}
