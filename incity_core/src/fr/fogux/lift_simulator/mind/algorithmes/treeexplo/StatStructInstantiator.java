package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

public class StatStructInstantiator<T extends Comparable<T>> implements ArbreOptSimuStructInstantiator
{
	protected final SimuEvaluator<T> ev;
	public StatStructInstantiator(SimuEvaluator<T> ev)
	{
		this.ev = ev;
	}
	
	@Override
	public ArbreOptionSimuStruct getStruct(int capacity, long maxForecasttime) 
	{
		return new OptSimuStructStat<>(capacity, maxForecasttime, ev);
	}

}
