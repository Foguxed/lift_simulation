package fr.fogux.lift_simulator.mind.monoasciteratif;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;

public class MonoMemoiser<T extends Comparable<T>>
{
    protected final Map<EtatMonoAsc,Simulation> map = new HashMap<>();
    protected Comparator<Simulation> comparator;

    public MonoMemoiser(final Comparator<Simulation> comparator)
    {
        this.comparator = comparator;
    }

    public void registerSimulation(final EtatMonoAsc etat, final Simulation simu)
    {
        //System.out.println("une simu registered");
        Simulation choisie = simu;
        final Simulation s = map.get(etat);
        if(s != null)
        {
            if(comparator.compare(simu, s) < 0)
            {
                choisie = simu;
            }
            else
            {
                choisie = s;
            }
        }
        map.put(etat, choisie);
    }

    public void runStep(final MinorableSimulStatCreator<T> statCreator, final T ref)
    {
        final Set<Entry<EtatMonoAsc,Simulation>> entrySet = new HashSet<>(map.entrySet());
        map.clear();
        //System.out.println("on explore");
        for(final Entry<EtatMonoAsc,Simulation> e : entrySet)
        {
            //System.out.println("du next step");
            ((AlgMonoAscIteratif)e.getValue().getPrgm()).nextSteps(e.getKey(), e.getValue(),statCreator,ref);
        }
    }

    public Map<EtatMonoAsc,Simulation> currentMap()
    {
        return map;
    }


}
