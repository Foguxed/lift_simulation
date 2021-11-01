package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public class SortiePers extends AlgoRequete
{


    public SortiePers(final AlgoPersonne concernee)
    {
        super(concernee);
    }

    @Override
    public int getEtage()
    {
        return concernee.destination;
    }

    @Override
    public boolean isEntree()
    {
        return false;
    }

}
