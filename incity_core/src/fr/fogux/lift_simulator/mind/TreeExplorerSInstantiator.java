package fr.fogux.lift_simulator.mind;

import fr.fogux.lift_simulator.mind.algorithmes.ShadowIndepAscInstantiator;
import fr.fogux.lift_simulator.mind.algorithmes.TreeExplorer;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.utils.OcamlList;

public class TreeExplorerSInstantiator<P extends FewUpdatePool,A extends AscIndepIteratif<P>> implements AlgoInstantiator
{
    protected final TreeExplorer<P,A> toshadow;
    protected final OcamlList<Choix<?,A>> newCommande;
    protected final ShadowIndepAscInstantiator<A> ascIndepInst;

    public TreeExplorerSInstantiator(final TreeExplorer<P,A> toshadow, final OcamlList<Choix<?,A>> newCommande, final ShadowIndepAscInstantiator<A> ascInst)
    {
        this.toshadow = toshadow;
        this.newCommande = newCommande;
        this.ascIndepInst = ascInst;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Algorithme getPrgm(final OutputProvider output, final ConfigSimu c)
    {
        return new TreeExplorer(toshadow, newCommande, output, c,ascIndepInst);
    }

    @Override
    public String getName()
    {
        return "TreeExplorerInstantiator";
    }

}
