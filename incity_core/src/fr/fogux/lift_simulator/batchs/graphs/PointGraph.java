package fr.fogux.lift_simulator.batchs.graphs;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.batchs.core.SimuTaskReceiver;
import fr.fogux.lift_simulator.fichiers.strings.StringProvider;
import fr.fogux.lift_simulator.stats.AveragedStat;
import fr.fogux.lift_simulator.stats.SimulationStat;
import fr.fogux.lift_simulator.stats.StandardSimulationStat;

public class PointGraph extends SimuTaskReceiver<StandardSimulationStat> implements Comparable<PointGraph>, SimulationStat, StringProvider
{	
	public final int id;
	protected final BatchGraphProducer batch;
	protected String resultString;
	
	
	public PointGraph( BatchGraphProducer batch, int echantillonage, int id, String abscisse) 
	{
		super(echantillonage);
		this.batch = batch;
		this.id = id;
		this.resultString = abscisse  + " ";
	}
	
	
	public String getString()
	{
		return resultString;
	}
	
	private String getResultAsString(AveragedStat averaged)
	{
		return "[" + averaged.totalTravelTime/averaged.nbPersDeplacees + "," + averaged.maxTravelTime + "]";
	}
	
	
	@Override
	public int compareTo(PointGraph o) 
	{
		return id - o.id;
	}

	@Override
	protected void onCompletion(List<List<StandardSimulationStat>> shuffledResult) 
	{
		List<AveragedStat> resultAverages = new ArrayList<>();
		for(int j = 0; j < shuffledResult.get(0).size(); j ++)// tous de meme taille car depend du nb d'algos
		{
			List<StandardSimulationStat> colonne = new ArrayList<>();
			for(int i = 0; i < shuffledResult.size(); i ++)
			{
				colonne.add(shuffledResult.get(i).get(j));
			}
			resultAverages.add(new AveragedStat(colonne));
		}
		for(AveragedStat g : resultAverages)
		{
			resultString += getResultAsString(g) + ";";
		}
		batch.registerCompleted(this);
	}
	
	public String toString()
	{
		return "PointGraph id" + id;
	}
}
