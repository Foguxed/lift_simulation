package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.IntEnsemble;
import fr.fogux.lift_simulator.utils.ListedTreemap;

public class ByEtagePool<A extends Object> extends BPersPool<A>
{
    protected final IntEnsemble etagesAutorises;
    protected final ListedTreemap<Integer,AlgoPersonne> departToPers;
    protected final ListedTreemap<Integer,AlgoPersonne> destinationToPers;


    public ByEtagePool(final IntEnsemble etagesAutorises, final List<A> ascs)
    {
        super(ascs);
        this.etagesAutorises = etagesAutorises;
        this.departToPers = new ListedTreemap<>();
        this.destinationToPers = new ListedTreemap<>();
    }



    @Override
    public void addToPool(final AlgoPersonne newPers)
    {
        departToPers.put(newPers.depart, newPers);
        destinationToPers.put(newPers.destination,newPers);
    }

    @Override
    public void removeFromPool(final AlgoPersonne pers)
    {
        departToPers.remove(pers.depart, pers);
        destinationToPers.remove(pers.destination, pers);
    }

    @Override
    public boolean couldAccept(final AlgoPersonne newPers)
    {
        return etagesAutorises.appartient(newPers.depart) && etagesAutorises.appartient(newPers.destination);
    }

}
