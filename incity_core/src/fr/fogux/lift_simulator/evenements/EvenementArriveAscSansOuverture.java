package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementArriveAscSansOuverture extends Evenement
{
    protected final AscId id;

    public EvenementArriveAscSansOuverture(final long time, final AscId id)
    {
        super(time);
        this.id = id;
    }

    @Override
    public void simuRun(final Simulation simulation)
    {
        System.out.println("arrivee sans ouverture");
        simulation.getImmeubleSimu().getAscenseur(id).arriveSansOuverture();
    }

    @Override
    public void visuRun(final AnimationProcess animation)
    {
    }

}
