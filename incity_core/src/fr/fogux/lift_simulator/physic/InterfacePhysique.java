package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.evenements.animation.EvenementConsoleLine;
import fr.fogux.lift_simulator.utils.Utils;

/**
 * 
 * @author Florent
 *
 *         seules ces méthodes statiques doivent être appelées par le Programme
 *         en lui même
 */
public class InterfacePhysique
{
    /**
     * 
     * @param idAscenseur ascenseur concerné
     * @return le nombre de personnes présentes dans l'ascenseur
     */
    public static int getNbPersonnes(int idAscenseur)
    {
        return Simulateur.getImmeubleSimu().getAscenseur(idAscenseur).getNbPersonnesIn();
    }

    public static int getEtageMin()
    {
        return Simulateur.getImmeubleSimu().getEtageMin();
    }

    public static int getEtageMaxNonInclu()
    {
        return Simulateur.getImmeubleSimu().getEtageMaxNonInclu();
    }

    /**
     * permet d'entammer un déplacement linéaire de l'ascenseur. la méthode peut
     * provoquer une erreur si l'ascenseur n'est pas en état de se déplacer.
     * 
     * @param ascenseurId ascenseur concerné
     * @param haut        true pour déplacer l'ascenseur vers le haut
     */
    public static void deplacerAscenseur(int ascenseurId, boolean haut)
    {
        Simulateur.getImmeubleSimu().getAscenseur(ascenseurId).deplacerVers(haut);
    }

    /**
     * stop le déplacement de l'ascenseur
     * 
     * @param ascenseurId ascenseur concerné
     */
    public static void stoperAscenseur(int ascenseurId)
    {
        Simulateur.getImmeubleSimu().getAscenseur(ascenseurId).stopDeplacement();
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
    public static void changerEtatBouton(int niveau, boolean on, boolean haut)
    {
        Simulateur.getImmeubleSimu().getEtage(niveau).setBoutonState(on, haut);
    }

    /**
     * 
     * allume/éteint un bouton parmis les boutons présents dans les ascenseurs, un
     * bouton allumé aura pour effet de dissuader les personnes d'appuyer a nouveau
     * dessus
     * 
     * @param ascenseurid ascenseur conserné
     * @param niveau      correspondant au bouton concerné
     * @param allume      true si le bouton doit être allumé
     */
    public static void changerEtatBoutonAscenseur(int ascenseurid, int niveau, boolean allume)
    {
        Simulateur.getImmeubleSimu().getAscenseur(ascenseurid).changerEtatBouton(niveau, allume);
    }

    /**
     * ouvre les portes d'un ascenseur pour permettre aux personnes en attente
     * d'entrer. la méthode peut provoquer une erreur si l'état de l'immeuble n'est
     * pas compatible avec l'ouverture de la porte.
     * 
     * @param niveau      auquel la porte doit être ouverte
     * @param idAscenseur ascenseur dont la porte doit s'ouvrir
     */
    public static void ouvrirLesPortes(int niveau, int idAscenseur)
    {
        Simulateur.getImmeubleSimu().getAscenseur(idAscenseur).debutMouvementPorte(niveau);
        Simulateur.getImmeubleSimu().getEtage(niveau).ouvrir(Simulateur.getImmeubleSimu().getAscenseur(idAscenseur));
    }

    /**
     * ouvre les portes d'un ascenseur pour permettre aux personnes en attente
     * d'entrer. la méthode peut provoquer une erreur si l'état de l'immeuble n'est
     * pas compatible avec la fermeture de la porte.
     * 
     * @param niveau      auquel la porte doit être fermée
     * @param idAscenseur ascenseur dont la porte doit se fermer
     */

    public static void fermerLesPortes(int niveau, int idAscenseur)
    {
        Simulateur.getImmeubleSimu().getAscenseur(idAscenseur).debutMouvementPorte(niveau);
        Simulateur.getImmeubleSimu().getEtage(niveau).fermer(Simulateur.getImmeubleSimu().getAscenseur(idAscenseur));
    }

    public static void println(String val)
    {
        new EvenementConsoleLine(Utils.getTimeString(GestionnaireDeTaches.getInnerTime()) + " " + val).print();
    }

}
