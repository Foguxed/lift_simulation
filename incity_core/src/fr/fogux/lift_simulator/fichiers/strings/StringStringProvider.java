package fr.fogux.lift_simulator.fichiers.strings;

public class StringStringProvider implements StringProvider
{
	private String str;
	public StringStringProvider(String str)
	{
		this.str = str;
	}
	
	@Override
	public String getString() 
	{
		return str;
	}
	
	public String toString()
	{
		return str;
	}
}
