package fr.fogux.lift_simulator.mind.algorithmes;

import java.util.List;
import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.option.NoeudChoix;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.utils.Arbre;
import fr.fogux.lift_simulator.utils.OcamlList;

public class PupetTreeExplorer<P extends FewUpdatePool,A extends AscIndepIteratif<P>> extends TreeExplorer<P,A>
{
    protected final CommandSupplier master;
    protected final boolean hasNextCommande;

    public PupetTreeExplorer(final Arbre<NoeudChoix<?, A>> choix, final CommandSupplier main,
        final OcamlList<Choix<?, A>> newcommande, final OutputProvider phys, final ConfigSimu c, final ShadowIndepAscInstantiator<A> inst,
        final List<IdAscPersPool> initialPools, boolean hasNextCommande)
    {
        super(choix, (Consumer<OptionSimu>)main, newcommande, phys, c, inst, initialPools);
        master = main;
        this.hasNextCommande = hasNextCommande;
    }


    @Override
    public boolean hasNextCommande()
    {
        return hasNextCommande;
    }

    @Override
    public Choix<?,A> pollNextCommande(final AscIndepIteratif<P> asc)
    {
        return (Choix<?,A>)master.pollNextCommande(asc);
    }

    public void flushPoolUpdates()
    {
        pools.stream().forEach(p -> p.flushUpdates());
    }
}
