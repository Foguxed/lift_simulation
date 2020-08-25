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


    public PointGraph( final BatchGraphProducer batch, final int echantillonage, final int id, final String abscisse)
    {
        super(echantillonage);
        this.batch = batch;
        this.id = id;
        resultString = abscisse  + " ";
    }


    @Override
    public String getString()
    {
        return resultString;
    }

    private String getResultAsString(final AveragedStat averaged)
    {
        if(failure)
        {
            return "[failed]";
        }
        return "[" + averaged.toString(",") + "]";
    }


    @Override
    public int compareTo(final PointGraph o)
    {
        return id - o.id;
    }

    @Override
    protected void onCompletion(final List<List<StandardSimulationStat>> shuffledResult)
    {
        if(shuffledResult == null)
        {
            resultString = "failure";
        }
        else
        {
            final List<AveragedStat> resultAverages = new ArrayList<>();
            for(int j = 0; j < shuffledResult.get(0).size(); j ++)// tous de meme taille car depend du nb d'algos
            {
                final List<StandardSimulationStat> colonne = new ArrayList<>();
                for(int i = 0; i < shuffledResult.size(); i ++)
                {
                    colonne.add(shuffledResult.get(i).get(j));
                }
                resultAverages.add(new AveragedStat(colonne));
            }
            for(final AveragedStat g : resultAverages)
            {
                resultString += getResultAsString(g) + ";";
            }
        }
        batch.registerCompleted(this);
    }

    @Override
    public String toString()
    {
        return "PointGraph id" + id;
    }
}
