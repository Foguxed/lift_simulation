package fr.fogux.lift_simulator.mind.option;

import java.util.function.Function;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;

public class GhostChoix<T,A extends AscIndepIteratif> implements Choix<T,A>
{
	protected Choix<T,A> c;
	
	
	public GhostChoix(Choix<T,A> c)
	{
		this.c = c;
	}
	
	
	@Override
	public T getObj() 
	{
		return c.getObj();
	}

	@Override
	public void apply(A asc) 
	{
		
	}

}
