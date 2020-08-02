package fr.fogux.dedale.proba;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.dedale.function.Utils;

public class WeightRandomPicker<T extends Object>
{
	private double[] probaResearchTable;
	private List<T> objects;
	
	public WeightRandomPicker(List<T> objecList, List<Double> respectiveWeight)
	{
		objects = new ArrayList<T>(objecList);
		probaResearchTable = new double[objects.size()];
		
		double totalWeight = 0d;
		for(Double d : respectiveWeight)
		{
			totalWeight += d;
		}
		
		probaResearchTable[0] = 0d;
		for(int i = 0; i < probaResearchTable.length-1; i ++)
		{
			probaResearchTable[i+1] = probaResearchTable[i] + respectiveWeight.get(i)/totalWeight;
		}
	}
	
	public static <T> WeightRandomPicker<T> fromObjAndWeightList(List<ObjectAndWeight<T>> liste)
	{
		List<Double> weights = new ArrayList<>(liste.size());
		List<T> objects = new ArrayList<>(liste.size());
		for(int i = 0; i < liste.size(); i ++)
		{
			weights.add(liste.get(i).weight);
			objects.add(liste.get(i).obj);
		}
		return new WeightRandomPicker<>(objects, weights);
	}
	
	public List<ObjectAndWeight<T>> toObjectAndWeightList()
	{
		double[] weights = getProbas();
		List<ObjectAndWeight<T>> retour = new ArrayList<>(weights.length);
		for(int i = 0; i < weights.length; i ++)
		{
			retour.add(new ObjectAndWeight<T>(objects.get(i), weights[i]));
		}
		return retour;
	}
	
	public T getRandomObject(Random r)
	{
		return objects.get(Utils.rechercheDichotomiqueClassique(probaResearchTable, r.nextDouble()));
	}
	
	public double[] getProbas()
	{
		double[] retour = new double[probaResearchTable.length];
		for(int i =0; i < probaResearchTable.length-1; i ++)
		{
			retour[i] = probaResearchTable[i+1] - probaResearchTable[i];
		}
		retour[probaResearchTable.length-1] = 1-probaResearchTable[probaResearchTable.length-1];
		return retour;
	}
	
	public List<T> getObjects()
	{
		return new ArrayList<T>(objects);
	}
}
