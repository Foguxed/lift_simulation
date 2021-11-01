package fr.fogux.lift_simulator.mind.planifiers;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public abstract class AlgoRequete
{
    public final AlgoPersonne concernee;
    public AlgoRequete(final AlgoPersonne concernee)
    {
        this.concernee = concernee;
    }

    public abstract int getEtage();
    public abstract boolean isEntree();

    @Override
    public String toString()
    {
        return getEtage() + " entree " + isEntree() + " " + concernee;
    }
}
