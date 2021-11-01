package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.algorithmes.TreeExplorer;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.pool.FewUpdatePool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class AscIndepIteratif<P extends FewUpdatePool> extends PoolIndepAsc<P,TreeExplorer<P,?>>
{

    public AscIndepIteratif(final AscIndepIteratif<P> shadowed,final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(shadowed, config, outputProvider, ascPrecedent);
    }


    public AscIndepIteratif(final AscId id, final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(id, config, outputProvider, ascPrecedent);
    }

    public abstract Collection<Choix<List<AlgoPersonne>,?>> getPossibilitesInvites(int niveau, int placesDispo);
    public abstract Collection<Choix<Integer,?>> getPossibilitesProchainArrets(Predicate<Integer> aFiltrer);
    public abstract Collection<Choix<Integer,?>> getPossibilitesPositionAttente();


    public abstract void printDebug(InterfacePhysique debugOutput);
    public abstract void noticeOuverture(int niveau);

    /*
    protected abstract void sontInvites(int niveau, int placesDispo, Choix<List<Integer>> invites);
    protected abstract void prochainArretChoisi(Integer i);
    protected abstract void positionAttenteChoisie(Integer i);
     */

    @Override
    public List<AlgoPersonne> getPersInvites(final int niveau, final int placesDispo)
    {
        if(alg.hasNextCommande())
        {
            return (List<AlgoPersonne>)alg.pollNextCommande(this).getObj();
        }
        else
        {
            final Collection<Choix<List<AlgoPersonne>,?>> pInvites = getPossibilitesInvites(niveau,placesDispo);
            if(pInvites.isEmpty())
            {
                throw new SimulateurAcceptableException("getPossibilitesInvites doit renvoyer une ensemble de choix non vide au moins");
            }
            else
            {
                if(pInvites.size() == 1)
                {
                    final Choix<List<AlgoPersonne>,?> c = pInvites.stream().findAny().get();
                    alg.algoDonneUnSeulChoix(c,this);
                    return c.getObj();
                }
                else
                {
                    alg.setCurrentSplit((Collection)pInvites);
                    outputProvider.interfacePhys.interrupt();
                }
            }
        }
        return null;
    }




    @Override
    public Integer prochainArret(final Predicate<Integer> aFiltrer)
    {
        if(alg.hasNextCommande())
        {
            return (Integer)alg.pollNextCommande(this).getObj();
        }
        else
        {
            final Collection<Choix<Integer,?>> choixarrets = getPossibilitesProchainArrets(aFiltrer);
            if(choixarrets.isEmpty())
            {
                throw new SimulateurAcceptableException("getPossibilitesProchainArrets doit renvoyer une liste non vide au moins");
            }
            else
            {
                if(choixarrets.size() == 1)
                {
                    final Choix<Integer,?> c = choixarrets.stream().findAny().get();
                    alg.algoDonneUnSeulChoix(c,this);
                    return c.getObj();
                }
                else
                {
                    alg.setCurrentSplit((Set)choixarrets);
                    outputProvider.interfacePhys.interrupt();
                }
            }
        }
        return null;
    }

    @Override
    public Integer positionDattente()
    {
        if(alg.hasNextCommande())
        {
            return (Integer)alg.pollNextCommande(this).getObj();
        }
        else
        {
            final Collection<Choix<Integer,?>> choixAttente = getPossibilitesPositionAttente();
            if(choixAttente.isEmpty())
            {
                throw new SimulateurAcceptableException("getPossibilitesProchainArrets doit renvoyer une liste non vide au moins");
            }
            else
            {
                if(choixAttente.size() == 1)
                {
                    final Choix<Integer,?> c = choixAttente.stream().findAny().get();
                    alg.algoDonneUnSeulChoix(c,this);
                    return c.getObj();
                }
                else
                {
                    alg.setCurrentSplit((Set)choixAttente);
                    outputProvider.interfacePhys.interrupt();
                }
            }
        }
        return null;
    }






}
