package fr.fogux.lift_simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import fr.fogux.lift_simulator.evenements.AnimatedEvent;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementInterruptSimulation;
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

    protected EventRunPolicy policy;

    protected boolean paused = true;
    protected boolean interrupted = false;

    protected Evenement toRerun;

    public Evenement currentEv;

    public long pingTime;

    public GestionnaireDeTachesSimu(final Simulation newSimulation, final GestionnaireDeTachesSimu shadowed, final boolean usejournOutput)
    {
        this(newSimulation,shadowed,usejournOutput,shadowed.taches.entrySet());
    }

    public GestionnaireDeTachesSimu(final Simulation newSimulation, final GestionnaireDeTachesSimu shadowed, final boolean usejournOutput, final long dureeMax)
    {
        this(newSimulation,shadowed,usejournOutput,shadowed.taches.headMap(shadowed.innerTime() + dureeMax, true).entrySet());
        new EvenementInterruptSimulation(shadowed.innerTime() + dureeMax).runOn(simu);

    }

    public GestionnaireDeTachesSimu(final Simulation newSimulation, final GestionnaireDeTachesSimu shadowed, final boolean usejournOutput, final Set<Entry<Long,List<Evenement>>> tachesToShadow)
    {
        super(shadowed);
        simu = newSimulation;
        toRerun = shadowed.toRerun;

        policy = choosePolicy(usejournOutput);
        lastInputTime = shadowed.lastInputTime;
        partition = Collections.emptyIterator();
        long time;
        for(final Entry<Long,List<Evenement>> entry : tachesToShadow)
        {
            time = entry.getKey();
            for(final Evenement e : entry.getValue())
            {
                if(e.shadowable(entry.getKey(),policy))
                {
                    putEventInTaches(e,time);
                }
            }
        }
        /*
        if(shadowed.currentEv != null && shadowed.currentEv.shadowable(shadowed.innerTime(), policy))
        {
            final List<Evenement> l = taches.get(shadowed.innerTime());
            if(l != null)
            {
                l.add(0,shadowed.currentEv);
            }
            else
            {
                putEventInTaches(shadowed.currentEv,shadowed.innerTime());
            }
        }*/
        //System.out.println("shadowing taches, shadowed " + shadowed.taches + " mestaches " + taches);
        //pingtime depend de l'algo
    }

    public GestionnaireDeTachesSimu(final Simulation simu, final boolean doPrintEvents, final PartitionSimu partition)
    {
        this.simu = simu;
        toRerun = null;
        policy = choosePolicy(doPrintEvents);
        this.partition = partition.getInputIterator();
    }

    public void thenpause()
    {
        paused = true;
    }

    public void interrupt()
    {
        interrupted = true;
        toRerun = currentEv;
        thenpause();
    }



    public void executerA(final Evenement tache, final long timeAbsolu)
    {
        //System.out.println("evenement registered " + tache + " " + timeAbsolu + " ceci " + hashCode());
        //new Throwable().printStackTrace();
        if (timeAbsolu < innerTime)
        {
            throw new SimulateurException("Evenement " + tache + " enregistre pour execution a " + timeAbsolu + " qui est inferieur à innertime " + innerTime);
        }
        else
        {
            putEventInTaches(tache,timeAbsolu);
        }
        policy.onRegister(tache, this, timeAbsolu);
        //System.out.println("tachesDesormais " + taches);
    }

    private void putEventInTaches(final Evenement tache, final long timeAbsolu)
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
        //System.out.println("cancelEvent " + ev);
        final List<Evenement> temp = taches.get(registeredTime);
        if (temp == null || !temp.remove(ev))
        {
            for(final Entry<Long,List<Evenement>> tlist :taches.entrySet())
            {
                System.out.println(tlist.getKey() + " " + tlist.getValue());
            }
            throw new SimulateurException("unable to cancel " + ev + " at registeredTime " + registeredTime + " innerTime " + innerTime + " evenements a registeredTimetime " + temp);
        }
        policy.onCancel(ev, this, registeredTime);
    }

    @Override
    public void init()
    {
        pingTime = simu.getPrgm().init();
        if(pingTime > 0)
        {
            final EvenementPingAlgorithme premierPing = new EvenementPingAlgorithme(innerTime + pingTime);
            executerA(premierPing, premierPing.getTime());
        }
        forecastNextPersInput();
    }

    public void resume()
    {
        //System.out.println("taches initiales " + taches + " ceci " + hashCode());
        if(!paused)
        {
            throw new SimulateurException("GestioTaches should be paused before any resume");
        }
        paused = false;
        interrupted = false;
        if(toRerun != null)
        {
            currentEv = toRerun;
            toRerun.reRun(simu);
            currentEv = null;
            if(paused)
            {
                return;
            }
            else
            {
                toRerun = null;
            }
        }
        while (!taches.isEmpty())
        {
            //System.out.println("taches " + taches);
            final Entry<Long, List<Evenement>> entry = taches.firstEntry();
            executeEntry(entry);
            if(paused)
            {
                break;
            }
            taches.pollFirstEntry();
        }
    }



    public void forecastNextPersInput()
    {
        if(partition.hasNext())
        {
            final EvenementPersonnesInput input = partition.next();
            executerA(input,input.getTime());
            lastInputTime = input.getTime();
        }
        else
        {
            lastInputTime = innerTime;
        }
    }

    private void executeEntry(final Entry<Long,List<Evenement>> entry)
    {
        executeEvents(entry.getKey(),entry.getValue());
    }

    private void executeEvents(final long key, final List<Evenement> value)
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
        //System.out.println("ev execs " + list + " time " + innerTime);
        while (!list.isEmpty())
        {
            currentEv = list.remove(0);
            policy.onSimuRun(currentEv);// attention on ne peut pas faire sur la fin de la liste car le run peut ajouter des events
            if(paused)
            {
                break;
            }
        }
        currentEv = null;
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


    @Override
    public String toString()
    {
        return " toRerun: " + toRerun + " taches: " + taches;
    }

    public int nbRemainingEventsTimes()
    {
        return taches.size();
    }

    private EventRunPolicy choosePolicy(final boolean doPrintEvents)
    {
        if(doPrintEvents)
        {
            return new EventRunPolicy()
            {

                @Override
                public void onSimuRun(final Evenement e)
                {
                    e.print(simu);
                    if(e instanceof AnimatedEvent && ((AnimatedEvent)e).doNotSimuRun(simu.getTime()))
                    {
                    }
                    else
                    {
                        e.simuRun(simu);
                    }
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
            return new EventRunPolicy()
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
