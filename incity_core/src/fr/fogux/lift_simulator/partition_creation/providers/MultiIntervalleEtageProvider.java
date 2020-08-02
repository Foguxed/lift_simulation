package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.fogux.dedale.proba.WeightRandomPicker;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class MultiIntervalleEtageProvider implements IntProvider,Compoundable
{
	protected WeightRandomPicker<IntervalleIntProvider> picker;
	
	public MultiIntervalleEtageProvider(DataTagCompound compound)
	{
		List<DataTagCompound> intervalles = compound.getCompoundList(TagNames.listeIntervalleEtages);
		List<Double> weights = new ArrayList<>(intervalles.size());
		List<IntervalleIntProvider> intervals = new ArrayList<IntervalleIntProvider>(intervalles.size());
		for(int i = 0; i < intervalles.size(); i ++)
		{
			weights.add(intervalles.get(i).getDouble(TagNames.weight));
			intervals.add(new IntervalleIntProvider(intervalles.get(i)));
		}
		picker = new WeightRandomPicker<>(intervals, weights);
	}
	
	@Override
	public int getRandomInt(Random r) 
	{
		return picker.getRandomObject(r).getRandomInt(r);
	}

	@Override
	public void printFieldsIn(DataTagCompound compound) 
	{
		List<IntervalleIntProvider> providers = picker.getObjects();
		double[] probas = picker.getProbas();
		List<DataTagCompound> compoundList = new ArrayList<>(providers.size());
		for(int i = 0; i < probas.length; i ++)
		{
			DataTagCompound c = new DataTagCompound();
			c.setDouble(TagNames.weight, probas[i]);
			providers.get(i).printFieldsIn(c);
		}
		compound.setCompoundList(TagNames.listeIntervalleEtages, compoundList);
	}

}
