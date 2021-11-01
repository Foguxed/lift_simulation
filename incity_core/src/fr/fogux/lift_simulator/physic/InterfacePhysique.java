package fr.fogux.lift_simulator.physic;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.EvenementSimplePing;
import fr.fogux.lift_simulator.evenements.EventPingWithData;
import fr.fogux.lift_simulator.evenements.animation.EvenementConsoleLine;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;
import fr.fogux.lift_simulator.utils.Utils;

/**
 *Fait l'interface entre l'algorithme et le simulateur, l'algorithme a accès à cet objet
 */
public class InterfacePhysique
{
    public final Simulation simu;

    public InterfacePhysique(final Simulation simu)
    {
        this.simu = simu;
    }

    /**
     *
     * @param ascenseurId
     * @param destination la nouvelle destination
     * @param ouvrirPortes true si l'ascenseur doit ouvrir ses portes à cet etage, la fonction finTransfertPersonne sera alors
     * appellée, sinon, la fonction arretSansOuverture de l'agorithme sera appellée
     *
     */
    public void changerDestination(final AscId ascenseurId, final int destination, final boolean ouvrirPortes)
    {
        simu.getImmeubleSimu().getAscenseur(ascenseurId).setObjectif(destination, ouvrirPortes);
    }

    /**
     *
     * @param id
     * @return EtatAsc permet à l'algorithme de connaitre le sens de l'ascenseur (montée descente arrêt) et le premier etage atteignable sans demi-tour
     */

    public EtatAsc getEtat(final AscId id)
    {
        return simu.getImmeubleSimu().getAscenseur(id).getEtat();
    }
    
    /**
     * met en pause la simulation après l'exécution complète de l'évènement actuel
     */
    
    public void thenpause()
    {
        simu.getGestio().thenpause();
    }
    
    /**
     * met en pause la simulation dans son etat actuel, l'évènement en cours sera appelé de nouveau
     */
    public void interrupt()
    {
        simu.getGestio().interrupt();
    }
    
    /**
     * enregistre un évènement qui appelera algorithme.ping() dans delayTime (en millisecondes).
     * @param delayTime
     */
    public void registerPing(final int delayTime)
    {
        new EvenementSimplePing(simu.getTime() + delayTime).runOn(simu);
    }
    
    /**
     * enregistre un évènement qui appelera algorithme.ping() dans delayTime (en millisecondes), l'algorithme pourra alors récupérer les données fournies
     *  dans data en utilisant InterfacePhysique.getPingStoredData().
     * @param delayTime
     */
    public void registerPingData(final int delayTime, final Object data)
    {
        new EventPingWithData<>(simu.getTime() + delayTime,data).runOn(simu);
    }

    public <A> A getPingStoredData()
    {
        return ((EventPingWithData<A>)simu.getGestio().currentEv).data;
    }

    public boolean contient(final AscId ascenseur, final int persId)
    {
        return simu.getImmeubleSimu().getAscenseur(ascenseur).contient(persId);
    }

    public void systemPrintLn(final Object o)
    {
        System.out.println(o);
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
    * allume/éteint un bouton parmis les boutons présents dans les etages, un
    * bouton allumé aura pour effet de dissuader les personnes d'appuyer a nouveau
    * dessus
    *
    * @param niveau auquel se trouve le bouton
    * @param on     true le bouton doit être allumé
    * @param haut   true si l'action doit être effectuée sur le bouton du haut,
    *               false: bas
    */
   
   /**
   *
   * @param idAscenseur ascenseur concerné
   * @return le nombre de personnes présentes dans l'ascenseur
   */
	  public int getNbPersonnes(final AscId id)
	  {
	      return simu.getImmeubleSimu().getAscenseur(id).getNbPersonnesIn();
	  }
	  
	   public void changerEtatBouton(final int niveau, final boolean on, final boolean haut)
	   {
	       simu.getImmeubleSimu().getEtage(niveau).setBoutonState(on, haut);
	   }
	    /**
	 * Fera apparaitre une ligne à l'instant d'execution de cette méthode dans une console qui s'affichera lors de l'animation,
	 * n'a aucun effet si l'algorithme est utilisé sans générer de journal
	 * @param val
	 */
	   
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
	  
    public void println(final Object val)
    {

        if(simu.doPrint())
        {
            new EvenementConsoleLine(Utils.getTimeString(simu.getTime()) + " " + val).print(simu);
        }
    }
}
