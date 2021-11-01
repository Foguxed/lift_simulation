package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;

import fr.fogux.lift_simulator.mind.ascenseurs.AscPoolUser;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class BPersPool<A> implements PersPool
{
    protected List<A> ascConcernes;
    
    
    public BPersPool(final List<A> ascConcernes)
    {
        this.ascConcernes = ascConcernes;
        for(final A asc : ascConcernes)
        {
            if(asc instanceof AscPoolUser<?>)
            {
                ((AscPoolUser<?>)asc).rawregisterPool(this);
            }
        }
    }

    public List<A> getAscConcernes()
    {
        return ascConcernes;
    }
   

}
