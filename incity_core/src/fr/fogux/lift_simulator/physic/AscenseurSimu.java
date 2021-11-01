package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementArriveAscSansOuverture;
import fr.fogux.lift_simulator.evenements.EvenementMouvementPortes;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonAscenseur;
import fr.fogux.lift_simulator.evenements.animation.EvenementChangementPlanifier;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.stats.StatCarrier;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.structure.EtatAsc;
import fr.fogux.lift_simulator.utils.Utils;

public class AscenseurSimu extends Ascenseur implements StatCarrier// extends Ascenseur<PersonneSimu>
{
    protected final Simulation simu;
    // protected int niveauActuel;
    // en niveau par seconde
    protected List<PersonneSimu> listeDePersonne = new ArrayList<>();
    protected int evacuationIndex = 0;

    protected EtageSimu etageTransfert = null;
    protected int personnesTransportes = 0;
    protected boolean ouvrirPortesProchaineDest = false;
    protected float deplacementTotal;


    protected AscenseurSimu ascSuperieur;
    protected AscenseurSimu ascInferieur;

    protected AscenseurSimu ascAttendu;

    protected Evenement prochainEventArrivee = null;

    protected final List<AscenseurSimu> listeners = new ArrayList<>();

    protected final List<Integer> invites;
    protected int inviteIndex = 0;


    protected float xObjectifActuel;

    protected int etageObjectif;

    protected boolean commandeAExecuter;

    public AscenseurSimu(final Simulation simu, final AscId id, final float initialY)
    {
        super(simu.getConfig() ,id, simu.getConfig().nbPersMaxAscenseur(), initialY);
        this.simu = simu;
        invites = new ArrayList<>();
        xObjectifActuel = initialY;
        etageObjectif = (int) initialY;
        commandeAExecuter = false;
        //System.out.println("nouvinstance ascSimu ");
    }



    public AscenseurSimu(final Simulation newSimu, final AscenseurSimu shadowed)
    {
        super(shadowed);
        simu = newSimu;
        for(final PersonneSimu p : shadowed.listeDePersonne)
        {
            listeDePersonne.add(newSimu.getPersonne(p.getId()));
        }
        evacuationIndex = shadowed.evacuationIndex;
        etageTransfert = EtageSimu.shadow(shadowed.etageTransfert, newSimu);

        //System.out.println(" shadowing asc: etage " + shadowed.etageTransfert + " res " + etageTransfert);
        personnesTransportes = shadowed.personnesTransportes;
        deplacementTotal = shadowed.deplacementTotal;
        ouvrirPortesProchaineDest = shadowed.ouvrirPortesProchaineDest;
        prochainEventArrivee = shadowed.prochainEventArrivee;
        invites = new ArrayList<>(shadowed.invites);
        inviteIndex = shadowed.inviteIndex;
        xObjectifActuel = shadowed.xObjectifActuel;
        etageObjectif = shadowed.etageObjectif;
        commandeAExecuter = shadowed.commandeAExecuter;
    }

    public void finalizeShadow(final Simulation newSimu, final AscenseurSimu shadowed)
    {

        ascSuperieur = shadow(shadowed.ascSuperieur,newSimu);
        ascInferieur = shadow(shadowed.ascInferieur,newSimu);
        ascAttendu = shadow(shadowed.ascAttendu,newSimu);
        for(final AscenseurSimu asc : shadowed.listeners)
        {
            listeners.add(shadow(asc,newSimu));
        }
    }

    public static AscenseurSimu shadow(final AscenseurSimu toBeShadowed, final Simulation newSimu)
    {
        if(toBeShadowed == null)
        {
            return null;
        }
        else
        {
            return newSimu.getImmeubleSimu().getAscenseur(toBeShadowed.getId());
        }
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
        commandeAExecuter = false;
        goToObjectif(etageObjectif);
    }

    protected boolean bloque()
    {
        return etageTransfert != null || (prochainEventArrivee instanceof EvenementMouvementPortes && planificateur.notMoving(simu.getTime()));
    }

    public void setObjectif(final int newEtageObjectif, final boolean ouvrirPortes)
    {
        if(newEtageObjectif < simu.getConfig().getNiveauMin() || newEtageObjectif > simu.getConfig().getNiveauMax())
        {
            throw new SimulateurAcceptableException(this + " a tentete de se deplacer vers " + newEtageObjectif + " qui n'est pas un etage de l'immeuble ");
        }
        /*
        if(this.id.monteeId == 1 & this.id.stackId == 0)
        {
        	simu.printConsoleLine("setObjectif bloque " + bloque() + " newEtageObjectif " + newEtageObjectif + " etageTransfert " + etageTransfert + " prochainEventArrivee " + prochainEventArrivee + " previousOuvPortes " + ouvrirPortesProchaineDest + " nextOuvPortes " + ouvrirPortes);
        }*/
        etageObjectif = newEtageObjectif;
        ouvrirPortesProchaineDest = ouvrirPortes;
        commandeAExecuter = true;
        //System.out.println(" setObjectif vers " + newEtageObjectif + " ouvPortes " + ouvrirPortes + " estbloque " + bloque());
        if(!bloque())// pb avec <=: bloque lorsq de l'exec de ouvertureporte avec < collision de ouverture/fermeture portes
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

        //System.out.println(id + " canReachEtage " + canReachEtage + " xObjCorrige " + xObjectifCorrige);
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
        return planificateur.EF.t;
    }

