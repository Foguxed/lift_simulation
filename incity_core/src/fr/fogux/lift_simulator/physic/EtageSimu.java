package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.Shadowable;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonTriangle;
import fr.fogux.lift_simulator.structure.Etage;

public class EtageSimu extends Etage
{
    protected final Simulation simu;

    public EtageSimu(final Simulation simu,final int numero)
    {
        super(numero);
        this.simu = simu;
    }

    /*
     * public void choisirDirection(Personne personne) { }
     */

    public void rappuyerBoutonsSiNecessaire()
    {

    }

    @Override
    public int getNiveau()
    {
        return num;
    }

    @Override
    public boolean boutonHautAllume()
    {
        return hautAllume;
    }

    @Override
    public boolean boutonBasAllume()
    {
        return basAllume;
    }

    @Override
    public void setBoutonState(final boolean allume, final boolean boutonDuHaut)
    {
        new EvenementBoutonTriangle(num, boutonDuHaut, allume, boutonDuHaut ? hautAllume : basAllume).print(simu);
        if (boutonDuHaut)
        {
            hautAllume = allume;
        } else
        {
            basAllume = allume;
        }

    }

    @Override
    public void arriveeDe(final int nbPersonnes, final int destination)
    {
        for(int i = 0; i < nbPersonnes; i ++)
        {
            simu.inputPersonne(this, destination);
        }
    }
}
