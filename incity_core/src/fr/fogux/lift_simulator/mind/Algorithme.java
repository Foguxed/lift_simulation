package fr.fogux.lift_simulator.mind;

import java.util.List;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;

/**
 * les algorithmes doivent extend cette classe et implémener les méthodes abstract définies ci-dessous
 */
public abstract class Algorithme
{

    protected OutputProvider output;
    protected ConfigSimu config;

    public Algorithme(final OutputProvider output, final ConfigSimu config)
    {
        this.output = output;
        this.config = config;
    }

    /**
     * 
     * @return l'interface physique avec laquelle doit dialoguer l'algorithme
     */
    public InterfacePhysique out()
    {
        return output.out();
    }

    /**
     * @return la configuration de la simulation
     */
    public ConfigSimu config()
    {
        return config;
    }
    
    /**
     * appellé si demandé par l'algorithme à l'aide de init() ou de InterfacePhysique.registerPing
     */
    public abstract void ping();

    /**
     * permet d'initialiser le programme si nécessaire
     * @return le temps entre chaque ping, -1 si non nécessaire
     */
    public abstract long init();

    /**
     * appelé lorsque quelqu'un arrive dans l'immeuble
     * @param idPersonne
     * @param niveau
     * @param destination
     */
    public abstract void appelExterieur(int idPersonne, int niveau, int destination);

    /**
     * Appellé lorsque les portes de l'ascenseur s'ouvrent à un étage ou lorsque le transfert
     * est terminé pour demander à l'algorithme si il y a de nouvelles personnes à inviter
     * @param idASc
     * @param places_disponibles
     * @param niveau
     * @return la liste des ids des personnes invitées dans l'ascenseur
     */
    public abstract List<Integer> listeInvites(AscId idASc, int places_disponibles, int niveau);

    /**
     * Appellé lorsque l'ascenseur s'arrête à un étage pour lequel l'algorithme n'avais pas demandé
     *  d'ouverture (paramètre booléen dans InterfacePhysique.deplacerAscenseur)
     * @param idAscenseur
     */
    public abstract void arretSansOuverture(AscId idAscenseur);

    /**
     * appelé lorsque les portes de l'ascenseur se sont fermées
     *
     * @param niveau      auquel l'action a lieue
     * @param idAscenseur de l'ascenseur ouvert
     */
    public abstract void finDeTransfertDePersonnes(AscId idAscenseur, int niveau);

    @Deprecated
    /**
     * appelé lorsqu'un bouton appartenant au panneau à l'interieur de l'ascenseur
     * est utilisé
     * à priori inutile puisque la destination de la personne est connue dès l'appel extérieur
     * Permet de faire joli en utilisant InterfacePhysique.changerEtatBouton
     *
     *
     * @param niveau      correspondant au bouton
     * @param idAscenseur de l'ascenseur concerné
     */
    public abstract void appelInterieur(int niveau, AscId idAscenseur);
}
