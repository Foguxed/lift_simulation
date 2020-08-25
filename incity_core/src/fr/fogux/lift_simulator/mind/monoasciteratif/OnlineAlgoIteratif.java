package fr.fogux.lift_simulator.mind.monoasciteratif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.independant.AlgoAscCycliqueIndependant;
import fr.fogux.lift_simulator.mind.independant.AlgoIndep;
import fr.fogux.lift_simulator.mind.independant.IndepAscInstantiator;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.Escale;
import fr.fogux.lift_simulator.mind.trajets.EtatContenuAsc;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;
import fr.fogux.lift_simulator.utils.OcamlList;

public class OnlineAlgoIteratif<T extends Comparable<T>> extends Algorithme implements Comparator<Simulation>
{

    protected EtatMonoAsc etat;

    protected OcamlList<Escale> trajet;

    protected MinorableSimulStatCreator<T> statCreator;

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
        //System.out.println(" arrive id " + idPersonne);
        etat.arrive(new AlgoPersonne(idPersonne, niveau, destination));
        //System.out.println(" masterPRGM quelqu'un est arrivé " + etat);
        //output.println(" qlq arrive id " + idPersonne + " niv " + niveau + " et " + etat);
        final EtatAsc ascState = out().getEtat(monoAscid);
        if(ascState.etat == EtatAscenseur.BLOQUE)
        {
            replanificationNecessaire = true;
        }else
        {
            plannifier();
            goToNextEscale();
        }

    }

    protected void plannifier()
    {
        final MonoMemoiser<T> mem = new MonoMemoiser<>(this);
        if(etat.nbSteps() > 20)
        {
            throw new SimulateurAcceptableException(" l'algoIteratif a été surchagé: temps de calcul trop long ");
        }
        if(etat.nbSteps() > 0)
        {
            //System.out.println("resulution avec " + etat.nbSteps() + " etapes");
            final EtatAsc ascState = out().getEtat(monoAscid);
            //System.out.println("PLANNIFIER etat " + etat + " time " + output.simu.getTime());
            AlgMonoAscIteratif.etatNextStepsFiltre(mem, etat, config, new OcamlList<>(), out().simu, ascState.filtreAntiDemiTour());
            //System.out.println(etat.nbSteps() + " " + mem.currentMap());
            final Simulation refSimu = new Simulation(out().simu,  Simulateur.getIndepInstantiator(IndepAscInstantiator.CYCLIQUE,"cycliqueasref"));
            (((AlgoIndep<AlgoAscCycliqueIndependant>)refSimu.getPrgm()).montees.get(0).ascenseurs.get(0)).etat = new EtatContenuAsc(this.etat);
            try
            {
                refSimu.initPrgmAndResume();
            }
            catch(final SimulateurAcceptableException e)
            {
                e.printStackTrace();
                throw new SimulateurAcceptableException("pb avec la reference " + e.getMessage());
            }
            final T ref = statCreator.produceStat(refSimu);
            //System.out.println("ref " + ref);
            for(int i = 0; i < etat.nbSteps() - 1; i ++)
            {
                mem.runStep(statCreator,ref);
                //System.out.println("taille " + mem.map.size());
            }
            trajet = ((AlgMonoAscIteratif)mem.currentMap().values().stream().min(this).get().getPrgm()).trajet.reverse();
        }

    }


    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        final List<Integer> invites = new ArrayList<>();

        //output.println("trajet " + trajet);
        etat.contenuAsc.removeIf(p -> p.destination == niveau);
        //System.out.println(" purge etat au niveau " + niveau + " et " + etat);
        //output.println("purge etat " + etat);
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
