package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;


public class Immeuble
{
    protected List<EtageDeDepart> departs;
    protected double valMax;
    protected Random r;
    
    public Immeuble(List<EtageDeDepart> etageDeparts)
    {
        this.departs = etageDeparts;
        valMax = 0;
        for(EtageDeDepart depart : etageDeparts)
        {
            valMax+= depart.getProba();
        }
        r = new Random();
    }
    
    public void genererPersonnesInput(int nbPersonnesDeplacees ,long dureeMilis)
    {
        
        List<PersonneInput> retour = new ArrayList<PersonneInput>();
        int nbADeplacer = nbPersonnesDeplacees;
        System.out.println("a deplacer" + nbADeplacer);
        PersonneInput temp;
        while(nbADeplacer > 0)
        {
            temp = getRandomEtage().getRandomPersonInupt((long)(r.nextDouble() * dureeMilis));
            retour.add(temp);
            nbADeplacer -= temp.getNbPersonnes();
        }
        Collections.sort(retour);
        retour.stream().forEach(p -> GestionnaireDeFichiers.newPartitionLine(p.getStringVal()));
    }

    
    protected EtageDeDepart getRandomEtage()
    {
        double val = r.nextDouble()*valMax;
        for(EtageDeDepart etage : departs)
        {
            val -= etage.getProba();
            if(val < 0)
            {
                return etage;
            }
        }
        System.out.println("PAS NORMAL");
        return null;
    }
}
