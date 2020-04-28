package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.animation.EvenementConsoleLine;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;
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

    public void deplacerAscenseur(final AscId ascenseurId, final int destination, final boolean ouvrirPortes)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurId).setObjectif(destination, ouvrirPortes);
    }

    public EtatAsc getEtat(final AscId id)
    {
        return simu.getImmeubleSimu().getAscenseur(id).getEtat(simu.getTime());
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
     *
     * @param ascenseurid ascenseur conserné
     * @param niveau      correspondant au bouton concerné
     * @param allume      true si le bouton doit être allumé
     */
    public void changerEtatBoutonAscenseur(final AscId ascenseurid, final int niveau, final boolean allume)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurid).changerEtatBouton(niveau, allume);
    }

    public void println(final String val)
    {
        new EvenementConsoleLine(Utils.getTimeString(simu.getTime()) + " " + val).print(simu);
    }

}
