package fr.fogux.lift_simulator.mind.monoasciteratif;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.batchs.core.MinorableSimulStatCreator;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.Escale;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.BOcamlList;

/**
 * Sous algorithme contenu dans les sous simulations de la mémoïsation
 */
public class AlgMonoAscIteratif extends Algorithme
{
    /**
     * N'est pas une implementation ONLINE
     * @param outputStruct
     * @param config
     */

    public static final AscId id = new AscId(0, 0);

    private final EtatMonoAsc etat;
    private final AlgoPersonne pObjectif;
    private final boolean recuperation;
    private boolean ouvertureEffectuee;
    public final BOcamlList<Escale> trajet;
    private final MonoMemoiser<?> memoiser;
    private int niveauObj;

    public AlgMonoAscIteratif(final OutputProvider output, final ConfigSimu config, final MonoMemoiser<?> memoiser,
        final EtatMonoAsc etat, final AlgoPersonne objectif, final boolean estRecuperation, final BOcamlList<Escale> trajet)
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
                out().thenpause();
            }
            else
            {
                ouvertureEffectuee = true;
                retour.add(pObjectif.id);
                registerBranches(etat);
            }
        }
        else
        {
            out().thenpause();
            ouvertureEffectuee = true;
            registerBranches(etat);
        }
        return retour;
    }
    
    /**
     * 
     * @param etat
     * @return true si de nouveaux états ont étés enregistrés à partir de cette simulation
     */
    protected boolean registerBranches(final EtatMonoAsc etat)
    {
        boolean quelqueChoseRegister = false;
        if(ouvertureEffectuee) // sinon cette simulation n'a pas atteint le moment satisfaisant pour enregistrer les états de l'étape k+1
        {
        	// enregistre les états (éventuellement multiples) auquel correspond cette simulation dans la structure de mémoïsation
            for(final AlgoPersonne p : etat.contenuAsc)
            {
                if(p.destination == etat.getNiveau())
                {
                	// les états correspondants à l'étape k+1 sont toutes les manières de faire sortir une personne au palier
                    final EtatMonoAsc newEtat = new EtatMonoAsc(etat);
                    newEtat.sortieDe(p);
                    memoiser.registerSimulation(newEtat, out().simu); 
                    quelqueChoseRegister = true;
                }
            }
            if(recuperation && etat.aDelivrer.contains(pObjectif))
            {
                final EtatMonoAsc newEtat = new EtatMonoAsc(etat);
                newEtat.entre(pObjectif);
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
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int niveau)
    {

    }

    public <T extends Comparable<T>> void nextSteps(final EtatMonoAsc registeredEtat,final Simulation s)
    {
        if(!registerBranches(registeredEtat))
        {
            etatNextSteps(memoiser, registeredEtat, config, trajet,s);
        }
    }
    
    /**
     * crée les sous simulations de s vue sous l'angle de l'état etat et les exécutent (elles vont s'enregistrer dans memoiser lorsqu'elles auront atteint l'étape k+1).
     */
    public static <T extends Comparable<T>> void etatNextSteps(final MonoMemoiser<?> memoiser, final EtatMonoAsc etat, final ConfigSimu config, final BOcamlList<Escale> trajet, final Simulation s)
    {
        try
        {
            if(etat.contenuAsc.size() < config.nbPersMaxAscenseur())
            {
                for(final AlgoPersonne p : etat.aDelivrer)
                {
                	// on essaye de prendre tous les clients possibles
                    new Simulation(s, new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, true,trajet.add(new Escale(p.depart, p))),false).initPrgmAndResume();
                }
            }
            for(final AlgoPersonne p : etat.contenuAsc)
            {
            	// on essaye toutes les sorties de client possibles
                new Simulation(s,new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, false,trajet.add(new Escale(p.destination, null))),false).initPrgmAndResume();
            }
        }
        catch(final SimulateurAcceptableException e)
        {
            e.printStackTrace();
            throw new SimulateurAcceptableException("erreur dans les sous simulations"  + e.getMessage());
        }
    }
    
    /**
     * même méthode que la précédement mais n'accepte que les choix d'étages qui respectent le filtre
     */
    public static<T extends Comparable<T>> void etatNextStepsFiltre(final MonoMemoiser<?> memoiser, final EtatMonoAsc etat, final ConfigSimu config, final BOcamlList<Escale> trajet, final Simulation s,final Predicate<Integer> filtreEtages)
    {
        try
        {
            if(etat.contenuAsc.size() < config.nbPersMaxAscenseur())
            {
                for(final AlgoPersonne p : etat.aDelivrer)
                {
                    if(filtreEtages.test(p.depart))
                    {
                        new Simulation(s, new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, true,trajet.add(new Escale(p.depart, p))),false).initPrgmAndResume();
                    }
                }
            }
            for(final AlgoPersonne p : etat.contenuAsc)
            {
                if(filtreEtages.test(p.destination))
                {
                    new Simulation(s,new AlgMonoInstantiator(new EtatMonoAsc(etat), memoiser, p, false,trajet.add(new Escale(p.destination, null))),false).initPrgmAndResume();
                }
            }
        }
        catch(final SimulateurAcceptableException e)
        {
            e.printStackTrace();
            throw new SimulateurAcceptableException("erreur dans les sous simulations " + e.getMessage());
        }
    }
}
