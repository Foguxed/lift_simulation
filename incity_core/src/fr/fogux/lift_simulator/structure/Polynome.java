package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.utils.Fct;

public class Polynome implements Fct
{
	public final long ti;
	public final float xi;
	public final float vi;
	public final float a;

	public Polynome(long ti,float xi, float vi, float acceleration)
	{
		this.ti = ti;
		this.xi = xi;
		this.vi = vi;
		this.a = acceleration;
	}
	
	public float apply(long absTime)
	{
		final float t = absTime-ti;
		return (0.5f*a)*t*t + vi*t + xi;
	}
	
	public float applyDer(long absTime)
	{
		return a*(absTime - ti) + vi;
	}

	@Override
	public double getY(double x) 
	{
		return apply((long)x);
	}
}
