package fr.fogux.lift_simulator.fichiers;

import fr.fogux.dedale.proba.ObjectAndWeight;

public interface Compoundable 
{
	public void printFieldsIn(DataTagCompound compound);
	
	public static DataTagCompound compound(Compoundable objet)
	{
		DataTagCompound d = new DataTagCompound();
		objet.printFieldsIn(d);
		return d;
	}
	
	public static void printWeight(DataTagCompound c, ObjectAndWeight<?> objAndWeight)
	{
		c.setDouble(TagNames.weight, objAndWeight.weight);
	}
	
	public static <T> ObjectAndWeight<T> oawfromCompound(T obj, DataTagCompound c)
	{
		return new ObjectAndWeight<T>(obj, c.getDouble(TagNames.weight));
	}
	
	public static  <T extends Compoundable>  DataTagCompound compound(ObjectAndWeight<T> objAndWeight)
	{
		DataTagCompound d = new DataTagCompound();
		printWeight(d,objAndWeight);
		objAndWeight.obj.printFieldsIn(d);
		return d;
	}
}
