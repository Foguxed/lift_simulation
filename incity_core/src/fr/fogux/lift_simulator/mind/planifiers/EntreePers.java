package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public class EntreePers extends AlgoRequete
{
    public EntreePers(final AlgoPersonne concernee)
    {
        super(concernee);
    }

    @Override
    public int getEtage()
    {
        return concernee.depart;
    }

    @Override
    public boolean isEntree()
    {
        return true;
    }
}
