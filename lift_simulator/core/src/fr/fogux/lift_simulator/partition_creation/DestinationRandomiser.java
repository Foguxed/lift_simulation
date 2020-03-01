package fr.fogux.lift_simulator.partition_creation;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DestinationRandomiser
{
    protected double totalVal;
    protected List<EtageDestination> destinations;
    protected Random r;
    
    public DestinationRandomiser(DestinationProfile profile, int etageDeDepart)
    {
        destinations = profile.getDestinations().stream().filter(d -> d.getEtage() != etageDeDepart).collect(Collectors.toList());
        totalVal = 0;
        for(EtageDestination desti : destinations)
        {
            totalVal += desti.getProbabilite();
        }
        r = new Random();
    }
    
    public EtageDestination getRandomDestination()
    {
        double val = r.nextDouble()*totalVal;
        for(EtageDestination desti : destinations)
        {
            val -= desti.getProbabilite();
            if(val < 0)
            {
                return desti;
            }
        }
        System.out.println("erreur " );
        return null;
    }
}
