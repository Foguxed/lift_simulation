package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.trajets.AlgoAscenseur;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

/**
 * Cette classe est utilisée (sous la forme d'extend) par certains algorithmes, associée avec AlgoImmeuble et AlgoMontée qui distribuent les évènements aux ascenseurs, elle permet de gérer "indépendament"
 * chaque ascenseur et implémente une procédure d'évitement entre les ascenseurs d'une même collone (en cas de conflit, l'ascenseur qui a réservé en premier la destination gagne, l'autre se met en état d'attente 
 * et laisse la place)
 * 
 * Lorsqu'un algorithme utilise cette classe, les seules méthodes à implémenter par chaque ascenseur qui extend cette classe
 * Integer prochainArret(Predicate<Integer> aFiltrer)
 * Integer positionDattente()
 * List<Integer> getInvites(final int niveau, final int placesDispo) (provient de l'interface AlgoAscenseur)
 */
public abstract class AlgoIndependentAsc implements AlgoAscenseur,VoisinAsc
{

    protected VoisinAsc ascenseurSuperieur;
    protected VoisinAsc ascenseurInferieur;
    protected final AscId id;

    protected final ConfigSimu config;
    protected final OutputProvider outputProvider;
    protected Predicate<Integer> voisinPredicate; // un prédicat sur les numéros d'étage qui renvoie true si l'étage est atteignable par cet ascenseur


    protected boolean busy; // false si en attente
    protected int objectifActuel; // dernier objectif demandé par l'algorithme (depuis la couche supérieure)

    protected boolean savedbusy;
    protected int savedObjectifActuel;

    protected int limiteAtteignableInf;
    protected int limiteAtteignableSup;
    
    protected final Simulation s;

    /**
     * permet de copier l'objet (dans le but d'effectuer des sous-simulations)
     * @param shadowed a copier
     * @param config la nouvelle config
     * @param outputProvider la nouvelle interface physique
     * @param ascPrecedent l'ascenseur précédent, lui aussi déjà une copie de l'ascenseur précédent de shadowed
     */
    public AlgoIndependentAsc(final AlgoIndependentAsc shadowed,final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        id = shadowed.id;
        ascenseurInferieur = ascPrecedent;
        this.outputProvider = outputProvider;
        this.config = config;
        limiteAtteignableInf = shadowed.limiteAtteignableInf;
        limiteAtteignableSup = shadowed.limiteAtteignableSup;
        busy = shadowed.busy;
        objectifActuel = shadowed.objectifActuel;
        savedbusy = shadowed.savedbusy;
        savedObjectifActuel = shadowed.savedObjectifActuel;
        s = phys().simu;
    }

    public AlgoIndependentAsc(final AscId id,final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        ascenseurInferieur = ascPrecedent;
        limiteAtteignableInf = ascPrecedent.getAtteignableInf() + 1;
        this.config = config;
        this.outputProvider = outputProvider;
        this.id = id;
        busy = false;
        s = phys().simu;
    }
    
    
    public abstract Integer prochainArret(Predicate<Integer> aFiltrer);

    /**
     * Appellé uniquement si prochainObjectif(Predicate) n'a pas fonctionné, (si asc vide ou bloqué par les voisin)
     * @return la position souhaitée, (sans prendre en compte les voisins), null pour ne pas bouger
     */
    public abstract Integer positionDattente();
    
    public void saveState()
    {
        savedbusy = busy;
        savedObjectifActuel = objectifActuel;
    }

    public void rallBack()
    {
        busy = savedbusy;
        objectifActuel = savedObjectifActuel;
    }
    
    /**
     * utilisé à l'initialisation
     */
    @Override
    public void setAscenseurSuperieur(final VoisinAsc asc)
    {
        ascenseurSuperieur = asc;
        voisinPredicate = (i -> (i < ascenseurSuperieur.getLimitInf() && i > ascenseurInferieur.getLimitSup()));
    }

    @Override
    public int initLimiteSup()
    {
        limiteAtteignableSup = ascenseurSuperieur.initLimiteSup() - 1;
        return limiteAtteignableSup;
    }

    @Override
    public void init()
    {
        reflechir();
    }

    @Override
    public void escaleTerminee()
    {
        reflechir();
    }

    protected static List<Integer> toIds(final List<AlgoPersonne> l)
    {
        final List<Integer> retour = new ArrayList<>();
        for(final AlgoPersonne p : l)
        {
            retour.add(p.id);
        }
        return retour;
    }

    public InterfacePhysique phys()
    {
        return outputProvider.out();
    }

