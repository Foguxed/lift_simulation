package fr.fogux.lift_simulator.fichiers;

import java.util.Iterator;

import fr.fogux.lift_simulator.evenements.Evenement;

public class EvenementIterable implements Iterator<Evenement>
{
    @Override
    public boolean hasNext()
    {
        return false;
    }
    @Override
    public Evenement next()
    {
        return null;
    }
}
