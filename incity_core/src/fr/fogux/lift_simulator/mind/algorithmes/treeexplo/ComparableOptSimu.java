package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;


import fr.fogux.lift_simulator.mind.option.OptionSimu;

public class ComparableOptSimu<T extends Comparable<T>> implements Comparable<ComparableOptSimu<T>>
{
	public OptionSimu simu;
	public T v;
	
	public ComparableOptSimu(OptionSimu contenu, T toCompare) 
	{
		this.simu = contenu;
		this.v = toCompare;
	}
	

	@Override
	public int compareTo(ComparableOptSimu<T> o) 
	{
		return v.compareTo(o.v);
	}
}
