package fr.fogux.lift_simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import fr.fogux.lift_simulator.evenements.AnimatedEvent;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.evenements.EvenementPingAlgorithme;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.exceptions.SimulateurException;

public class GestionnaireDeTachesSimu extends GestionnaireDeTaches
{
    protected final Simulation simu;
    protected final Iterator<EvenementPersonnesInput> partition;

    protected final TreeMap<Long, List<Evenement>> taches = new TreeMap<>();
    
    protected long lastInputTime;

    protected PrintPolicy policy;

    public long pingTime;
    
    public GestionnaireDeTachesSimu(final Simulation newSimulation, GestionnaireDeTachesSimu shadowed)
    {
    	this.simu = newSimulation;
    	this.policy = choosePolyci(false);
    	this.lastInputTime = shadowed.lastInputTime;
    	this.partition = Collections.emptyIterator();
    	long time;
    	for(Entry<Long,List<Evenement>> entry : shadowed.taches.entrySet())
    	{
    		time = entry.getKey();
    		for(Evenement e : entry.getValue())
    		{
    			if(!(e instanceof EvenementPersonnesInput))
    			{
        			putEventInTaches(e,time);
    			}
    		}
    	}
    	//pingtime depend de l'algo
    }
    
    
    
    public GestionnaireDeTachesSimu(final Simulation simu, final boolean doPrintEvents, final PartitionSimu partition)
    {
        this.simu = simu;
        this.policy = choosePolyci(doPrintEvents);
        this.partition = partition.getInputIterator();
    }
    
    public void executerA(final Evenement tache, final long timeAbsolu)
    {
        // System.out.println("evenement registered " + tache.getClass() + " " +
        // timeAbsolu);
        if (timeAbsolu < innerTime)
        {
            throw new SimulateurException("Evenement " + tache + " enregistre pour execution a " + timeAbsolu + " qui est inferieur à innertime " + innerTime);
        }
        else
        {
        	putEventInTaches(tache,timeAbsolu);
        }
        policy.onRegister(tache, this, timeAbsolu);
    }
    
    private void putEventInTaches(Evenement tache, long timeAbsolu)
    {
    	final List<Evenement> tempList = taches.get(timeAbsolu);
        if (tempList != null)
        {
            tempList.add(tache);
        } else
        {
            final List<Evenement> list = new ArrayList<>();
            list.add(tache);
            taches.put(timeAbsolu, list);
        }
    }
    
    public void CancelEvenement(final Evenement ev, final long registeredTime)
    {
        final List<Evenement> temp = taches.get(registeredTime);
        if (temp == null || !temp.remove(ev))
        {
            throw new SimulateurException("unable to cancel " + ev + " at registeredTime " + registeredTime + " innerTime " + innerTime + " evenements a registeredTimetime " + temp);
        }
        policy.onCancel(ev, this, registeredTime);
    }

    @Override
    public void runExecuting()
    {
        pingTime = simu.getPrgm().init();
        if(pingTime > 0)
        {
            final EvenementPingAlgorithme premierPing = new EvenementPingAlgorithme(innerTime + pingTime);
            executerA(premierPing, premierPing.getTime());
        }
        forecastNextPersInput();
        while (!taches.isEmpty())
        {
            final Entry<Long, List<Evenement>> entry = taches.firstEntry();
            executeEntry(entry);
            taches.pollFirstEntry();
        }
    }
    
    public void forecastNextPersInput()
    {
    	if(partition.hasNext())
        {
        	EvenementPersonnesInput input = partition.next();
        	executerA(input,input.getTime());
        }
    	else
    	{
    		lastInputTime = innerTime;
    	}
    }
    
    private void executeEntry(Entry<Long,List<Evenement>> entry)
    {
    	executeEvents(entry.getKey(),entry.getValue());
    }
    
    private void executeEvents(long key, List<Evenement> value)
    {
        innerTime = key;
        if (beyondEndOFTime(innerTime))
        {
            throw new SimulateurAcceptableException(" plus de 3 heures ecoulees depuis la derniere arriveee ");
        }
        executerChaqueEvenement(value);
    }
    
    private boolean beyondEndOFTime(final long time)
    {
        return time > lastInputTime + 1000 * 60 * 60 * 3;
    }

    public void executerChaqueEvenement(final List<Evenement> list)
    {
        while (!list.isEmpty())
        {
        	policy.onSimuRun(list.get(0));// attention on ne peut pas faire sur la fin de la liste car le run peut ajouter des events
            list.remove(0);
        }
    }

    /*
     * protected static void refillBuffer() { while(bufferPartition.size() < 10) {
     * addOnePartitionLine(); } }
     *
     * protected static void addOnePartitionLine() {
     * bufferPartition.add(Evenement.genererEvenement(GestionnaireDeFichiers.
     * getNextPartitionLine())); }
     */

    @Override
    public boolean marcheArriereEnCours()
    {
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


    public int nbRemainingEventsTimes()
    {
        return taches.size();
    }
    
    private PrintPolicy choosePolyci(boolean doPrintEvents)
    {
    	if(doPrintEvents)
        {
            return new PrintPolicy()
            {

                @Override
                public void onSimuRun(final Evenement e)
                {
                    if(e instanceof AnimatedEvent && ((AnimatedEvent)e).doNotSimuRun(simu.getTime()))
                    {
                    }
                    else
                    {
                        e.simuRun(simu);
                    }
                    e.print(simu);
                }

                @Override
                public void onRegister(final Evenement e, final GestionnaireDeTachesSimu gestio, final long registeredTime)
                {
                    e.onPrintRegister(gestio, registeredTime);
                }

                @Override
                public void onCancel(final Evenement e, final GestionnaireDeTachesSimu gestio, final long registeredTime)
                {
                    e.onPrintCancel(gestio, registeredTime);
                }

                @Override
                public boolean doPrint()
                {
                    return true;
                }
            };
        }
        else
        {
            return new PrintPolicy()
            {

                @Override
                public void onSimuRun(final Evenement e)
                {
                    e.simuRun(simu);
                }

                @Override
                public void onRegister(final Evenement e, final GestionnaireDeTachesSimu gestio, final long registeredTime)
                {
                }

                @Override
                public void onCancel(final Evenement e, final GestionnaireDeTachesSimu gestio, final long time)
                {
                }

                @Override
                public boolean doPrint()
                {
                    return false;
                }
            };
        }
    }
}
