package fr.fogux.lift_simulator.physic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.EvenementAscenseurCapteurNiveau;
import fr.fogux.lift_simulator.evenements.EvenementChangementDeplacementAscenseur;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonAscenseur;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.mind.ProgrammeEntryListener;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.stats.StatsCarrier;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.utils.Utils;

public class AscenseurSimu extends Ascenseur implements StatsCarrier// extends Ascenseur<PersonneSimu>
{
    //protected int niveauActuel;
    //en niveau par seconde
    protected List<PersonneSimu> listeDePersonne = new ArrayList<PersonneSimu>();
    protected int evacuationIndex = 0;
    protected Evenement nextNiveauTriger;
    protected Evenement ascConstantSpeed;
    
    protected EtageSimu etageArrime = null;
    protected int personnesTransportes = 0;
    protected float deplacementTotal;
    protected float lastRealLevel;
    
    protected float posAuDernierDemarage;
    protected long tAuDernierDemarage;
    
    protected final float distPourArreter;
    protected final float distPourTroisPhases;
    protected float dernierNiveauArrete;
    
    protected boolean vaVersHaut;
    protected boolean checkerLesEtages;
    
    protected int dernierCapteurNiveau = 0;
    
    public AscenseurSimu(int id,int personnesMax)
    {
        super(id,personnesMax);
        distPourArreter = TimeConfig.getAscenseurSpeed()*TimeConfig.getAscenseurSpeed()/ (2f*TimeConfig.getAscenseurAcceleration());
        distPourTroisPhases = TimeConfig.getAscenseurSpeed()*TimeConfig.getAscenseurSpeed()/ (2f*TimeConfig.getAscenseurDecelleration())
                +distPourArreter;
        lastRealLevel = getRealLevel();
    }
    
    public void sortieDe(PersonneSimu personne)
    {
        //System.out.println("Sortie de qulequn ");
        personnesTransportes ++;
        listeDePersonne.remove(personne);
        evacuateNext();
    }
    
    public void entreeDe(PersonneSimu personne)
    {
        listeDePersonne.add(personne);
    }
    
    public boolean accrocheEtage(int niveau)
    {
        return Math.abs((float)niveau-getRealLevel())<0.15 && arrete() ;
    }
    
    protected boolean arrete()
    {
        return vi == 0 && acceleration == 0;
    }
    
    public int getId()
    {
        return id;
    }
    
    public int getNbPersonnesIn()
    {
        return listeDePersonne.size();
    }
    
    public void niveauTriger(int niveau)
    {
        ProgrammeEntryListener.capteurDeNiveau(id, niveau);
        if(!Simulateur.getImmeubleSimu().dansImmeuble(niveau))
        {
            throw new SimulateurAcceptableException("Ascenseur hors limite de l'immeuble");
        }
        predictNextNiveauTrigger();
    }
    
