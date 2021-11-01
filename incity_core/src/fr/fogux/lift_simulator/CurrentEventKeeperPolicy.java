package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.evenements.Evenement;

public class CurrentEventKeeperPolicy implements EventRunPolicy
{
    public Evenement currentEvent = null;
    protected EventRunPolicy printPolicy;

    public CurrentEventKeeperPolicy(final EventRunPolicy printPolicy)
    {
        this.printPolicy = printPolicy;
    }

    @Override
    public void onSimuRun(final Evenement e)
    {
        currentEvent = e;
        printPolicy.onSimuRun(e);
        currentEvent = null;
    }

    @Override
    public void onRegister(final Evenement e, final GestionnaireDeTachesSimu gestio, final long time)
    {
        printPolicy.onRegister(e, gestio, time);

    }

    @Override
    public void onCancel(final Evenement e, final GestionnaireDeTachesSimu gestio, final long time)
    {
        printPolicy.onCancel(e, gestio, time);
    }

    @Override
    public boolean doPrint()
    {
        return printPolicy.doPrint();
    }


}
