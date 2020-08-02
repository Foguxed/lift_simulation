package fr.fogux.lift_simulator;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.batchs.core.SimulationStatCreator;
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
    protected final List<PersonneSimu> potentiellementNonLivree;
    protected final BufferedWriter journalOutput;
    protected boolean completed;
    private InterfacePhysique phys;
    
    
    public Simulation(final AlgoInstantiator prgminstantiator, final ConfigSimu config, final PartitionSimu partition)
    {
    	this(prgminstantiator,config,partition,null);
    }
    
    /*private Simulation(Simulation shadowed, AlgoInstantiator prgminstantiator)
    {
    	this.journalOutput = null;
    	this.c = shadowed.c;
    	this.phys = new InterfacePhysique(this);
    	this.p = prgminstantiator.getPrgm(phys, c);
    	this.personnesList = shadowed.personnesList;
    	this.gestioTaches = new GestionnaireDeTachesSimu(this, shadowed.gestioTaches);
    	
    }*/
    
    public List<PersonneSimu> getPersonnesNonLivrees()
    {
    	potentiellementNonLivree.removeIf(new Predicate<PersonneSimu>() 
    	{
			@Override
			public boolean test(PersonneSimu t) 
			{
				return t.livree();
			}
		});
    	return potentiellementNonLivree;
    }
    
    public Simulation(final AlgoInstantiator prgminstantiator, final ConfigSimu config, final PartitionSimu partition, final BufferedWriter journalOutput)
    {
        this.journalOutput = journalOutput;
        c = config;
        phys = new InterfacePhysique(this);
        p = prgminstantiator.getPrgm(phys,config);
        personnesList = new ArrayList<>();
        potentiellementNonLivree = new ArrayList<>();
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

    public void run()
    {
        gestioTaches.runExecuting();
        for(PersonneSimu p : personnesList)
        {
        	if(!p.livree())
        	{
        		throw new SimulateurAcceptableException("Toutes les personnes n'ont pas etees livrees exemple:" + p);
        	}
        }
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

    public void inputPersonne(final EtageSimu etageDepart, final int destination)
    {
        final PersonneSimu newP = new PersonneSimu(this, personnesList.size(), destination, etageDepart);
        personnesList.add(newP);
        potentiellementNonLivree.add(newP);
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
    
    public List<PersonneSimu> getPersonneList()
    {
    	return personnesList;
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
    
    public void printConsoleLine(String str)
    {
    	phys.println(str);
    }

}