    public void predictNextNiveauTrigger()
    {
        if(nextNiveauTriger != null)
        {
            nextNiveauTriger.cancel();
            nextNiveauTriger = null;
        }
        if(checkerLesEtages)
        {
            int nextLevel;
            float accDecel;
            float accAccel;
            float nominalSpeed;
            if(vaVersHaut)
            {
                dernierCapteurNiveau ++;
                accDecel = -TimeConfig.getAscenseurDecelleration();
                nominalSpeed = TimeConfig.getAscenseurSpeed();
                accAccel = TimeConfig.getAscenseurAcceleration();
            }
            else
            {
                dernierCapteurNiveau --;
                accDecel = TimeConfig.getAscenseurDecelleration();
                nominalSpeed = -TimeConfig.getAscenseurSpeed();
                accAccel = -TimeConfig.getAscenseurAcceleration();
            }
            nextLevel = dernierCapteurNiveau;
            long tChoisir;
            //System.out.println(" on essaye pour le " + nextLevel + " pos actuelle " + getRealLevel() +" au debut mvmt " + posAuDernierDemarage);
            //System.out.println("maxpour 3 phases " +distPourTroisPhases + "val dist " + (nextLevel-posAuDernierDemarage));
            if(Math.abs(nextLevel-posAuDernierDemarage) < distPourTroisPhases)
            {
                tChoisir = (long)Math.sqrt((double) ((nextLevel-posAuDernierDemarage)*2*accDecel/(accAccel*(accDecel - accAccel))));
                //System.out.println("PAS VITESSE SEULE POSSIBLE innerTime " + accAccel +" g3" + accDecel);     
                //System.out.println("innerTime " + GestionnaireDeTaches.getInnerTime()); 
            }
            else
            {
                tChoisir = (long)(nominalSpeed/(2*accDecel) +
                        nominalSpeed/(2*accAccel)+(nextLevel-posAuDernierDemarage)/nominalSpeed);
            }
            //System.out.println("innerTime " + GestionnaireDeTaches.getInnerTime());
            //System.out.println("tChoisir " + tChoisir + " tdernierdem " + tAuDernierDemarage);
            //System.out.println("total " + (tAuDernierDemarage + tChoisir) +" acceleration " + acceleration +" a larret " + arrete() +" vitesse " + vi);
            new EvenementAscenseurCapteurNiveau(tAuDernierDemarage + tChoisir,id,nextLevel);
        }
    }
    
    
    protected float getRealLevel()
    {
        return getPosition(GestionnaireDeTaches.getInnerTime()).y;
    }
    
    public void deplacerVers(boolean haut)
    {
        checkerLesEtages = true;
        posAuDernierDemarage = getRealLevel();
        tAuDernierDemarage = GestionnaireDeTaches.getInnerTime();
        if(etageArrime != null)
        {
            throw new SimulateurAcceptableException("l'ascenseur " + id + " a tente de se deplacer alors que ses portes etaient ouvertes");
        }
        else if(!arrete())
        {
            throw new SimulateurAcceptableException("l'ascenseur " + id + " a tente de se deplacer alors qu'il n'était pas a l'arret");
        }
        else
        {
            vaVersHaut = haut;
            long newTi = tAuDernierDemarage;
            float newXi = posAuDernierDemarage;
            float newAcc;
            
            float nextVitesse;
            if(haut)
            {
                 newAcc = TimeConfig.getAscenseurAcceleration();
                 nextVitesse = TimeConfig.getAscenseurSpeed();
            }
            else
            {
                newAcc = -TimeConfig.getAscenseurAcceleration();
                nextVitesse = -TimeConfig.getAscenseurSpeed();
            }
           // System.out.println("DEP vers " + newTi);
            new EvenementChangementDeplacementAscenseur(newTi, id, ti, xi, vi, acceleration, newXi, 0, newAcc, true);
            long tempsAvantVitesseCst = (long ) (nextVitesse/newAcc);
            
            
            //System.out.println("CHGMT VITESSE A " + (tempsAvantVitesseCst+newTi) + " nextVi " + nextVitesse + " nextAcc " + newAcc);
            ascConstantSpeed = new EvenementChangementDeplacementAscenseur(tempsAvantVitesseCst+newTi,id,newTi,newXi,
                    0,newAcc,newAcc*tempsAvantVitesseCst*tempsAvantVitesseCst/2+newXi,nextVitesse,0,true);
            predictNextNiveauTrigger();
            //System.out.println("DEP vers 2 Nouat");
        }
    }
    
    public void stopDeplacement()
    {
        //System.out.println("STOPPER !!!!");
        checkerLesEtages = false;
        if(arrete())
        {
            throw new SimulateurAcceptableException("l'ascenseur " + id + " a tente de s'arreter alors qu'il etait deja arrete");
        }
        if(ascConstantSpeed != null)
        {
            ascConstantSpeed.cancel();
            ascConstantSpeed = null;
        }
        
        long newTi = GestionnaireDeTaches.getInnerTime();
        float newXi = getRealLevel();
        float newVi = acceleration*(GestionnaireDeTaches.getInnerTime() - ti) + vi;
        float newAcc = vaVersHaut ? -TimeConfig.getAscenseurDecelleration():TimeConfig.getAscenseurDecelleration();
        
        
        new EvenementChangementDeplacementAscenseur(newTi, id, ti, xi, vi, acceleration, newXi, newVi, newAcc, true);
        
        long deltaArretTime = (long)(-newVi/newAcc);
        long arretTime = newTi + deltaArretTime;
        float arretNiveau = newAcc*deltaArretTime*deltaArretTime/2 + newVi*deltaArretTime + newXi;
        //System.out.println("acceleration 0 a " + arretTime);
        new EvenementChangementDeplacementAscenseur(arretTime, id, newTi, newXi, newVi, newAcc, arretNiveau, 0, 0, true);
    }
    
