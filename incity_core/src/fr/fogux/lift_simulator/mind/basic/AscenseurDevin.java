package fr.fogux.lift_simulator.mind.basic;

import java.util.List;

import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class AscenseurDevin
{
    protected final List<DestinationSimple> destinations;
    protected int index;
    protected final AscId id;
    protected final InterfacePhysique interfacePhysique;

    public AscenseurDevin(final List<DestinationSimple> destinations, final AscId id, final InterfacePhysique interfacePhysique)
    {
        this.destinations = destinations;
        this.id = id;
        this.interfacePhysique = interfacePhysique;
        index = 0;
    }

    public List<Integer> getInvites()
    {
        return destinations.get(index-1).invites;
    }

    public void objectifSuivant()
    {
        if(index < destinations.size())
        {
            interfacePhysique.deplacerAscenseur(id, destinations.get(index).niveau, true);
            index ++;
        }
    }
}
