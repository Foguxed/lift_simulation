package fr.fogux.lift_simulator.fichiers;

public interface Compoundable 
{
	public void printFieldsIn(DataTagCompound compound);
	
	public static DataTagCompound compound(Compoundable objet)
	{
		DataTagCompound d = new DataTagCompound();
		objet.printFieldsIn(d);
		return d;
	}
}
