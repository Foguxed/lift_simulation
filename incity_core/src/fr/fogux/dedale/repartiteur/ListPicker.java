package fr.fogux.dedale.repartiteur;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import fr.fogux.dedale.proba.ProbaToProbaReparter;

public class ListPicker<T extends Object>
{
	protected final List<T> toBeShuffeled;
	protected final ProbaToProbaReparter proportionPick;
	protected final int nbPickedMin;
	protected Random r;
	
	public ListPicker(List<T> arrayList, ProbaToProbaReparter proportionPick, int nbPickedMin)
	{
		this.toBeShuffeled = arrayList;
		this.proportionPick = proportionPick;
		this.r = new Random();
		if(nbPickedMin > arrayList.size())
		{
			throw new IllegalArgumentException("il ne peut pas y avoir plus de pickedmin que le nombre d'éléments de la liste");
		}
		this.nbPickedMin = nbPickedMin;
	}
	
	public Collection<T> getRandomlyPicked()
	{
		return getPickedItem(r.nextDouble());
	}
	
	public Collection<T> getPickedItem(double proportionFrom0to1)
	{
		Collection<T> retour = new HashSet<T>();
		Collections.shuffle(toBeShuffeled);
		int nb = (int) (nbPickedMin + Math.round((toBeShuffeled.size() - nbPickedMin)*proportionPick.getY(proportionFrom0to1)));
		for(int i = 0 ; i < nb ; i ++)
		{
			retour.add(toBeShuffeled.get(i));
		}
		return retour;
	}
}
