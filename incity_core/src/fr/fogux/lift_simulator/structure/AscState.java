package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class AscState implements Compoundable
{
	public final float x;
	public final float v;
	public final long t;
	
	public AscState(long t, float x, float v)
	{
		this.t = t;
		this.x = x;
		this.v = v;
	}
	
	public AscState(DataTagCompound compound)
	{
		this.x = compound.getFloat(TagNames.x);
		this.v = compound.getFloat(TagNames.v);
		this.t = compound.getLong(TagNames.t);
	}
	
	public AscState copyTranslated(float xOffset)
	{
		return new AscState(t,x+ xOffset, v);
	}
	
	public AscState copyStationnaire(long newT)
	{
		return new AscState(newT, x,v);
	}
	
	public void printFieldsIn(DataTagCompound compound)
	{
		compound.setFloat(TagNames.x, x);
		compound.setFloat(TagNames.v, v);
		compound.setLong(TagNames.t, t);
	}
	
	public String toString()
	{
		return "{t = " + t + " x = " + x + " v = " + v + "}";
	}
}
