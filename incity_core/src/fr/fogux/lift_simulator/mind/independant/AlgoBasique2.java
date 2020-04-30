package fr.fogux.lift_simulator.mind.independant;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class AlgoBasique2 extends Algorithme
{
    protected final Montee[] montees;

    protected int repartIndex = 0;

    public AlgoBasique2(final InterfacePhysique output, final ConfigSimu config)
    {
        super(output, config);
        final int[] repart = config.getRepartAscenseurs();
        montees = new Montee[repart.length];
        for(int j = 0 ; j < repart.length; j ++)
        {
            montees[j] = new Montee(output, config, j, repart[j]);
        }
    }

    @Override
    public void ping()
    {

    }

    @Override
    public long init()
    {
        return -1;
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        montees[repartIndex].attribuer(new AlgoPersonne(idPersonne, destination, niveau));
        repartIndex ++;
        if(repartIndex == montees.length)
        {
            repartIndex = 0;
        }
    }

    @Override
    public Iterable<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        return montees[idASc.monteeId].invites(niveau,idASc.stackId, places_disponibles);
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur)
    {
        montees[idAscenseur.monteeId].escaleTerminee(idAscenseur.stackId);
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {
    }

}
