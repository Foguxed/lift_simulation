package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.dedale.proba.ObjectAndWeight;
import fr.fogux.dedale.proba.WeightRandomPicker;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public abstract class PerValueWeighter<T extends Object> implements Compoundable
{
	protected WeightRandomPicker<T> picker;
	
	public PerValueWeighter(DataTagCompound c)
	{
		List<DataTagCompound> compounds = c.getCompoundList(TagNames.valuesAndWeights);
		List<ObjectAndWeight<T>> objs = new ArrayList<>(compounds.size());
		for(int i = 0; i < compounds.size(); i ++)
		{
			objs.add(Compoundable.oawfromCompound(extractFromCompound(compounds.get(i),TagNames.val), compounds.get(i)));
		}
		picker = WeightRandomPicker.fromObjAndWeightList(objs);
	}
	
	@Override
	public void printFieldsIn(DataTagCompound compound) 
	{
		List<ObjectAndWeight<T>> objAndWeights = picker.toObjectAndWeightList();
		List<DataTagCompound> toCompounds = new ArrayList<>(objAndWeights.size());
		for(ObjectAndWeight<T> oaw : objAndWeights) 
		{
			DataTagCompound d = new DataTagCompound();
			setInCompound(d,TagNames.val, oaw.obj);
			Compoundable.printWeight(d, oaw);
			toCompounds.add(d);
		}
		compound.setCompoundList(TagNames.valuesAndWeights, toCompounds);
	}
	
	protected abstract T extractFromCompound(DataTagCompound c,String key);
	
	protected abstract void setInCompound(DataTagCompound c, String key, T value);
}
