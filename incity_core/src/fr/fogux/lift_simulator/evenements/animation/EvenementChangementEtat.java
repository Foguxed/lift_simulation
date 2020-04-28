package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.AnimationProcess;

public abstract class EvenementChangementEtat extends EvenementAnimationOnly
{

    public EvenementChangementEtat()
    {
        super();
    }

    public EvenementChangementEtat(final long time)
    {
        super(time);
    }

    @Override
    public void visuRun(final AnimationProcess animP)
    {
        if (animP.gestioTaches().marcheArriereEnCours())
        {
            visuRunetatPrecedent(animP);
        } else
        {
            visuRunetatSuivant(animP);
        }
    }

    public abstract void visuRunetatSuivant(AnimationProcess animP);

    public abstract void visuRunetatPrecedent(AnimationProcess animP);
}
