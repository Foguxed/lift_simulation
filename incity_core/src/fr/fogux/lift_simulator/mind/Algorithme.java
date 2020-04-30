package fr.fogux.lift_simulator.mind;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class Algorithme
{

    protected InterfacePhysique output;
    protected ConfigSimu config;

    public Algorithme(final InterfacePhysique output, final ConfigSimu config)
    {
        this.output = output;
        this.config = config;
    }

    public InterfacePhysique output()
    {
        return output;
    }



    /**
     *
     * @return la config
     */
    public ConfigSimu config()
    {
        return config;
    }

    /**
     * Ne pas utiliser pour un algorithme efficace en temps de calcul
     * appellé tout les SimuConfig.pingTime, voir TagNames.pingTime
     */
    public abstract void ping();

    /**
     *
     * @return ping delay time value , <0 if no ping required
     */
    public abstract long init();

    /**
     * appelé lorsque quelqu'un arrive dans l'immeuble
     * @param niveau
     * @param destination
     */
    public abstract void appelExterieur(int idPersonne, int niveau, int destination);

    /**
     * Appellé lorsque les portes de l'ascenseur s'ouvrent à un étage, seules les personnes dont l'id figure dans cet
     * iterable entreront dans l'ascenseur (iterables c'est une liste par exemple)
     * @param idASc
     * @param places_disponibles
     * @param niveau
     * @return un Iterable dont l'iterator ne compte pas plus de places_disponibles éléments, attention, l'iterable n'est pas
     * immédiatement itéré (il faut donc le copier si nécessaire)
     */
    public abstract Iterable<Integer> listeInvites(AscId idASc, int places_disponibles, int niveau);

    /**
     * Appellé lorsque l'ascenseur s' arrête à un étage pour lequel l'algorithme n'avais pas demandé
     *  d'ouverture (paramètre booléen dans InterfacePhysique.deplacerAscenseur)
     * @param idAscenseur
     */
    public abstract void arretSansOuverture(AscId idAscenseur);

    /**
     * appelé lorsque les portes de l'ascenseur se sont fermées
     *
     * @param niveau      auquel l'actio a lieue
     * @param idAscenseur de l'ascenseur ouvert
     */
    public abstract void finDeTransfertDePersonnes(AscId idAscenseur);

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
