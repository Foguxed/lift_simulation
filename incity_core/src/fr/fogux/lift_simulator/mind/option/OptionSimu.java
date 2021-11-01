package fr.fogux.lift_simulator.mind.option;

import java.util.Comparator;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.algorithmes.TreeExplorer;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.utils.Arbre;

public class OptionSimu
{
    public static final Comparator<OptionSimu> OPT_TIME_COMPARATOR = new Comparator<OptionSimu>()
    {

        @Override
        public int compare(final OptionSimu o1, final OptionSimu o2)
        {
            return o1.computationTime - o2.computationTime;
        }
    };

    protected Simulation s;
    protected int computationTime;

    public OptionSimu(final Simulation s)
    {
        this.s = s;
        
    }
    public Simulation getSimulation()
    {
        return s;
    }

    public <A extends AscIndepIteratif> Arbre<NoeudChoix<?,A>> getNoeudCorrespondant()
    {
        return ((TreeExplorer)s.getPrgm()).getNoeud();
    }
    
    public int getTime()
    {
    	return computationTime;
    }
    
    public void simulationUpdated()
    {
        computationTime = (int)s.getTime();
    }
}
