package fr.fogux.lift_simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;

import fr.fogux.lift_simulator.evenements.AnimatedEvent;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;

public class GestionnaireDeTachesSimu extends GestionnaireDeTaches
{
    protected final Simulation simu;

    protected final TreeMap<Long, List<Evenement>> taches = new TreeMap<>();
    protected Set<EnregistreurDeDuree> enregs = new HashSet<>();
    protected List<Evenement> bufferPartition = new ArrayList<>();
    protected long realtTimeAtStart;
    protected long lastInputTime;

    protected PrintPolicy policy;

    public GestionnaireDeTachesSimu(final Simulation simu, final boolean doPrintEvents, final PartitionSimu partition)
    {
        this.simu = simu;
        if(doPrintEvents)
        {
            policy = new PrintPolicy()
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
            policy = new PrintPolicy()
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
        for(final Evenement e : partition.getInputs())
        {
            executerA(e,e.getTime());
        }
    }

    public void executerA(final Evenement tache, final long timeAbsolu)
    {
        // System.out.println("evenement registered " + tache.getClass() + " " +
        // timeAbsolu);
        if (timeAbsolu < innerTime)
        {
            Gdx.app.log("GestonnaireDeTaches", "executerA erreur, timeAbsolu inferieur au temps actuel");
        } else
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
        policy.onRegister(tache, this, timeAbsolu);
    }

    public void CancelEvenement(final Evenement ev, final long registeredTime)
    {
        final List<Evenement> temp = taches.get(registeredTime);
        if (temp != null)
        {
            temp.remove(ev);
        }
        policy.onCancel(ev, this, registeredTime);
    }

    @Override
    public void runExecuting()
    {
        System.out.println("run executing on " + taches.size() + " events ");
        realtTimeAtStart = System.currentTimeMillis();
        lastInputTime = taches.lastKey();

        System.out.println("fin des imputs");
        simu.getPrgm().init();
        while (!taches.isEmpty())
        {
            final Entry<Long, List<Evenement>> entry = taches.firstEntry();
            final Long tempsEcoule = entry.getKey() - innerTime;
            innerTime = entry.getKey();
            if (tempsEcoule != 0)
            {
                for (final EnregistreurDeDuree obj : enregs)
                {
                    obj.tempsEcoulee(tempsEcoule);
                }
            }
            executerChaqueEvenement(entry.getValue());
            taches.pollFirstEntry();

            if (innerTime > lastInputTime + 1000 * 60 * 60 * 3)
            {
                throw new SimulateurAcceptableException(" plus de 3 heures ecoulees depuis la derniere arriveee ");
            }
        }
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

}
