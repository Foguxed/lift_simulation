package fr.fogux.lift_simulator.mind.monoasciteratif;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.Escale;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.OcamlList;

public class AlgMonoAscIteratif extends Algorithme //implements AlgoInstantiator
{
    /**
     * N'est pas une implementation ONLINUE
     * @param output
     * @param config
     */

    public static final AscId id = new AscId(0, 0);

    private final EtatMonoAsc etat;
    private final AlgoPersonne pObjectif;
    private final boolean recuperation;
    private boolean ouvertureEffectuee;
    public final OcamlList<Escale> trajet;
    private final MonoMemoiser<?> memoiser;
    private int niveauObj;

    public AlgMonoAscIteratif(final OutputProvider output, final ConfigSimu config, final MonoMemoiser<?> memoiser,
        final EtatMonoAsc etat, final AlgoPersonne objectif, final boolean estRecuperation, final OcamlList<Escale> trajet)
    {
        super(output, config);
        if(config.getRepartAscenseurs().length > 1 || config.getRepartAscenseurs()[0] > 1)
        {
            throw new SimulateurException("Config incorrecte");
        }
        this.memoiser = memoiser;
        this.etat = etat;
        this.trajet = trajet;
        ouvertureEffectuee = false;
        recuperation = estRecuperation;
        pObjectif = objectif; // Si null alors l'ascenseur effectue simplement une déposition;
    }

    @Override
    public void ping()
    {

    }

    @Override
    public long init()
    {
        if(recuperation)
        {
            recuperer(pObjectif);
        }
        else
        {
            livrer(pObjectif);
        }
        return -1;
    }

    private void recuperer(final AlgoPersonne p)
    {
        niveauObj = p.depart;
        out().changerDestination(id, p.depart, true);
    }

    private void livrer(final AlgoPersonne p)
    {
        niveauObj = p.destination;
        out().changerDestination(id, p.destination, true);
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
        throw new SimulateurException("AlgMonoAscIteratif n'est pas une implementation online: appelExterieur interdit");
    }

    @Override
    public List<Integer> listeInvites(final AscId idASc, final int places_disponibles, final int niveau)
    {
        if(niveau != niveauObj)
        {
            return new ArrayList<>();
        }
        etat.setEtage(niveau);
        final List<Integer> retour = new ArrayList<>(1);
        if(recuperation)
        {

            if(ouvertureEffectuee)
            {
                trajet.head.todeleteTime = out().simu.getTime();
                out().pause(); // une personne est entree
            }
            else
            {
                //System.out.println("ouverture");
                ouvertureEffectuee = true;
                retour.add(pObjectif.id);
                registerBranches(etat);
            }
        }
        else
        {
            trajet.head.todeleteTime = out().simu.getTime();
            out().pause();
            ouvertureEffectuee = true;
            registerBranches(etat);
        }
        return retour;
    }

    protected boolean registerBranches(final EtatMonoAsc etat)
    {
        //System.out.println("register branches " + ouvertureEffectuee);

        boolean quelqueChoseRegister = false;
        if(ouvertureEffectuee)
        {
            for(final AlgoPersonne p : etat.contenuAsc)
            {
                if(p.destination == etat.getNiveau())
                {
                    final EtatMonoAsc newEtat = new EtatMonoAsc(etat);
                    newEtat.sortieDe(p);
                    memoiser.registerSimulation(newEtat, out().simu); // la simu va aller jusqu'au fin
                    quelqueChoseRegister = true;
                }
            }
            if(recuperation && etat.aDelivrer.contains(pObjectif))
            {
                //System.out.println("oldEtat " + etat);
                final EtatMonoAsc newEtat = new EtatMonoAsc(etat);
                newEtat.entre(pObjectif);
                //System.out.println("newEtat " + etat);
                memoiser.registerSimulation(newEtat, out().simu);
                quelqueChoseRegister = true;
            }
        }


        return quelqueChoseRegister;
    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
        throw new SimulateurException("ne doit pas arriver");
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {

    }

    public <T extends Comparable<T>> void nextSteps(final EtatMonoAsc registeredEtat,final Simulation s, final MinorableSimulStatCreator<T> statCreator, final T ref)
    {
        if(!registerBranches(registeredEtat))
        {
            if(statCreator.getMinorant(s, registeredEtat, config).compareTo(ref) < 0)
            {
                etatNextSteps(memoiser, registeredEtat, config, trajet,s);
            }
            else
            {
                //System.out.println("statcretor eliminated " + statCreator + " ref " +  ref);
            }
        }
    }

    public static <T extends Comparable<T>> void etatNextSteps(final MonoMemoiser<?> memoiser, final EtatMonoAsc etat, final ConfigSimu config, final OcamlList<Escale> trajet, final Simulation s)
    {
        try
        {
            if(etat.contenuAsc.size() < config.nbPersMaxAscenseur())
            {
                for(final AlgoPersonne p : etat.aDelivrer)
                {
                    new Simulation(s, new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, true,trajet.add(new Escale(p.depart, p)))).initPrgmAndResume();
                }
            }
            for(final AlgoPersonne p : etat.contenuAsc)
            {
                new Simulation(s,new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, false,trajet.add(new Escale(p.destination, null)))).initPrgmAndResume();
            }
        }
        catch(final SimulateurAcceptableException e)
        {
            e.printStackTrace();
            throw new SimulateurAcceptableException("erreur dans les sous simulations"  + e.getMessage());
        }
    }

    public static<T extends Comparable<T>> void etatNextStepsFiltre(final MonoMemoiser<?> memoiser, final EtatMonoAsc etat, final ConfigSimu config, final OcamlList<Escale> trajet, final Simulation s,final Predicate<Integer> filtreEtages)
    {
        try
        {
            if(etat.contenuAsc.size() < config.nbPersMaxAscenseur())
            {
                for(final AlgoPersonne p : etat.aDelivrer)
                {
                    if(filtreEtages.test(p.depart))
                    {
                        new Simulation(s, new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, true,trajet.add(new Escale(p.depart, p)))).initPrgmAndResume();
                    }
                }
            }
            for(final AlgoPersonne p : etat.contenuAsc)
            {
                if(filtreEtages.test(p.destination))
                {
                    new Simulation(s,new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, false,trajet.add(new Escale(p.destination, null)))).initPrgmAndResume();
                }
            }
        }
        catch(final SimulateurAcceptableException e)
        {
            e.printStackTrace();
            throw new SimulateurAcceptableException("erreur dans les sous simulations " + e.getMessage());
        }
    }
    /*
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return "algomonoasciteratif";
    }*/

}
