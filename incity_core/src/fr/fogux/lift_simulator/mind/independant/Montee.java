package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.trajets.AlgoMontee;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class Montee<T extends AlgoIndependentAsc> extends AlgoMontee<T>
{

    public Montee(final OutputProvider phys, final ConfigSimu config,final int monteeId,final int nbAscenseurs,final IndepAscInstantiator instantiator)
    {
        super((List<T>)getAscenseurs(phys,config,monteeId,nbAscenseurs ,instantiator));
    }

    protected static List<AlgoIndependentAsc> getAscenseurs(final OutputProvider phys, final ConfigSimu config, final int monteeId, final int nbAscenseurs, final IndepAscInstantiator instantiator)
    {
        final VoisinAsc ascBorne = new VoisinAsc()
        {
            @Override
            public void updateLimitVoisin(final boolean isSup)
            {
            }

            @Override
            public int getLimitSup()
            {
                return config.getNiveauMin()-1;
            }

            @Override
            public int getLimitInf()
            {
                return config.getNiveauMax()+1;
            }

            @Override
            public void setAscenseurSuperieur(final VoisinAsc asc)
            {
            }

            @Override
            public int getAtteignableSup()
            {
                return config.getNiveauMax()+1;
            }

            @Override
            public int getAtteignableInf()
            {
                return config.getNiveauMin()-11;
            }
        };
        final List<AlgoIndependentAsc> ascs = new ArrayList<>(nbAscenseurs);
        VoisinAsc ascPrecedent = ascBorne;
        for(int i = 0; i < nbAscenseurs; i ++)
        {
            final AscId id = new AscId(monteeId, i);
            final AlgoIndependentAsc asc = instantiator.getNewInstance(id, config, phys, ascPrecedent);
            ascPrecedent.setAscenseurSuperieur(asc);
            ascPrecedent = asc;
            ascs.add(asc);
        }
        ascPrecedent.setAscenseurSuperieur(ascBorne);
        return ascs;
    }
}
