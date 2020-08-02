package fr.fogux.lift_simulator.fichiers.strings;

import java.util.ArrayList;
import java.util.List;

public interface StringProvider 
{
	public String getString();
	
	public static String getFullString(List<StringProvider> providers)
	{
		String str = "";
		for(StringProvider p : providers)
		{
			//System.out.println("provider " + p + " string " + p.getString());
			str += p.getString();
		}
		return str;
	}
	
	public static List<StringProvider> separe(List<? extends StringProvider> providers, StringProvider splitter)
	{
		List<StringProvider> retour = new ArrayList<>();
		for(int i = 0; i < providers.size() -1; i ++)
		{
			retour.add(providers.get(i));
			retour.add(splitter);
		}
		if(!providers.isEmpty())
		{
			retour.add(providers.get(providers.size()-1));
		}
		return retour;
	}
	
	public static List<StringProvider> concatener(List<List<? extends StringProvider>> lists, StringProvider splitter)
	{
		List<StringProvider> retour = new ArrayList<>();
		for(int i = 0; i < lists.size() - 1 ; i ++)
		{
			retour.addAll(lists.get(i));
			retour.add(splitter);
		}
		retour.addAll(lists.get(lists.size() - 1));
		return retour;
	}
}
