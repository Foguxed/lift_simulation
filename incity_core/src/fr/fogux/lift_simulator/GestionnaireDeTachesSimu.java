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

    protected boolean paused = true;

    protected Evenement toRerun;

    public long pingTime;

    public GestionnaireDeTachesSimu(final Simulation newSimulation, final GestionnaireDeTachesSimu shadowed)
    {
        super(shadowed);
        simu = newSimulation;
        toRerun = shadowed.toRerun;
        policy = choosePolyci(false);
        lastInputTime = shadowed.lastInputTime;
        partition = Collections.emptyIterator();
        long time;
        for(final Entry<Long,List<Evenement>> entry : shadowed.taches.entrySet())
        {
            time = entry.getKey();
            for(final Evenement e : entry.getValue())
            {
                if(e.shadowable(entry.getKey()))
                {
                    putEventInTaches(e,time);
                }
            }
        }
        //System.out.println("shadowing taches, shadowed " + shadowed.taches + " mestaches " + taches);
        //pingtime depend de l'algo
    }



    public GestionnaireDeTachesSimu(final Simulation simu, final boolean doPrintEvents, final PartitionSimu partition)
    {
        this.simu = simu;
        toRerun = null;
        policy = choosePolyci(doPrintEvents);
        this.partition = partition.getInputIterator();
    }

    public void pause()
    {
        paused = true;
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
        if(toRerun != null)
        {
            System.out.println("un truc a rerun " + toRerun);
            toRerun.reRun(simu);
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
            final Evenement e = list.remove(0);
            policy.onSimuRun(e);// attention on ne peut pas faire sur la fin de la liste car le run peut ajouter des events
            if(paused)
            {
                toRerun = e;
                break;
            }
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

    private PrintPolicy choosePolyci(final boolean doPrintEvents)
    {
        if(doPrintEvents)
        {
            return new PrintPolicy()
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
