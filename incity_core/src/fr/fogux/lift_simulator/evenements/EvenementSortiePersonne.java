package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class EvenementSortiePersonne extends AnimatedEvent
{

    protected final int personneId;
    
    
    public EvenementSortiePersonne(long time, DataTagCompound data)
    {
        super(time, data);
        
        personneId =  data.getInt(TagNames.personneId);
    }
    
    public EvenementSortiePersonne(long timeAbsolu,int personneId)
    {
        super(timeAbsolu,true);
        System.out.println("Event Sortie de la personne " + personneId);
        this.personneId = personneId;
    }

    @Override
    public void simuRun()
    {
        super.simuRun();
        PersonneSimu.getPersonne(personneId).sortirDeAscenseur();
    }
    
    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setInt(TagNames.personneId,personneId);
    }

    @Override
    protected void runAnimation(long timeDebut, long animationDuree)
    {
        System.out.println(" run anim sortie ");
        PersonneVisu.getPersonne(personneId).animationSortie(timeDebut, animationDuree);
    }

    @Override
    protected void sortieAnimation()
    {
        // TODO Auto-generated method stub
        
    }

}
