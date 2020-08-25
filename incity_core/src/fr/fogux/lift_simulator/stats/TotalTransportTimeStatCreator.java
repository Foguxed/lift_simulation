package fr.fogux.lift_simulator.stats;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class TotalTransportTimeStatCreator implements MinorableSimulStatCreator<Integer>
{

    @Override
    public Integer produceStat(final Simulation s)
    {
        int totalTime = 0;
        for(final PersonneSimu p : s.getPersonneList())
        {
            if(p!=null)
            {
                totalTime += p.getTempsTrajet();
            }
        }
        return totalTime;
    }

    @Override
    public Integer getMinorant(final Simulation simu, final EtatMonoAsc etat,final ConfigSimu c)
    {
        return produceStat(simu) + (int)etat.getMinorantTotalTrajetTime(c);
    }

}