    public Predicate<Integer> getPredicateEtagesAtteignables()
    {
        return voisinPredicate;
    }

    public void ping()
    {
        if(!busy)
        {
            reflechir();
        }
    }

    public boolean occupe()
    {
        return busy;
    }
    
    /**
     * Coeur du mécanisme d'évitement, détermine la prochaine destination
     */
    protected void reflechir()
    {
        Integer prochainArret = null;
        final Predicate<Integer> predicate = voisinPredicate;
        prochainArret = prochainArret(predicate); // demande à la subclass sont souhait de prochain arrêt, cet arrêt doit être valide par rapport au voisinPredicate mais peut être null
        if(s.interrupted())
        {
        	/* a tout moment si la simulation est interrompue (par la couche supérieure), l'évènement qui a déclenché cette méthode sera relancé 
        	il faut donc que aucun paramètre de l'ascenseur n'ait été modifié (utilisé dans TreeExplorer)*/
            return;
        }
        if(prochainArret != null)
        {
            if(!predicate.test(prochainArret))
            {
                throw new SimulateurAcceptableException(id + " mauvaise destination " + prochainArret);
            }
        }
        if(prochainArret == null)
        {
            if(busy)
            {
                busy = false;
                updateVoisins();//signale aux ascenseurs voisins qu'il y a eut un changement d'état
                if(s.interrupted())
                {
                    busy = true;
                    return;
                }
            }
            else
            {
                prochainArret = positionDattente();
                if(s.interrupted())
                {
                    return;
                }
                if(prochainArret == null)
                {
                    prochainArret = objectifActuel;
                }
                // on se rapproche le plus possible du prochain arret
                if(prochainArret < ascenseurInferieur.getLimitSup() + 1)
                {
                    objectifActuel = ascenseurInferieur.getLimitSup() + 1; 
                }
                else if(prochainArret > ascenseurSuperieur.getLimitInf() - 1)
                {
                    objectifActuel = ascenseurSuperieur.getLimitInf() - 1;
                }
                else
                {
                    objectifActuel = prochainArret;
                }
                phys().changerDestination(id, objectifActuel, false);// demande de déplacement au simulateur
            }
        }
        else
        {
            final boolean oldbusy = busy;
            final int oldobj = objectifActuel;
            busy = true;
            objectifActuel = prochainArret;
            updateVoisins();// les voisins vont éviter cet ascenseurs si ils sont en état d'attente (busy == false)
            if(s.interrupted())
            {
                objectifActuel = oldobj;
                busy = oldbusy;
                return;
            }
            phys().changerDestination(id, prochainArret, true);
        }
    }

    protected void updateVoisins()
    {
        ascenseurSuperieur.updateLimitVoisin(false);
        if(s.interrupted())
        {
            return;
        }
        ascenseurInferieur.updateLimitVoisin(true);
    }
    
    @Override
    public int getLimitSup()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurInferieur.getLimitSup() + 1;
        }
    }

    @Override
    public int getLimitInf()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurSuperieur.getLimitInf() - 1;
        }
    }

    /**
     * @param isSup true si le voisin qui a été mis à jour à le voisin supérieur
     */
    @Override
    public void updateLimitVoisin(final boolean isSup)
    {
        if(!busy)// si l'ascenseur en état d'attente, il faut peut être se déplacer pour éviter l'ascenseur voisin, d'où reflechir()
        {
            reflechir();
            if(s.interrupted())
            {
                return;
            }
            if(!busy)// il ne s'est donc rien passé
            {
                if(isSup)
                {
                    ascenseurInferieur.updateLimitVoisin(true);
                }
                else
                {
                    ascenseurSuperieur.updateLimitVoisin(false);
                }
            }
        }
    }

    public int objectifActuel()
    {
        return objectifActuel;
    }

    @Override
    public int getAtteignableSup()
    {
        return limiteAtteignableSup;
    }

    @Override
    public int getAtteignableInf()
    {
        return limiteAtteignableInf;
    }

    public boolean atteignable(final int j) // supose ecart < 1 etage entre les acss dans la config
    {
        return (j >= getAtteignableInf() && j <= getAtteignableSup());
    }

    public boolean atteignable(final AlgoPersonne p)
    {
        return atteignable(p.depart) && atteignable(p.destination);
    }

    @Override
    public String toString()
    {
        return id + " etage atteignableinf : " + getAtteignableInf() + " atteignable sup " + getAtteignableSup();
    }

    public AscId getId()
    {
        return id;
    }
}
