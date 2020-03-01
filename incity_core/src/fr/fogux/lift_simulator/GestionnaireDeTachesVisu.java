package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.fichiers.LiseurDeJournal;
import fr.fogux.lift_simulator.utils.ChainedList;
import fr.fogux.lift_simulator.utils.Maillon;

public class GestionnaireDeTachesVisu extends GestionnaireDeTaches
{
    protected long vitesse = 16;//en millisecondes par immage
    protected ChainedList<Evenement> evenements = new ChainedList<Evenement>();
    protected Executeur executeur;
    protected Executeur executeurInnactif;
    protected boolean sensPositif = true;
    protected long updatedSpeed = 16;
    protected boolean vitesseNeedsUpdate = false;
    //protected Evenement nextNonBufferedEvent;
    /*protected Maillon<Evenement> evenementInf;
    protected Maillon<Evenement> evenementSup;*/
   // protected Thread bufferFillerTrhead;
   /* protected long marge;
    protected boolean buteeNext = false;
    protected boolean buteePrevious = true;*/

    
    public GestionnaireDeTachesVisu()
    {
    }
    
    @Override
    public void runExecuting()
    {
        fillBuffer();
        executeur = new Executeur()
                {
                    @Override
                    protected Maillon<Evenement> getProchainEvent(Maillon<Evenement> event)
                    {
                        return event.getSuivant();
                    }

                    @Override
                    protected boolean peutEtreExecute(Evenement event,long newTime)
                    {
                        return event.getTime() <= newTime;
                    }

                    @Override
                    public boolean isSensPositif()
                    {
                        return true;
                    }

                    @Override
                    protected Maillon<Evenement> getPrecedentEvent(Maillon<Evenement> event)
                    {
                        return event.getPrecedent();
                    }
                };
        executeur.updateExecuteur(evenements.getFirst());
        executeurInnactif = new Executeur()
                {

                    @Override
                    protected Maillon<Evenement> getProchainEvent(Maillon<Evenement> event)
                    {
                        return event.getPrecedent();
                    }
                    
                    @Override
                    protected boolean peutEtreExecute(Evenement event,long newTime)
                    {
                        return event.getTime() >= newTime;
                    }

                    @Override
                    public boolean isSensPositif()
                    {
                        return false;
                    }

                    @Override
                    protected Maillon<Evenement> getPrecedentEvent(Maillon<Evenement> event)
                    {
                        return event.getSuivant();
                    }
                };
        System.out.println("evenementActuelUpdate ");
        //runEventActuel();
        
    }
    
    @Override
    protected boolean marcheArriereEnCours()
    {        
        return vitesse<0;        
    }
    
    protected void fillBuffer()
    {
        evenements.addAll(new LiseurDeJournal());
    }
    
    public void update()
    {
        if(vitesse != 0l)
        {
            executeur.executerJusque(innerTime + vitesse);
        }
        
        if(vitesseNeedsUpdate)
        {
            vitesseNeedsUpdate = false;
            vitesse = updatedSpeed;
            vitesseUpdated();
        }
        //System.out.println("fin update");
        //fillBuffer();
    }
    
    public void stopper()
    {
        updatedSpeed = 0;
        vitesseNeedsUpdate = true;
    }
    
    public void modifVitesse(boolean sensPos)
    {
        if(sensPos)
        {
            updatedSpeed = vitesse+16;
        }
        else
        {
            updatedSpeed = vitesse-16;
        }
        vitesseNeedsUpdate = true;
        
    }
    
    protected void vitesseUpdated()
    {
        System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        System.out.println("NEW SPEED " + vitesse);
        if(vitesse != 0)
        {
            System.out.println("check speed change, sensPos " + sensPositif + " vitesse " + vitesse);
            boolean switchExecuteurs = false;
            if(sensPositif)
            {
                if(vitesse < 0)
                {
                    sensPositif = false;
                    switchExecuteurs = true;
                }
            }
            else if(vitesse > 0)
            {
                sensPositif = true;
                switchExecuteurs = true;
            }
            if(switchExecuteurs)
            {
                System.out.println("SWITCHING Executeurs");
                Executeur temp = executeur;
                executeur = executeurInnactif;
                executeurInnactif = temp;
                executeur.updateExecuteur(executeurInnactif.getMaillonRef());
            }
        }
    }
    
    
    
    
    
    @Override
    public void dispose()
    {
        
    }
    
    abstract class Executeur
    {
        protected boolean paused = false;
        protected Maillon<Evenement> evenementActuel;
        
        public void updateExecuteur(Maillon<Evenement> evenementRef)
        {
            paused = false;
            evenementActuel = evenementRef;
            System.out.println("Updated executeur" +evenementRef.getValue().getClass().getSimpleName() + " time " + evenementRef.getValue().getTime() + " innerTime "+innerTime
                    +" sensPositif " + isSensPositif()
                    );
        }
        
        public void executerJusque(long newTime)
        {
            //System.out.println("evenementActuel " + evenementActuel.getValue() + " newTime " + newTime + " evTime" + evenementActuel.getValue().getTime());
            while(peutEtreExecute(evenementActuel.getValue(),newTime)&&!paused)
            {
                
                runEvent(evenementActuel.getValue());
                if(getProchainEvent(evenementActuel) == null)
                {
                    paused = true;
                }
                else
                {
                    evenementActuel = getProchainEvent(evenementActuel);
                }
            }
            if(!paused)
            {
                innerTime = newTime;
            }
        }
        
        protected abstract boolean peutEtreExecute(Evenement event,long newTime);
        
        protected abstract Maillon<Evenement> getProchainEvent(Maillon<Evenement> event);
        
        protected abstract Maillon<Evenement> getPrecedentEvent(Maillon<Evenement> event);
        
        public abstract boolean isSensPositif();
        
        public Maillon<Evenement> getMaillonRef()
        {
            if(paused)
            {
                return evenementActuel;
            }
            else return getPrecedentEvent(evenementActuel);
        }
        
        protected void runEvent(Evenement event)
        {
            System.out.println("RUN event" +event.getClass().getSimpleName() + " time " + event.getTime() + " innerTime "+innerTime
                    +" sensPositif " + isSensPositif() + " sensPosVitesse " + !marcheArriereEnCours()
                    );
            innerTime = event.getTime();
            event.visuRun();
        }
    }
}
