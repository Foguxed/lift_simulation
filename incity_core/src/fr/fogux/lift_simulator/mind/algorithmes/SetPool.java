package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.fogux.lift_simulator.mind.ascenseurs.AscPoolUser;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.utils.IntEnsemble;

public class SetPool extends BPersPool
{
	protected final IntEnsemble etagesPossibles;
	public final Set<AlgoPersonne> set;
	
	public SetPool(List<AscPoolUser<?>> ascConcernes, IntEnsemble etagesPossibles) 
	{
		super(ascConcernes);
		this.etagesPossibles = etagesPossibles;
		set = new HashSet<>();
	}

	@Override
	public void addToPool(AlgoPersonne newPers) 
	{
		set.add(newPers);
	}

	@Override
	public void removeFromPool(AlgoPersonne pers) 
	{
		set.remove(pers);
	}

	@Override
	public boolean couldAccept(AlgoPersonne newPers) 
	{
		return etagesPossibles.appartient(newPers.depart)&&etagesPossibles.appartient(newPers.destination);
	}

}
