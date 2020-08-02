package fr.fogux.lift_simulator.batchs.core;

import java.util.ArrayList;
import java.util.List;

public abstract class SimuTaskReceiver<S extends Object>
{
	private int nbTask;
	
	private List<List<S>> results;
	
	public SimuTaskReceiver(int nbTask)
	{
		this.nbTask = nbTask;
		results = new ArrayList<>();
	}
	
	private synchronized void collect(List<S> resultPerAlgo)
	{
		nbTask --;
		results.add(resultPerAlgo);
	}
	/**
	 * Simulateur Acceptable Exception
	 */
	protected synchronized void taskFailed()
	{
		nbTask --;
	}
	
	
	@SuppressWarnings("unchecked")
	public void uncheckedTaskCompleted(List<Object> resultPerAlgo)
	{
		this.taskCompleted((List<S>) resultPerAlgo);
	}
	
	protected void taskCompleted(List<S> result)
	{
		collect(result);
		if(nbTask <= 0)
		{
			if(nbTask == 0)
			{
				onCompletion(results);
			}
			else
			{
				throw new IllegalStateException("nbTaskACompleter " + nbTask + " je suis " + this);
			}
		}
	}
	
	protected abstract void onCompletion(List<List<S>> results);
}
