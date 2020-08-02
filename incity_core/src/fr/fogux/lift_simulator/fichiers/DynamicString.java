package fr.fogux.lift_simulator.fichiers;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.fichiers.strings.StringProvider;
import fr.fogux.lift_simulator.fichiers.strings.StringStringProvider;
import fr.fogux.lift_simulator.fichiers.variators.CompoundVariator;

public class DynamicString 
{
	private List<StringProvider> stringParts;
	private List<CompoundVariator> variators;
	
	public DynamicString(String rawString)
	{
		variators = new ArrayList<CompoundVariator>();
		stringParts = new ArrayList<>();
		int firstStringPartIndex = 0;
		int firstPipeIndex = -1;
		int i;
		for(i = 0; i < rawString.length(); i++)
		{
			if(rawString.charAt(i) == '|')
			{
				if(firstPipeIndex > 0)
				{
					CompoundVariator cv = CompoundVariator.fromString(rawString.substring(firstPipeIndex+1, i));
					stringParts.add(cv);
					variators.add(cv);
					firstPipeIndex = -1;
					firstStringPartIndex = i+1;
				}
				else
				{
					stringParts.add(new StringStringProvider(rawString.substring(firstStringPartIndex,i)));
					firstPipeIndex = i;
				}
			}
		}
		stringParts.add(new StringStringProvider(rawString.substring(firstStringPartIndex,i)));
	}
	
	public String getString()
	{
		return StringProvider.getFullString(stringParts);
	}
		
	public List<CompoundVariator> getVariators()
	{
		return variators;
	}
	
	public boolean next()
	{
		for(int i = 0; i < variators.size(); i ++)
		{
			if(!variators.get(i).nextStep())
			{
				return false;
			}
		}
		return true;
	}
	
	public int nbVariators()
	{
		return variators.size();
	}
}
