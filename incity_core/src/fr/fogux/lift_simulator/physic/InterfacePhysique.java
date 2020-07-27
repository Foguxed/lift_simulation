package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

/**
 *
 * @author Florent
 *
 *         seules ces méthodes statiques doivent être appelées par le Programme
 *         en lui même
 */
public class InterfacePhysique
{
    protected final Simulation simu;

    public InterfacePhysique(final Simulation simu)
    {
        this.simu = simu;
    }

    /**
     *
     * @param idAscenseur ascenseur concerné
     * @return le nombre de personnes présentes dans l'ascenseur
     */
    public int getNbPersonnes(final AscId id)
    {
        return simu.getImmeubleSimu().getAscenseur(id).getNbPersonnesIn();
    }

    /**
     *
     * @param ascenseurId
     * @param destination la nouvelle destination
     * @param ouvrirPortes true si l'ascenseur doit ouvrir ses portes à cet etage, la fonction finTransfertPersonne sera alors
     * appellée, sinon, la fonction arretSansOuverture sera appellée
     *
     */
    public void changerDestination(final AscId ascenseurId, final int destination, final boolean ouvrirPortes)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurId).setObjectif(destination, ouvrirPortes);
    }

    /**
     *
     * @param id
     * @return EtatAsc permet de connaitre le sens (montée descente arrêt) et le premier etage atteignable
     */

    public EtatAsc getEtat(final AscId id)
    {
        return simu.getImmeubleSimu().getAscenseur(id).getEtat();
    }

    /**
     *
     * @param id
     * @param objectif
     * @return -1 si l'ascenseur est bloque, ou est géné par un ascenseur voisin sinon
     * la durée du demplacement de l'ascenseur vers cet objectif depuis sa position actuelle
     * 
     */
    @Deprecated
    public long getDistanceTemporelle(final AscId id, final float objectif)
    {
        return -1;
    }

    /**
     * allume/éteint un bouton parmis les boutons présents dans les etages, un
     * bouton allumé aura pour effet de dissuader les personnes d'appuyer a nouveau
     * dessus
     *
     * @param niveau auquel se trouve le bouton
     * @param on     true le bouton doit être allumé
     * @param haut   true si l'action doit être effectuée sur le bouton du haut,
     *               false: bas
     */
    public void changerEtatBouton(final int niveau, final boolean on, final boolean haut)
    {
        simu.getImmeubleSimu().getEtage(niveau).setBoutonState(on, haut);
    }

    /**
     *
     * allume/éteint un bouton parmis les boutons présents dans les ascenseurs, un
     * bouton allumé aura pour effet de dissuader les personnes d'appuyer a nouveau
     * dessus
     * n'a aucun effet si l'algorithme est utilisé sans générer de journal
     *
     * @param ascenseurid ascenseur conserné
     * @param niveau      correspondant au bouton concerné
     * @param allume      true si le bouton doit être allumé
     */
    public void changerEtatBoutonAscenseur(final AscId ascenseurid, final int niveau, final boolean allume)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurid).changerEtatBouton(niveau, allume);
    }

    /**
     * Fera apparaitre une ligne à l'instant d'execution de cette méthode dans une console qui s'affichera lors de l'animation,
     * n'a aucun effet si l'algorithme est utilisé sans générer de journal
     * @param val
     */

    public void println(final Object val)
    {
        if(simu.doPrint())
        {
            //new EvenementConsoleLine(Utils.getTimeString(simu.getTime()) + " " + val).print(simu);
        }
    }

    public void systemPrintLn(final Object o)
    {
        System.out.println(o);
    }
}
