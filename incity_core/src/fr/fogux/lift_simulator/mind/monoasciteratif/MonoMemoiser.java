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
/**
 * Structure effectuant la mémoïsation
 *
 * @param <T>
 */
public class MonoMemoiser<T extends Comparable<T>>
{
    protected final Map<EtatMonoAsc,Simulation> map = new HashMap<>(); // dictionnaire java
    protected Comparator<Simulation> comparator;//objet capable de comparer deux simulation qui ont le même état, dans ce cas, a meilleur que b <=> comparator.compare(a,b) < 0

    public MonoMemoiser(final Comparator<Simulation> comparator)
    {
        this.comparator = comparator;
    }

    public void registerSimulation(final EtatMonoAsc etat, final Simulation simu)
    {
        Simulation choisie = simu;
        final Simulation s = map.get(etat);
        if(s != null)
        {
        	/* si l'état a déjà été traité, on garde la meilleure simulation (simulation <=> transition car la valeur de la fonction de coût passée est stockée dans la Simuation sous la forme
        	des temps de trajets individuels des personnes
        	*/
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
    /**
     * si lors de l'appel de cete fonction, map est le dictionnaire contenant les états atteignables en k étapes, alors
     * après l'appel de cette fonction, map est le dictionnaire contenant les états atteignables en k+1 étapes.
     */
    public void runStep()
    {
        final Set<Entry<EtatMonoAsc,Simulation>> entrySet = new HashSet<>(map.entrySet());// copie map
        map.clear();//vide map
        for(final Entry<EtatMonoAsc,Simulation> e : entrySet)
        {
            ((AlgMonoAscIteratif)e.getValue().getPrgm()).nextSteps(e.getKey(), e.getValue());// les AlgoMonoAscIteratifs vont appeler registerSimulation (cf ci dessus)
        }
    }

    public Map<EtatMonoAsc,Simulation> currentMap()
    {
        return map;
    }
}
