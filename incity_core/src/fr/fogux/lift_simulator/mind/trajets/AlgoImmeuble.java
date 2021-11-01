package fr.fogux.lift_simulator.mind.trajets;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.algorithmes.IndepAscInstantiator;
import fr.fogux.lift_simulator.mind.algorithmes.Montee;
import fr.fogux.lift_simulator.mind.algorithmes.ShadowIndepAscInstantiator;
import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class AlgoImmeuble<A extends AlgoIndependentAsc> extends Algorithme
{
    public final List<Montee<A>> montees;

    public AlgoImmeuble(final AlgoImmeuble<A> toShadow,final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> instantiator)
    {
        super(phys,c);
        this.montees = new ArrayList<>();
        for(int i = 0; i < toShadow.montees.size(); i ++)
        {
            montees.add(new Montee<>(toShadow.montees.get(i),phys,c,i,instantiator));
        }
    }

    public AlgoImmeuble(final OutputProvider phys, final ConfigSimu c,final IndepAscInstantiator<A> inst)
    {
        this(getMontees(phys,c,inst),phys,c);
    }

    public AlgoImmeuble(final List<Montee<A>> montees,final OutputProvider phys, final ConfigSimu c)
    {
        super(phys, c);
        this.montees = montees;
    }

    public static <T extends AlgoIndependentAsc> List<Montee<T>> getMontees(final OutputProvider out,final ConfigSimu config, final IndepAscInstantiator<T> instantiator)
    {
        final int[] repart = config.getRepartAscenseurs();
        final List<Montee<T>> montees = new ArrayList<>(repart.length);
        for(int j = 0 ; j < repart.length; j ++)
        {
            montees.add(new Montee<>(out, config, j, repart[j], instantiator));
        }
        return montees;
    }


    public A getAsc(final AscId id)
    {
        return montees.get(id.monteeId).ascenseurs.get(id.stackId);
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

    @Override
    public String toString()
    {
        String str = " montees:";
        for(final Montee<A> m : montees)
        {
            str += m.toString() + " , ";
        }
        return str;
    }
}
