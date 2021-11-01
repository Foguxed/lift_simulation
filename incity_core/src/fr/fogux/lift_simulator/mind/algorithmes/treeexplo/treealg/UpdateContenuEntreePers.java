package fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg;

import java.util.List;
import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public class UpdateContenuEntreePers<A extends EtatContenuAscUser> implements Consumer<A>
{
	protected List<AlgoPersonne> acceptees;
	
	public UpdateContenuEntreePers(List<AlgoPersonne> acceptees)
	{
		this.acceptees = acceptees;
	}
	
	@Override
	public void accept(A obj) 
	{
		obj.getContenu().addAll(acceptees);
	}

}
