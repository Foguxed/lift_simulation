package fr.fogux.lift_simulator.mind;


import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;

public class AlgoInstantiatorJustRef<T extends Algorithme & DynamicOutputAlgorithm > implements AlgoInstantiator
{
    protected final T a;
    public AlgoInstantiatorJustRef(final T a)
    {
        this.a = a;
    }

    @Override
    public Algorithme getPrgm(final OutputProvider output, final ConfigSimu c)
    {
        a.setOutput(output.out());
        return a;
    }


    @Override
    public String getName()
    {
        return "selfInstantiated";
    }

}
