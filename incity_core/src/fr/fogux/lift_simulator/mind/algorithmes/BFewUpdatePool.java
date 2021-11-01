package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.mind.pool.IdSetPool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;

public class BFewUpdatePool<P extends IdAscPersPool> implements IdAscPersPool
{
    protected P innerP;

    protected Set<AlgoPersonne> added;
    protected Set<AlgoPersonne> removed;


    public BFewUpdatePool(final BFewUpdatePool<P> toShadow)
    {
        this.innerP = toShadow.innerP;
        toShadow.checkRemoved();
        this.added = new HashSet<>(toShadow.added);
        this.removed = new HashSet<>(toShadow.removed);
    }

    public void checkRemoved()
    {
        if(removed.size() > 100)
        {
            removed.removeIf(p -> ! innerP.contains(p));
        }
    }

    public BFewUpdatePool(final P innerP)
    {
        this.innerP = innerP;
        added = new LinkedHashSet<>();
        removed = new HashSet<>();
    }

    @Override
    public void addToPool(final AlgoPersonne newPers)
    {
        added.add(newPers);
    }

    @Override
    public void removeFromPool(final AlgoPersonne pers)
    {
        if(added.contains(pers))
        {
            added.remove(pers);
        }
        else
        {
            removed.add(pers);
        }
    }

    @Override
    public boolean couldAccept(final AlgoPersonne newPers)
    {
        return innerP.couldAccept(newPers);
    }

    public P innerPool()
    {
        return innerP;
    }



    @Override
    public List<AscId> getAscs()
    {
        return innerP.getAscs();
    }

    public static AlgoPersonne getAnyPers(final BFewUpdatePool<IdSetPool> p)
    {
        Optional<AlgoPersonne> opt =p.innerP.getPersSet().stream().filter(pers -> !p.removed.contains(pers)).findAny();
        if(opt.isPresent())
        {
            return opt.get();
        }
        else
        {
            opt = p.added.stream().findAny();
            if(opt.isPresent())
            {
                return opt.get();
            }
            else
            {
                return null;
            }
        }
    }

    public static Stream<AlgoPersonne> stream(final BFewUpdatePool<IdSetPool> setPool)
    {
        return Stream.concat(setPool.added.stream(),setPool.innerP.getPersSet().stream().filter(pers -> !setPool.removed.contains(pers)));
    }

    @Override
    public boolean contains(final AlgoPersonne p)
    {
        return added.contains(p) || (innerP.contains(p) && ! removed.contains(p));
    }

}
