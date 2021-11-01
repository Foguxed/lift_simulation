package fr.fogux.lift_simulator.mind.option;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;

public class NoeudChoix<O,A extends AscIndepIteratif> implements Choix<O,A>
{
    public final Choix<O,A> choix;
    public OptionSimu simu;

    public NoeudChoix(final Choix<O,A> choix, final OptionSimu simu)
    {
        this.choix = choix;
        this.simu = simu;
    }

    public void setSimu(final OptionSimu simu)
    {
        this.simu = simu;
    }

    @Override
    public O getObj()
    {
        return choix.getObj();
    }

    @Override
    public void apply(final A asc)
    {
        choix.apply(asc);
    }

    @Override
    public String toString()
    {
        return " noeudchoix " + choix;
    }
}
