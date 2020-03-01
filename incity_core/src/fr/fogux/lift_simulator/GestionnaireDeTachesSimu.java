package fr.fogux.lift_simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;

import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;

public class GestionnaireDeTachesSimu extends GestionnaireDeTaches
{
    protected final TreeMap<Long,List<Evenement>> taches = new TreeMap<Long,List<Evenement>>();
    protected Set<EnregistreurDeDuree> enregs = new HashSet<EnregistreurDeDuree>();
    protected List<Evenement> bufferPartition = new ArrayList<Evenement>();
    protected long realtTimeAtStart;
    protected long lastInputTime;
    
    
    public void executerDans(Evenement tache,long timeRelatif)
    {
        executerA(tache,innerTime + timeRelatif);
    }
    
    public void executerA(Evenement tache,long timeAbsolu)
    {
        //System.out.println("evenement registered " + tache.getClass() + " " + timeAbsolu);
        if(timeAbsolu < innerTime)
        {
            Gdx.app.log("GestonnaireDeTaches", "executerA erreur, timeAbsolu inferieur au temps actuel");
        }
        else
        {
            List<Evenement> tempList = taches.get(timeAbsolu);
            if(tempList != null)
            {
                tempList.add(tache);
            }
            else
            {
                List<Evenement> list = new ArrayList<Evenement>();
                list.add(tache);
                System.out.println("nouveau time events");
                taches.put(timeAbsolu, list);
            }
            
        }
    }
    
    public void CancelEvenement(Evenement ev)
    {
        List<Evenement> temp = taches.get(ev.getTime());
        if(temp != null)
        {
            temp.remove(ev);
        }
        
    }
    
    public void runExecuting()
    {
        realtTimeAtStart = System.currentTimeMillis();
        //refillBuffer();
        inputAllEvents();
        lastInputTime = taches.lastKey();
        
        System.out.println("fin des imputs");
        while(!taches.isEmpty())
        {
            Entry<Long,List<Evenement>> entry = taches.firstEntry();
            final Long tempsEcoule = entry.getKey()-innerTime;
            innerTime = entry.getKey();
            if(tempsEcoule != 0)
            {
                for(EnregistreurDeDuree obj:enregs)
                {
                    obj.tempsEcoulee(tempsEcoule);
                }
            }
            executerChaqueEvenement(entry.getValue());
            taches.pollFirstEntry();
            
            if(innerTime > lastInputTime+1000*60*60*3)
            {
                throw new SimulateurAcceptableException(" plus de 3 heures ecoulees depuis la derniere arriveee ");
            }
        }
    }
    
    public void executerChaqueEvenement(List<Evenement> list)
    {
        while(!list.isEmpty())
        {
            /*if(System.currentTimeMillis() - realtTimeAtStart > 15*1000)
            {
                throw new SimulateurException("Timeout");//TODO faire quelquechose?
            }*/
            list.get(0).simuRun();
            /*if(*/list.remove(0);/* == bufferPartition.get(0))
            {
                bufferPartition.remove(0);
                addOnePartitionLine();
            }*/
        }
    }
    
    /*protected static void refillBuffer()
    {
        while(bufferPartition.size() < 10)
        {
            addOnePartitionLine();
        }
    }
    
    protected static void addOnePartitionLine()
    {
        bufferPartition.add(Evenement.genererEvenement(GestionnaireDeFichiers.getNextPartitionLine()));
    }*/
    
    protected void inputAllEvents()
    {
        String str = GestionnaireDeFichiers.getNextPartitionLine();
        while(str != null)
        {
            System.out.println("newEvent " + str);
            Evenement.genererEvenement(str);
            str = GestionnaireDeFichiers.getNextPartitionLine();
        }
    }


    @Override
    protected boolean marcheArriereEnCours()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void update()
    {
        
    }

    @Override
    public void dispose()
    {
        
    }
    
    
    
}
