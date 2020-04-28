package fr.fogux.lift_simulator.mind.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class ProgrammeBasique extends Algorithme
{
    protected final AscenseurDevin ascSup;
    protected final AscenseurDevin ascInf;

    public ProgrammeBasique(final InterfacePhysique output, final ConfigSimu config)
    {
        super(output, config);
        final List<DestinationSimple> destinationsSup = new ArrayList<>();
        destinationsSup.add(new DestinationSimple(11));
        destinationsSup.add(new DestinationSimple(13));
        destinationsSup.add(new DestinationSimple(17,0));
        destinationsSup.add(new DestinationSimple(19,2));
        destinationsSup.add(new DestinationSimple(16));
        destinationsSup.add(new DestinationSimple(0,4));
        final List<DestinationSimple> destinationsInf = new ArrayList<>();
        destinationsInf.add(new DestinationSimple(12));
        destinationsInf.add(new DestinationSimple(6,1));
        destinationsInf.add(new DestinationSimple(16));
        destinationsInf.add(new DestinationSimple(8));
        destinationsInf.add(new DestinationSimple(7));
        destinationsInf.add(new DestinationSimple(-1));
        ascInf = new AscenseurDevin(destinationsInf, new AscId(0, 0), output);
        ascSup = new AscenseurDevin(destinationsSup, new AscId(0, 1), output);
    }

    @Override
    public void init()
    {
        ascSup.objectifSuivant();
        ascInf.objectifSuivant();
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {

    }

    @Override
    public Collection<Integer> listeInvites(final AscId idASc, final int places_disponibles)
    {
        if(idASc.stackId == 0)
        {
            return ascInf.getInvites();
        }
        else
        {
            return ascSup.getInvites();
        }
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur)
    {
        if(idAscenseur.stackId == 0)
        {
            ascInf.objectifSuivant();
        }
        else
        {
            ascSup.objectifSuivant();
        }
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

}
