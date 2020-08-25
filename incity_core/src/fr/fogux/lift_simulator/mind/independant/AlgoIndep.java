package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.trajets.AlgoImmeuble;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class AlgoIndep<T extends AlgoIndependentAsc & GloutonAsc> extends AlgoImmeuble<Montee<T>>
{

    public AlgoIndep(final OutputProvider output, final ConfigSimu config, final IndepAscInstantiator instantiator)
    {
        super(getMontees(output,config , instantiator),output, config);

    }

    public static <T extends AlgoIndependentAsc & GloutonAsc> List<Montee<T>> getMontees(final OutputProvider out,final ConfigSimu config, final IndepAscInstantiator instantiator)
    {
        final int[] repart = config.getRepartAscenseurs();
        final List<Montee<T>> montees = new ArrayList<>(repart.length);
        for(int j = 0 ; j < repart.length; j ++)
        {
            montees.add(new Montee<T>(out, config, j, repart[j], instantiator));
        }
        return montees;
    }

    @Override
    public void ping()
    {

    }

    @Override
    public int algInit()
    {
        return -1;
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        final AlgoPersonne p = new AlgoPersonne(idPersonne, niveau, destination);
        T ascMin = null;
        int valMin = Integer.MAX_VALUE;
        for(int i = 0 ; i < montees.size(); i ++)
        {
            for(int j = 0; j < montees.get(i).ascenseurs.size(); j ++)
            {
                final int v = montees.get(i).ascenseurs.get(j).evaluer(p);
                if(v < valMin)
                {
                    valMin = v;
                    ascMin = montees.get(i).ascenseurs.get(j);
                }
            }
        }
        ascMin.attribuer(p);
    }



    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {
    }

}
