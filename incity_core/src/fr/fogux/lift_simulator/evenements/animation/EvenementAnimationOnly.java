package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.PrintableEvenement;
import fr.fogux.lift_simulator.exceptions.SimulateurException;

public abstract class EvenementAnimationOnly extends PrintableEvenement
{

    public EvenementAnimationOnly()
    {
        super(-1);
    }

    public EvenementAnimationOnly(final long time)
    {
        super(time);
    }

    @Override
    public void simuRun(final Simulation simu)
    {
        throw new SimulateurException("EvenementAnimationOnly " + this + " was simuRun ");
    }


    @Override
    public void reRun(final Simulation simu)
    {
        simuRun(simu);
    }

    @Override
    public boolean shadowable(final long registeredT)
    {
        return false;
    }
}
