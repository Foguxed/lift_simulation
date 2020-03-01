package fr.fogux.lift_simulator.partition_creation;

import java.util.List;


public class DestinationProfile
{
    protected final List<EtageDestination> destinations;
    
    public DestinationProfile(List<EtageDestination> destinations)
    {
        this.destinations = destinations;
    }
    
    public List<EtageDestination> getDestinations()
    {
        return destinations;
    }
    
    
}
