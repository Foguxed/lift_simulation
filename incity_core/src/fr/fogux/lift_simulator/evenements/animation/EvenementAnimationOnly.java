package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.evenements.PrintableEvenement;

public abstract class EvenementAnimationOnly extends PrintableEvenement
{

    public EvenementAnimationOnly()
    {
        super(0, false);
    }

    public EvenementAnimationOnly(long time)
    {
        super(time, true);
    }

    @Override
    public void simuRun()
    {

    }

}
