package fr.fogux.lift_simulator.mind.independant;

import java.util.List;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class Montee
{
    protected final AlgoAscenseur[] ascenseurs;

    protected int indexAttribution = 0;

    public Montee(final InterfacePhysique phys, final ConfigSimu config,final int monteeId,final int nbAscenseurs)
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
        };

        ascenseurs = new AlgoAscenseur[nbAscenseurs];
        VoisinAsc ascPrecedent = ascBorne;
        for(int i = 0; i < nbAscenseurs; i ++)
        {
            final AscId id = new AscId(monteeId, i);
            final AlgoAscenseur asc = new AlgoAscenseur(id, config, phys, ascPrecedent);
            ascPrecedent.setAscenseurSuperieur(asc);
            ascPrecedent = asc;
            ascenseurs[i] = asc;
        }
        ascPrecedent.setAscenseurSuperieur(ascBorne);
    }

    public void attribuer(final AlgoPersonne p)
    {
    	if(p.depart == 0)
    	{
    		ascenseurs[0].attribuer(p);
    	}
    	else
    	{
    		ascenseurs[indexAttribution].attribuer(p);
            indexAttribution ++;
    	}
        
        if(indexAttribution == ascenseurs.length)
        {
            indexAttribution = 0;
        }
    }
    
    public List<Integer> invites(final int niveau,final int stackId, final int placesDispo)
    {
        return ascenseurs[stackId].getInvites(niveau,placesDispo);
    }
    
    public void escaleTerminee(final int stackId)
    {
        ascenseurs[stackId].escaleTerminee();
    }
}
