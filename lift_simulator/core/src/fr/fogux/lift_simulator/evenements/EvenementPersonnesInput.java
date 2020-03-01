package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementPersonnesInput extends PrintableEvenement
{
    protected int nbPersonnes;
    protected int destination;
    protected int etage;
    
    public EvenementPersonnesInput(long time,DataTagCompound data)
    {
        super(time,true);
        this.nbPersonnes = data.getInt(TagNames.nbPersonnes);
        this.destination = data.getInt(TagNames.destination);
        this.etage = data.getInt(TagNames.etage);
    }
    
    public void simuRun()
    {
        super.simuRun();
        Simulateur.getImmeubleSimu().getEtage(etage).arriveeDe(nbPersonnes, destination);
    }
    
    

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        compound.setInt(TagNames.nbPersonnes,nbPersonnes);
        compound.setInt(TagNames.destination,destination);
        compound.setInt(TagNames.etage, etage);
    }
    

    @Override
    public void visuRun()
    {
        if(GestionnaireDeTaches.marcheArriere())
        {
            System.out.println("retirer " + nbPersonnes);
            PersonneVisu.removeLastPersonnes(nbPersonnes);
        }
        else
        {
            System.out.println("arrivee " + nbPersonnes +" etage " + etage +  " desti " + destination);
            //System.out.println("update evenement input " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
            Simulateur.getImmeubleVisu().getEtage(etage).arriveeDe(nbPersonnes, destination);
            
        }
    }

}
