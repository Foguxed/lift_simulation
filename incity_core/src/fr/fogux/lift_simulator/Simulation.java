package fr.fogux.lift_simulator;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.ImmeubleSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class Simulation
{
    protected ImmeubleSimu immeuble;
    protected final Algorithme p;
    protected final ConfigSimu c;
    protected final GestionnaireDeTachesSimu gestioTaches;
    protected final List<PersonneSimu> personnesList;
    protected final List<PersonneSimu> potentiellementNonLivree;
    protected final BufferedWriter journalOutput;
    protected boolean completed;
    protected final boolean shadow;
    protected final int persListOfset;
    private final InterfacePhysique phys;


    public Simulation(final AlgoInstantiator prgminstantiator, final ConfigSimu config, final PartitionSimu partition)
    {
        this(prgminstantiator,config,partition,null);
    }

    public boolean paused()
    {
        return gestioTaches.paused;
    }

    public Simulation(final Simulation shadowed, final AlgoInstantiator prgminstantiator)
    {
        shadow = true;
        completed = shadowed.completed;
        journalOutput = null;
        c = shadowed.c;
        phys = new InterfacePhysique(this);
        p = prgminstantiator.getPrgm(new OutputProvider(phys), c);
        if(shadowed.shadow)
        {
            persListOfset = shadowed.persListOfset;
            personnesList = new ArrayList<>(shadowed.personnesList.size());
            for(final PersonneSimu p : shadowed.personnesList)
            {
                if(p == null)
                {
                    personnesList.add(null);
                }
                else
                {

                    personnesList.add(new PersonneSimu(p, this));
                }
            }
        }
        else
        {
            final List<PersonneSimu> personnesNonLivrees = shadowed.getPersonnesNonLivrees();
            int minId = Integer.MAX_VALUE;
            int maxId = Integer.MIN_VALUE;
            for(final PersonneSimu p : personnesNonLivrees)
            {
                if(p.getId() < minId)
                {
                    minId = p.getId();
                }
                if(p.getId() >= maxId)
                {
                    maxId = p.getId()+1;
                }
            }
            persListOfset = minId;
            if(personnesNonLivrees.isEmpty())
            {
                personnesList = new ArrayList<>();
            }
            else
            {
                personnesList = new ArrayList<>(Collections.nCopies(maxId - minId, null));
                for(final PersonneSimu pSimu : personnesNonLivrees)
                {
                    personnesList.set(pSimu.getId() - persListOfset,new PersonneSimu(pSimu, this));
                }
            }
            //System.out.println("Simulation shadow old" +shadowed.personnesList + " maliste " + personnesList );
        }

        potentiellementNonLivree = new ArrayList<>(personnesList);
        new ImmeubleSimu(shadowed.immeuble, this);
        gestioTaches = new GestionnaireDeTachesSimu(this, shadowed.gestioTaches);
    }

    public void setImmeubleSimu(final ImmeubleSimu i)
    {
        immeuble = i;
    }

    public List<PersonneSimu> getPersonnesNonLivrees()
    {
        potentiellementNonLivree.removeIf(new Predicate<PersonneSimu>()
        {
            @Override
            public boolean test(final PersonneSimu t)
            {
                return t.livree();
            }
        });
        return potentiellementNonLivree;
    }

    public Simulation(final AlgoInstantiator prgminstantiator, final ConfigSimu config, final PartitionSimu partition, final BufferedWriter journalOutput)
    {
        this.journalOutput = journalOutput;
        shadow = false;
        persListOfset = 0;
        c = config;
        phys = new InterfacePhysique(this);
        p = prgminstantiator.getPrgm(new OutputProvider(phys),config);
        personnesList = new ArrayList<>();
        potentiellementNonLivree = new ArrayList<>();

        immeuble = new ImmeubleSimu(this);
        gestioTaches = new GestionnaireDeTachesSimu(this,journalOutput != null,partition);
    }

    public BufferedWriter getJournalOutput()
    {
        return journalOutput;
    }

    public boolean doPrint()
    {
        return gestioTaches
            .policy
            .doPrint();
    }

    public ImmeubleSimu getImmeubleSimu()
    {
        return immeuble;
    }

    public ConfigSimu getConfig()
    {
        return c;
    }

    public void start()
    {
        if(completed)
        {
            throw new SimulateurException("simulation already completed");
        }
        gestioTaches.init();
        resumeTaches();
    }


    public void initPrgmAndResume()
    {
        //System.out.println("DEBUT INIT");
        getPrgm().init();
        //System.out.println("gest " + gestioTaches.taches);
        resumeTaches();
        //System.out.println("FIN RESUM TACHES");
    }

    public void resumeWithoutInit()
    {
        resumeTaches();
    }

    private void resumeTaches()
    {

        gestioTaches.resume();
        //System.out.println("fin gestio resume");
        if(gestioTaches.paused)
        {
            return;
        }
        for(final PersonneSimu p : personnesList)
        {
            if(p!= null && !p.livree())
            {
                throw new SimulateurAcceptableException("Toutes les personnes n'ont pas etees livrees exemple:" + p);
            }
        }
        completed = true;
    }

    public boolean completed()
    {
        return completed;
    }
    /*
    public Simulation shadow(final AlgoInstantiator newInstantiator)
    {
    	return new Simulation(this,newInstantiator);
    }*/

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

    public void inputPersonne(final int etageDepart, final int destination)
    {
        final PersonneSimu newP = new PersonneSimu(this, personnesList.size(), destination, etageDepart);
        personnesList.add(newP);
        potentiellementNonLivree.add(newP);
        newP.choisirDestination();
    }

    public void reRunLastInputPersonne()
    {
        personnesList.get(personnesList.size() - 1).choisirDestination();
    }

    public PersonneSimu getPersonne(final int id)
    {
        return personnesList.get(id - persListOfset);
    }

    public int getPersonneListSize()
    {
        return personnesList.size();
    }

    public List<PersonneSimu> getPersonneList()
    {
        return personnesList;
    }

    public boolean isCorrectId(final int persId)
    {
        return persId - persListOfset < personnesList.size() && persId  >= persListOfset;
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

    public void printConsoleLine(final String str)
    {
        phys.println(str);
    }

}