    protected void bruteDeplacer(final float newXObjectif)
    {
        DataTagCompound oldPlanificateur = null;
        if(simu.doPrint())
        {
            oldPlanificateur = Compoundable.compound(planificateur);
        }

        final long timeChangement = simu.getTime();

        final float oldXi = planificateur.EI.x;
        planificateur.initiateMovement(timeChangement, newXObjectif, ascSuperieur, ascInferieur);

        if(simu.doPrint())
        {
            final DataTagCompound newPlanifier = Compoundable.compound(planificateur);
            new EvenementChangementPlanifier(id, oldPlanificateur, newPlanifier).print(simu);
        }

        deplacementTotal += Math.abs(oldXi - planificateur.EI.x);
        xObjectifActuel = newXObjectif;

        final List<AscenseurSimu> voisins = new ArrayList<>(listeners);
        for(final AscenseurSimu a : voisins)
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
        if(planificateur.notMoving(time))
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
            return planificateur.getFullMovingEtat(time);
        }
    }

    public void arriveSansOuverture()
    {
        prochainEventArrivee = null;
    }

    public void finOuverturePortes(final int niveau)
    {
        prochainEventArrivee = null;
        etageTransfert = simu.getImmeubleSimu().getEtage(niveau);

        //System.out.println(" fin ouv portes donc " + etageTransfert);
        evacuateNext();
    }

    public void reRunDemandeDeListe()
    {
        essayerDeLancerEntrees();
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
            essayerDeLancerEntrees();
        }
    }

    public void sortieDe(final PersonneSimu personne)
    {
        personnesTransportes++;
        listeDePersonne.remove(personne);
        evacuateNext();
    }

    private void essayerDeLancerEntrees()
    {
        inviteIndex = 0;
        invites.clear();
        final List<Integer> listeInvites = simu.getPrgm().listeInvites(id, simu.getConfig().nbPersMaxAscenseur() - getNbPersonnesIn(), etageTransfert.getNiveau());
        if(simu.interrupted())
        {
            return;// l'évènement est annulé
        }
        invites.addAll(listeInvites);
        if(simu.paused())
        {
            invites.clear();// on va refaire une demande
        }
        if(!invites.isEmpty())
        {
            enterNext();
        }
        else
        {
            if(etageObjectif == etageTransfert.getNiveau())
            {
                commandeAExecuter = false;
            }
            finirLeTransfert();
        }
    }

    private void enterNext()
    {
        if(inviteIndex < invites.size())
        {
            final Integer id = invites.get(inviteIndex);
            inviteIndex++;
            if(id == null || !simu.isCorrectId(id))
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
            essayerDeLancerEntrees();
        }
    }

    public void estEntre(final PersonneSimu personne)
    {
        //System.out.println("qlqun est entre");
        listeDePersonne.add(personne);
        enterNext();
    }

    public boolean contient(final int persId)
    {
        final boolean b;
        if(invites != null && invites.stream().anyMatch(id -> id == persId))
        {
            return true;
        }
        return listeDePersonne.stream().anyMatch(p -> p.getId() == persId);
    }

    public void finirLeTransfert()
    {
        evacuationIndex = 0;
        //System.out.println(" fin de transfert " + simu.getTime());
        //new Throwable().printStackTrace();
        new EvenementMouvementPortes(simu.getTime(), simu.getConfig(), id, etageTransfert.getNiveau(), false).runOn(simu);
    }

    public void finFermeturePortes(final int niveau)
    {
        final EtageSimu etageSimutemp = etageTransfert;

        etageTransfert = null;

        simu.getPrgm().finDeTransfertDePersonnes(id, niveau);// l'asc n'est plus bloque ici
        if(!simu.paused())
        {
            //System.out.println("etageSimutemp " + etageSimutemp);
            etageSimutemp.rappuyerBoutonsSiNecessaire();
            if(commandeAExecuter)
            {
                tentativeAtteinteObjectif();
            }
        }
    }

    public void reRunFermeturePortes(final int niveau)
    {
        simu.getPrgm().finDeTransfertDePersonnes(id, niveau);// l'asc n'est plus bloque ici
        if(!simu.paused())
        {
            simu.getImmeubleSimu().getEtage(niveau).rappuyerBoutonsSiNecessaire();
            if(commandeAExecuter)
            {
                tentativeAtteinteObjectif();
            }
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