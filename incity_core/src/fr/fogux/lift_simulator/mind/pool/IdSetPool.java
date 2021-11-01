package fr.fogux.lift_simulator.mind.pool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class IdSetPool extends BIdPersPool
{
	
	protected final Set<AlgoPersonne> persSet;

	public IdSetPool(List<AscId> ascIds) {
		super(ascIds);
		persSet = new HashSet<>();
	}

	@Override
	public void addToPool(AlgoPersonne newPers) {
		persSet.add(newPers);
	}

	@Override
	public void removeFromPool(AlgoPersonne pers) {
		persSet.remove(pers);
	}
	
	public Set<AlgoPersonne> getPersSet()
	{
		return persSet;
	}

}
