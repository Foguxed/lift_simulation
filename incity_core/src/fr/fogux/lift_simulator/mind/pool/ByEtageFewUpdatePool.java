package fr.fogux.lift_simulator.mind.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.Ref;

public class ByEtageFewUpdatePool implements FewUpdatePool, Function<AlgoPersGroup,AlgoPersGroup>
{
    protected final HashMap<AlgoPersGroup,Set<AlgoPersonne>> modifies = new HashMap<>();

    protected IdByEtagePool innerP;

    public int borneSupClients;
    public int borneInfClients;

    public ByEtageFewUpdatePool(final IdByEtagePool innerP)
    {
        this.innerP = innerP;
        updateBorneSupClients();
        updateBorneInfClients();
    }

    public ByEtageFewUpdatePool(final ByEtageFewUpdatePool toShadow)
    {
        innerP = toShadow.innerP;
        borneSupClients = toShadow.borneSupClients;
        borneInfClients = toShadow.borneInfClients;
        if(toShadow.modifies.size() > 60)
        {
            toShadow.checkRetires();
        }
        for(final Entry<AlgoPersGroup,Set<AlgoPersonne>> e: toShadow.modifies.entrySet())
        {
            modifies.put(e.getKey(), new HashSet<>(e.getValue()));
        }
    }

    public int etageMax()
    {
        return innerP.etages.lastKey();
    }

    public int etageMin()
    {
        return innerP.etages.firstKey();
    }

    public int distanceTotale()
    {
        return innerP.distanceTotale;
    }

    public float refNoteEtage()
    {
        return 1f + (float)innerP.nbPersonnes/(innerP.nbEtages);
    }

    protected void updateBorneSupClients()
    {
        final Optional<AlgoPersGroup> borne = persborne(false);

        if(borne.isPresent())
        {
            borneSupClients = borne.get().getEtage();
        }
        else
        {
            borneSupClients = innerP.etages.firstKey();
        }
    }

    protected void updateBorneInfClients()
    {
        final Optional<AlgoPersGroup> borne = persborne(true);
        if(borne.isPresent())
        {
            borneInfClients = borne.get().getEtage();
        }
        else
        {
            borneInfClients = innerP.etages.lastKey();
        }
    }

    protected Optional<AlgoPersGroup> persborne(final boolean borneInf)
    {
        final Optional<AlgoPersGroup> s = realStream(innerP.getFullGroupStream(borneInf)).limit(innerP.margeBorneClients).skip(innerP.margeBorneClients-1).findFirst();
        if(s.isPresent())
        {
            return s;
        }
        else
        {
            return realStream(innerP.getFullGroupStream(borneInf)).limit(innerP.margeBorneClients).findFirst();
        }
    }


    public void checkRetires()
    {
        final List<AlgoPersGroup> toRemove = new ArrayList<>();
        for(final Entry<AlgoPersGroup,Set<AlgoPersonne>> e: modifies.entrySet())
        {
            e.getValue().removeIf(p -> !innerP.contains(p));
            if(e.getValue().isEmpty())
            {
                toRemove.add(e.getKey());
            }
        }
        for(final AlgoPersGroup key : toRemove)
        {
            modifies.remove(key);
        }
    }

    @Override
    public List<AscId> getAscs()
    {
        return innerP.getAscs();
    }

    @Override
    public void addToPool(final AlgoPersonne newPers)
    {
        throw new RuntimeException("n'arrive jamais");
    }

    @Override
    public void removeFromPool(final AlgoPersonne pers)
    {
        if(!innerP.contains(pers))
        {
            return;
        }
        final AlgoPersGroup g = innerP.getGroup(pers);

        Set<AlgoPersonne> mg = modifies.get(g);
        if(mg != null)
        {
            mg.add(pers);
        }
        else
        {
            mg = new HashSet<>();
            mg.add(pers);
            modifies.put(g, mg);
        }
        if(g.getEtage() >= borneSupClients)
        {
            updateBorneSupClients(); // on ne fait pas d'update pour les ajouts: on suppose que ça suffira pour l'estimation
        }
        if(g.getEtage() <= borneInfClients)
        {
            updateBorneInfClients();
        }
    }

    @Override
    public boolean couldAccept(final AlgoPersonne newPers)
    {
        return innerP.couldAccept(newPers);
    }

    @Override
    public boolean contains(final AlgoPersonne p)
    {
        throw new RuntimeException("jamais utilise");
    }

    @Override
    public AlgoPersGroup apply(final AlgoPersGroup t)
    {
        final Set<AlgoPersonne> toRemove = modifies.get(t);
        if(toRemove == null)
        {
            return t;
        }
        else
        {
            final AlgoPersGroup newG = new AlgoPersGroup(t);
            for(final AlgoPersonne p : toRemove)
            {
                newG.safeRemove(p);
            }
            return newG;
        }
    }

    public Stream<AlgoPersGroup> realStream(final Stream<AlgoPersGroup> rawStream)
    {
        return rawStream.map(this).filter(p -> !p.isEmpty());
    }

    public Stream<AlgoPersGroup> getPersGroup(final int etage, final boolean montee)
    {
        return realStream(innerP.getEtage(etage).getPersGroupStream(montee));
    }

    public Stream<AlgoPersGroup> getPersGroups(final int etage)
    {
        return realStream(Stream.concat(innerP.getEtage(etage).getPersGroupStream(true),(innerP.getEtage(etage).getPersGroupStream(false))));
    }

    public Stream<AlgoPersGroup> getGroupStream(final int fromKey, final int toKeyExcluded, final boolean enMontee)
    {

        return realStream(innerP.getGroupStream(fromKey, toKeyExcluded, enMontee));
    }

    /**
     * modifie les pools initiales
     */
    @Override
    public void flushUpdates()
    {
        for(final Entry<AlgoPersGroup,Set<AlgoPersonne>> e : modifies.entrySet())
        {
            for(final AlgoPersonne p : e.getValue())
            {
                innerP.removeFromPool(p);
            }
        }
        modifies.clear();
    }

    @Override
    public String toString()
    {
        final Ref<String> str = new Ref<>("ByIdFewUpdatePool ");
        realStream(innerP.getFullGroupStream(true)).forEach(pg -> str.set(str.get() + " | " + pg));


        return str.get();
    }

    public String stringEtage(final int etage)
    {
        final Ref<String> str = new Ref<>("etage: " + etage + " inner descente " + innerP.getGroupStream(etage, etage-1, false).count() + " realcontenu " + getPersGroups(etage).count());
        getPersGroups(etage).forEach(pg -> str.set(str.get() + " | " + pg));
        return str.get();
    }
}
