package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.Simulation;

public class EvenementInterruptSimulation extends Evenement
{

    public EvenementInterruptSimulation(final long time)
    {
        super(time);
    }

    @Override
    public void simuRun(final Simulation simulation)
    {
        simulation.getGestio().thenpause();
    }

    @Override
    public void visuRun(final AnimationProcess animation)
    {
    }

    @Override
    public void reRun(final Simulation simulation)
    {
        simulation.getGestio().thenpause();
    }

    @Override
    public boolean shadowable(final long registeredTime, final EventRunPolicy newPolicy)
    {
        return true;
    }

}
