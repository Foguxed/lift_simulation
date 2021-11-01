package fr.fogux.lift_simulator.mind.monoasciteratif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.algorithmes.AlgoIndep;
import fr.fogux.lift_simulator.mind.algorithmes.IndepAscInstantiator;
import fr.fogux.lift_simulator.mind.ascenseurs.AlgoAscCycliqueIndependant;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.Escale;
import fr.fogux.lift_simulator.mind.trajets.EtatContenuAsc;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;
import fr.fogux.lift_simulator.utils.BOcamlList;

/**
 * Algorithme de mémoïsation
 * @param <T>
 */
public class OnlineAlgoIteratif<T extends Comparable<T>> extends Algorithme implements Comparator<Simulation>
{
    protected EtatMonoAsc etat;
    protected BOcamlList<Escale> trajet;
    protected MinorableSimulStatCreator<T> statCreator;//fonction de coût qui peut être appliquée à des simulations qui ont le même état présent mais qui ne sont pas forcément terminées
    public static final AscId monoAscid = new AscId(0, 0);
    protected boolean replanificationNecessaire;

    public OnlineAlgoIteratif(final OutputProvider output, final ConfigSimu config, final MinorableSimulStatCreator<T> statCreator)
    {
        super(output, config);
        etat = new EtatMonoAsc(0);
        this.statCreator = statCreator;
        replanificationNecessaire = false;
    }

    @Override
    public void ping()
    {

    }

    @Override
    public long init()
    {
        return -1;
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        etat.arrive(new AlgoPersonne(idPersonne, niveau, destination));
        final EtatAsc ascState = out().getEtat(monoAscid);
        if(ascState.etat == EtatAscenseur.BLOQUE)
        {
            replanificationNecessaire = true;// la replanification peut avoir lieue à la fin du transfert
        }else
        {
        	// implémentation de type Replanifier
            plannifier();
            goToNextEscale();
        }

    }

    protected void plannifier()
    {
        final MonoMemoiser<T> mem = new MonoMemoiser<>(this);
        if(etat.nbSteps() > 20)//10 personnes max (version online)
        {
            throw new SimulateurAcceptableException(" la mémoïsation a été surchagée: temps de calcul trop long ");
        }
        if(etat.nbSteps() > 0)
        {
            final EtatAsc ascState = out().getEtat(monoAscid);
            AlgMonoAscIteratif.etatNextStepsFiltre(mem, etat, config, new BOcamlList<>(), out().simu, ascState.filtreAntiDemiTour());// initialise la premiere etape dans la memoïsation, on interdit tout de même les demi-tour d'ascenseur
            for(int i = 0; i < etat.nbSteps() - 1; i ++)// effectue les 2n-1 etapes restantes par mémoïsation
            {
                mem.runStep();
            }
            //mem contient les états à l'étape 2n (toutes les personnes sont livrées)
            // this est cet objet qui est (entre autre) un comparateur de simulation, d'où le min(this)
            trajet = ((AlgMonoAscIteratif)mem.currentMap().values().stream().min(this).get().getPrgm()).trajet.reverse();// choisit le trajet effectué par la meilleure simulation
            
        }
    }


    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
    	// suis les instructions du trajet plannifié
        final List<Integer> invites = new ArrayList<>();
        etat.contenuAsc.removeIf(p -> p.destination == niveau);
        while(!trajet.isEmpty() && trajet.head.etage == niveau)
        {
            if(trajet.head.invite != null)
            {
                invites.add(trajet.head.invite.id);
                etat.entre(trajet.head.invite);
            }
            trajet = trajet.queue;
        }
        return invites;
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
        throw new SimulateurAcceptableException("ne doit pas arriver");
    }

    protected void goToNextEscale()
    {
        if(!trajet.isEmpty())
        {
            out().changerDestination(AlgMonoAscIteratif.id, trajet.head.etage, true);
        }
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {
        if(replanificationNecessaire)
        {
            plannifier();
        }
        goToNextEscale();
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

    @Override
    public int compare(final Simulation o1, final Simulation o2)
    {
        final T statA = statCreator.produceStat(o1);
        final T statB = statCreator.produceStat(o2);
        return statA.compareTo(statB);
    }
}
