package fr.fogux.lift_simulator.mind.trajets;

import java.util.List;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class AlgoImmeuble<M extends AlgoMontee<?>> extends Algorithme
{
    public List<M> montees;

    public AlgoImmeuble(final List<M> montees,final OutputProvider phys, final ConfigSimu c)
    {
        super(phys, c);
        this.montees = montees;
    }

    @Override
    public long init()
    {
        montees.stream().forEach(m -> m.init());
        return algInit();
    }

    protected abstract int algInit();

    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        return montees.get(idASc.monteeId).invites(niveau,idASc.stackId, places_disponibles);
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {
        montees.get(idAscenseur.monteeId).escaleTerminee(idAscenseur.stackId);
    }
}
