package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;

public class PersGroupLivraison
{
    protected int nbDeplaces;

    public PersGroupLivraison(final AlgoPersGroup objectif, final int nbDeplaces)
    {
        this.nbDeplaces = nbDeplaces;
    }
}
