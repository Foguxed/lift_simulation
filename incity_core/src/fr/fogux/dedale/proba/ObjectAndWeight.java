package fr.fogux.dedale.proba;

public class ObjectAndWeight<T extends Object>
{
	public final double weight;
	public final T obj;
	
	public ObjectAndWeight(T obj, double weight)
	{
		this.obj = obj;
		this.weight = weight;
	}
	
	
}
