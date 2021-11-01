package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.Simulation;

public class EvenementSimplePing extends Evenement
{

    public EvenementSimplePing(final long time)
    {
        super(time);
    }

    @Override
    public void simuRun(final Simulation simulation)
    {
        simulation.getPrgm().ping();
    }

    @Override
    public void visuRun(final AnimationProcess animation)
    {
    }

    @Override
    public void reRun(final Simulation simulation)
    {
        simulation.getPrgm().ping();
    }

    @Override
    public boolean shadowable(final long registeredTime, EventRunPolicy newPolicy)
    {
        return true;
    }

}