    @Override
    public void setDeplacement(long time,float newXi, float newVi,float acceleration)
    {
        updateDepStat();
        super.setDeplacement(time, newXi, newVi, acceleration);
        //System.out.println(" atta " + time +" newVi " + newVi + " new ac" + acceleration + " time " + GestionnaireDeTaches.getInnerTime());
        if(arrete())
        {
            ProgrammeEntryListener.ascenseurArrete(id);
        }
    }
    
    protected void updateDepStat()
    {
        deplacementTotal += Math.abs(lastRealLevel - getRealLevel());
        lastRealLevel = getRealLevel();
    }
    
    @Override
    public void changerEtatBouton(int bouton,boolean allume)
    {
        System.out.println(Utils.getTimeString(GestionnaireDeTaches.getInnerTime()) + " boutonsEvent " + bouton + " allueme " + allume + " list " + boutonsAllumes);
        new EvenementBoutonAscenseur(bouton, id, allume, boutonsAllumes.contains(bouton)).print();
        super.changerEtatBouton(bouton, allume);
    }
    
    public void lorsqueOuvert()
    {
        //Utils.msg(this, "taille liste personnes "+ listeDePersonne.size());
        evacuateNext();
    }
    
    public void debutMouvementPorte(int niveau)
    {
        if(!accrocheEtage(niveau))
        {
            throw new SimulateurAcceptableException("Ascenseur " + id + " pas assez proche de l'etage " + niveau + " ou en mouvement, action sur les portes impossible");
        }
        etageArrime = Simulateur.getImmeubleSimu().getEtage(niveau);
    }
    
    public void lorsqueFerme()
    {
        EtageSimu etageSimutemp = etageArrime;
        etageArrime = null;
        //Utils.msg(this, "lorsque ferme debut ");
        ProgrammeEntryListener.ascenseurFerme(id);
        etageSimutemp.rappuyerBoutonsSiNecessaire();
        
    }
    
    public void evacuateNext()
    {
        
        if(listeDePersonne.size()>evacuationIndex)
        {
            /*boolean deleteMe = false;;
            if(listeDePersonne.size() > 1)
            {
                deleteMe = listeDePersonne.get(0) == listeDePersonne.get(1);
            }*/
            
            boolean bool = listeDePersonne.get(evacuationIndex).jeSortDeAscenseur(etageArrime);
            //System.out.println(" listSize " + listeDePersonne.size() + " index " + evacuationIndex +" deuxpersidentiques " + deleteMe + " bool " + bool);
            if(!bool)
            {
                evacuationIndex ++;
                evacuateNext(); 
            }
        }
        else
        {
            evacuationIndex = 0;
            etageArrime.onOuverture(this);
        }
    }

    @Override
    public void printStats(DataTagCompound compound)
    {
        updateDepStat();
        compound.setInt(TagNames.nbPersonnesTransportees, personnesTransportes);
        compound.setFloat(TagNames.deplacementTotal, deplacementTotal);
        compound.setInt(TagNames.capacitePersonnes, persMax);
        compound.setFloat(TagNames.acceleration, TimeConfig.getAscenseurAcceleration());
        compound.setFloat(TagNames.deceleration, TimeConfig.getAscenseurDecelleration());
        compound.setFloat(TagNames.vitesse, TimeConfig.getAscenseurSpeed());
        compound.setLong(TagNames.tempsEntreePersonne, TimeConfig.getDureeSortieEntreePersonne());
        compound.setLong(TagNames.tempsOuverturePortes, TimeConfig.getDureePortes());
    }

    
}
