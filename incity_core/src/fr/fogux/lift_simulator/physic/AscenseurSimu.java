package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementArriveAscSansOuverture;
import fr.fogux.lift_simulator.evenements.EvenementMouvementPortes;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonAscenseur;
import fr.fogux.lift_simulator.evenements.animation.EvenementChangementPlannifier;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.stats.StatsCarrier;
import fr.fogux.lift_simulator.structure.AscDeplacementFunc;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.structure.EtatAsc;
import fr.fogux.lift_simulator.utils.Utils;

public class AscenseurSimu extends Ascenseur implements StatsCarrier// extends Ascenseur<PersonneSimu>
{
    protected final Simulation simu;
    // protected int niveauActuel;
    // en niveau par seconde
    protected List<PersonneSimu> listeDePersonne = new ArrayList<>();
    protected int evacuationIndex = 0;
    protected Evenement ascConstantSpeed;

    protected EtageSimu etageTransfert = null;
    protected int personnesTransportes = 0;
    protected boolean ouvrirPortesProchaineDest = false;
    protected float deplacementTotal;


    protected AscenseurSimu ascSuperieur;
    protected AscenseurSimu ascInferieur;

    protected AscenseurSimu ascAttendu;

    protected Evenement prochainEventArrivee = null;

    protected Set<AscenseurSimu> listeners = new HashSet<>();

    protected Iterator<Integer> iteratorInvites = null;
    
    protected float xObjectifActuel;
    
    protected int etageObjectif;
    
    public AscenseurSimu(final Simulation simu, final AscId id, final float initialY)
    {
        super(simu.getConfig() ,id, simu.getConfig().nbPersMaxAscenseur(), initialY);
        this.simu = simu;
        this.xObjectifActuel = initialY;
        this.etageObjectif = (int) initialY;
    }

    public void setAscSuperieur(final AscenseurSimu asc)
    {
        ascSuperieur = asc;
    }

    public void setAscInferieur(final AscenseurSimu asc)
    {
        ascInferieur = asc;
    }

    public int getNbPersonnesIn()
    {
        return listeDePersonne.size();
    }

    @Override
    public void changerEtatBouton(final int bouton, final boolean allume)
    {
        System.out.println(
            Utils.getTimeString(simu.getTime()) + " boutonsEvent " + bouton + " allueme " + allume
            + " list " + boutonsAllumes);
        if(simu.doPrint())
        {
            new EvenementBoutonAscenseur(bouton, id, allume, boutonsAllumes.contains(bouton)).print(simu);
            super.changerEtatBouton(bouton, allume);
        }
    }

    public void registerListener(final AscenseurSimu asc)
    {
        listeners.add(asc);
    }

    public void unregisterListener(final AscenseurSimu asc)
    {
        listeners.remove(asc);
    }


    public void neighboorMoved()
    {
        tentativeAtteinteObjectif(); // on est ici certain que l'ascenseur n'était pas dans un état bloqué
    }

    private void tentativeAtteinteObjectif()
    {
        goToObjectif(etageObjectif);
    }

    protected boolean bloque()
    {
        return etageTransfert != null || (prochainEventArrivee != null && plannifier.notMoving(simu.getTime()));
    }

    public void setObjectif(final int newEtageObjectif, final boolean ouvrirPortes)
    {
        if(newEtageObjectif < simu.getConfig().getNiveauMin() || newEtageObjectif > simu.getConfig().getNiveauMax())
        {
            throw new SimulateurAcceptableException(this + " a tentete de se deplacer vers " + newEtageObjectif + " qui n'est pas un etage de l'immeuble ");
        }
        etageObjectif = newEtageObjectif;
        ouvrirPortesProchaineDest = ouvrirPortes;
        if(!bloque())
        {
            if(prochainEventArrivee != null)
            {
                prochainEventArrivee.cancel(simu);
                prochainEventArrivee = null;
            }
            tentativeAtteinteObjectif();
        }
    }

    /**
     * Va tenter d'approcher l'ascenseur de cette destination quelque soit l'état de l'ascenseur
     * @param newEtageObjectif
     */
    private void goToObjectif(final int newEtageObjectif)
    {

        if(ascAttendu != null)
        {
            ascAttendu.unregisterListener(this);
            ascAttendu = null;
        }
        etageObjectif = newEtageObjectif;
        float xObjectifCorrige = newEtageObjectif;
        boolean canReachEtage = true;
        if(newEtageObjectif > xObjectifActuel)
        {
            if(collisionSuperieure(newEtageObjectif))
            {
                xObjectifCorrige = ascSuperieur.xObjectifActuel - simu.getConfig().getMargeInterAscenseur();
                canReachEtage = false;
                ascAttendu = ascSuperieur;
                ascSuperieur.registerListener(this);
            }
        }
        else
        {
            if(collisionInferieure(newEtageObjectif))
            {
                xObjectifCorrige = ascInferieur.xObjectifActuel + simu.getConfig().getMargeInterAscenseur();
                canReachEtage = false;
                ascAttendu = ascInferieur;
                ascInferieur.registerListener(this);
            }
        }
        if(Math.abs(xObjectifCorrige - xObjectifActuel) > ConfigSimu.XEQUALITY_MARGIN)
        {
            bruteDeplacer(xObjectifCorrige);
        }

        if(canReachEtage)
        {
            lancerProchainsEvents();
        }
    }

    protected void lancerProchainsEvents()
    {
        if(ouvrirPortesProchaineDest)
        {
            prochainEventArrivee = new EvenementMouvementPortes(Math.max(getInstantProchainArret(), simu.getTime()),simu.getConfig(), id, etageObjectif, true);
        }
        else
        {
            prochainEventArrivee = new EvenementArriveAscSansOuverture(Math.max(getInstantProchainArret(), simu.getTime()),id);
        }
        prochainEventArrivee.runOn(simu);
    }
    
