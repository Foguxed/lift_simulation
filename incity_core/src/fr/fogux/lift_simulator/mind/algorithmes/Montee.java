package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.mind.trajets.AlgoMontee;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public class Montee<T extends AlgoIndependentAsc> extends AlgoMontee<T>
{


    public Montee(final Montee<T> shadowed,final OutputProvider phys, final ConfigSimu config,final int monteeId, final ShadowIndepAscInstantiator<T> shadowInstantiator)
    {
        super(getShadowAscenseurs(shadowed.ascenseurs,monteeId,phys,config,shadowInstantiator));
    }

    public Montee(final OutputProvider phys, final ConfigSimu config,final int monteeId,final int nbAscenseurs,final IndepAscInstantiator<T> instantiator)
    {
        super(getAscenseurs(phys,config,monteeId,nbAscenseurs ,instantiator));
    }

    protected static VoisinAsc ascBorne(final ConfigSimu config)
    {
        return new VoisinAsc()
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
                return config.getNiveauMin()-1;
            }

            @Override
            public int initLimiteSup() {
                return getAtteignableSup();
            }
        };
    }


    protected static <T extends AlgoIndependentAsc> List<T> getAscenseurs(final OutputProvider phys, final ConfigSimu config, final int monteeId, final int nbAscenseurs, final IndepAscInstantiator<T> instantiator)
    {
        final VoisinAsc ascBorne = ascBorne(config);
        final List<T> ascs = new ArrayList<>(nbAscenseurs);
        VoisinAsc ascPrecedent = ascBorne;
        for(int i = 0; i < nbAscenseurs; i ++)
        {
            final AscId id = new AscId(monteeId, i);
            final T asc = instantiator.getNewInstance(id, config, phys, ascPrecedent);
            ascPrecedent.setAscenseurSuperieur(asc);
            ascPrecedent = asc;
            ascs.add(asc);
        }
        ascPrecedent.setAscenseurSuperieur(ascBorne);
        ascs.get(0).initLimiteSup();
        return ascs;
    }

    protected static <T extends AlgoIndependentAsc> List<T> getShadowAscenseurs(final List<T> toShadow, final int monteeId, final OutputProvider phys, final ConfigSimu config, final ShadowIndepAscInstantiator<T> instantiator)
    {
        final VoisinAsc ascBorne = ascBorne(config);
        final List<T> ascs = new ArrayList<>(toShadow.size());
        VoisinAsc ascPrecedent = ascBorne;
        for(int i = 0; i < toShadow.size(); i ++)
        {
            final AscId id = new AscId(monteeId, i);
            final T asc = instantiator
                .shadowInstantiate(toShadow
                    .get(i), config, phys, ascPrecedent);
            ascPrecedent.setAscenseurSuperieur(asc);
            ascPrecedent = asc;
            ascs.add(asc);
        }
        ascPrecedent.setAscenseurSuperieur(ascBorne);
        ascs.get(0).initLimiteSup();
        return ascs;
    }

    @Override
    public String toString()
    {
        String str = " ascs: ";
        for(final T asc : ascenseurs)
        {
            str = str + asc.toString() + " , ";
        }
        return str;
    }
}
