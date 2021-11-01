package fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg;

import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.planifiers.ContenuAsc;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;

public class AscCycleUpdate implements Consumer<AscCycleOption>
{
    public final ContenuAsc contenu;
    public final AlgoPersGroup prochainCl;



    public AscCycleUpdate(final ContenuAsc contenu, final AlgoPersGroup persGroup)
    {
        this.contenu = contenu;
        prochainCl = persGroup;

    }



    @Override
    public void accept(final AscCycleOption t)
    {
        t.update(this);
    }

}