    private long getInstantProchainArret()
    {
    	return plannifier.EF.t;
    }
    
    protected void bruteDeplacer(final float newXObjectif)
    {
    	DataTagCompound oldPlannifier = null;
    	if(simu.doPrint())
    	{
        	oldPlannifier = Compoundable.compound(plannifier);
    	}
        
        final long timeChangement = simu.getTime();
        
        final float oldXi = plannifier.EI.x;
        plannifier.initiateMovement(timeChangement, newXObjectif, ascSuperieur, ascInferieur);
        
        if(simu.doPrint())
    	{
        	DataTagCompound newPlannifier = Compoundable.compound(plannifier);
        	new EvenementChangementPlannifier(id, oldPlannifier, newPlannifier).print(simu);
    	}
        
        deplacementTotal += Math.abs(oldXi - plannifier.EI.x);
        xObjectifActuel = newXObjectif;
        
        for(final AscenseurSimu a : listeners)
        {
            a.neighboorMoved();
        }
    }
    
    /**
     * 
     * @param newXObjectif
     * @param timeChangement
     * @param c
     */
    protected void changerXObjectif(final float newXObjectif, final long timeChangement, final ConfigSimu c)
    {
        
    }
    
    private boolean collisionInferieure(final float objectif)
    {
        return ascInferieur != null && objectif < ascInferieur.xObjectifActuel + simu.getConfig().getMargeSupInterAscenseur();
    }

    private boolean collisionSuperieure(final float objectif)
    {
        return ascSuperieur != null && objectif > ascSuperieur.xObjectifActuel - simu.getConfig().getMargeSupInterAscenseur();
    }
    

    
    
    /**
     *
     * @param time > time actuel (très important), sinon le résultat sera incohérent
     * @return l'etat dans l'ascenseur à cet instant si aucun ordre ne luit est donne
     */
    public EtatAsc getEtat()
    {
    	final long time = simu.getTime();
        if(plannifier.notMoving(time))
        {
            if(bloque())
            {
                return new EtatAsc(EtatAscenseur.BLOQUE, xObjectifActuel, Integer.MIN_VALUE);
            }
            else
            {
                return new EtatAsc(EtatAscenseur.ARRET, xObjectifActuel, Integer.MIN_VALUE);
            }
        }
        else
        {
            return plannifier.getFullMovingEtat(time);
        }
    }

    public void arriveSansOuverture()
    {
        prochainEventArrivee = null;
        simu.getPrgm().arretSansOuverture(id);
    }

    public void finOuverturePortes(final int niveau)
    {
        prochainEventArrivee = null;
        etageTransfert = simu.getImmeubleSimu().getEtage(niveau);
        System.out.println(" fin ouverture portes " + etageTransfert + " id " + id);
        evacuateNext();
    }

    public void evacuateNext()
    {

        if (listeDePersonne.size() > evacuationIndex)
        {
            final boolean bool = listeDePersonne.get(evacuationIndex).jeSortDeAscenseur(etageTransfert.getNiveau());
            if (!bool)
            {
                evacuationIndex++;
                evacuateNext();
            }
        }
        else
        {
            evacuationIndex = 0;
            finEvacuation();
        }
    }

    public void sortieDe(final PersonneSimu personne)
    {
        personnesTransportes++;
        listeDePersonne.remove(personne);
        evacuateNext();
    }

    public void finEvacuation()
    {
        iteratorInvites = simu.getPrgm().listeInvites(id, simu.getConfig().nbPersMaxAscenseur() - getNbPersonnesIn(), etageTransfert.getNiveau()).iterator();
        enterNext();
    }

    public void enterNext()
    {
        if(iteratorInvites.hasNext())
        {
            final Integer id = iteratorInvites.next();
            if(id == null || id >= simu.getPersonneListSize())
            {
                throw new SimulateurAcceptableException("l'id de personne " + id + " ne designe aucune personne connue " + toString());
            }
            else if(getNbPersonnesIn() == simu.getConfig().nbPersMaxAscenseur())
            {
                throw new SimulateurAcceptableException(this + " est déjà plein et ne peut pas acceuillir " + simu.getPersonne(id));
            }
            else
            {
                simu.getPersonne(id).tenterEntrerAscenseur(this,etageTransfert.getNiveau());
            }
        }
        else
        {
            finirLeTransfert();
        }
    }

    public void estEntre(final PersonneSimu personne)
    {
        listeDePersonne.add(personne);
        enterNext();
    }

    public void finirLeTransfert()
    {
        new EvenementMouvementPortes(simu.getTime(), simu.getConfig(), id, etageTransfert.getNiveau(), false).runOn(simu);
    }

    public void finFermeturePortes(final int niveau)
    {
        simu.getPrgm().finDeTransfertDePersonnes(id, niveau);
        final EtageSimu etageSimutemp = etageTransfert;
        System.out.println("fin fermeture portes etage transfert " + etageTransfert + " asc " + id + " niveau " + niveau + " " + simu.getImmeubleSimu().getEtage(niveau));
        etageTransfert = null;
        etageSimutemp.rappuyerBoutonsSiNecessaire();
        if(etageObjectif != niveau)
        {
            tentativeAtteinteObjectif();
        }
    }

    @Override
    public void printStats(final DataTagCompound compound)
    {
        compound.setInt(TagNames.nbPersonnesTransportees, personnesTransportes);
        compound.setFloat(TagNames.deplacementTotal, deplacementTotal);
        compound.setFloat(TagNames.acceleration, simu.getConfig().getAscenseurAcceleration());
    }

}