package fr.fogux.lift_simulator;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.evenements.animation.EvenementErreur;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtageSimu;
import fr.fogux.lift_simulator.physic.ImmeubleSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.stats.StatAccumulator;

public class Simulation
{
    protected final ImmeubleSimu immeuble;
    protected final Algorithme p;
    protected final ConfigSimu c;
    protected final GestionnaireDeTachesSimu gestioTaches;
    protected final List<PersonneSimu> personnesList;
    protected final BufferedWriter journalOutput;

    public Simulation(final AlgoInstantiator prgminstantiator, final ConfigSimu config, final PartitionSimu partition, final BufferedWriter journalOutput)
    {
        this.journalOutput = journalOutput;
        p = prgminstantiator.getPrgm(new InterfacePhysique(this),config);
        c = config;
        personnesList = new ArrayList<>();
        gestioTaches = new GestionnaireDeTachesSimu(this,journalOutput != null,partition);
        immeuble = new ImmeubleSimu(this);
    }

    public BufferedWriter getJournalOutput()
    {
        return journalOutput;
    }

    public boolean doPrint()
    {
        return gestioTaches.policy.doPrint();
    }

    public ImmeubleSimu getImmeubleSimu()
    {
        return immeuble;
    }

    public ConfigSimu getConfig()
    {
        return c;
    }

    public void run(final StatAccumulator<PersonneSimu> statPersAccumulator)
    {
        try
        {
            gestioTaches.runExecuting();
        }
        catch (final SimulateurAcceptableException e)
        {
            new EvenementErreur(e.getMessage()).print(this);
        }
        accumulateStatsPers(statPersAccumulator);
    }

    public GestionnaireDeTachesSimu getGestio()
    {
        return gestioTaches;
    }

    public long getTime()
    {
        return gestioTaches.innerTime();
    }

    public Algorithme getPrgm()
    {
        return p;
    }

    public void inputPersonne(final EtageSimu etageDepart, final int destination)
    {
        final PersonneSimu newP = new PersonneSimu(this, personnesList.size(), destination, etageDepart);
        personnesList.add(newP);
        newP.choisirDestination();
    }

    public PersonneSimu getPersonne(final int id)
    {
        return personnesList.get(id);
    }

    public int getPersonneListSize()
    {
        return personnesList.size();
    }

    public void printPersStats(final BufferedWriter fOutput)
    {
        for (final PersonneSimu pers : personnesList)
        {
            final DataTagCompound compound = new DataTagCompound();
            pers.printStats(compound);
            GestFichiers.printIn(fOutput, compound.getValueAsString());
        }
    }

    private void accumulateStatsPers(final StatAccumulator<PersonneSimu> statAcc)
    {
        for (final PersonneSimu pers : personnesList)
        {
            statAcc.accumulateStat(pers);
        }
    }

}
