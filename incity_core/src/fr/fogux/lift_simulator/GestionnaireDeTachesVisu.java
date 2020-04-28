package fr.fogux.lift_simulator;

import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.fichiers.LiseurDeJournal;
import fr.fogux.lift_simulator.utils.ChainedList;
import fr.fogux.lift_simulator.utils.Maillon;

public class GestionnaireDeTachesVisu extends GestionnaireDeTaches
{
    protected AnimationProcess anim;

    protected long vitesse = 16;// en millisecondes par immage
    protected ChainedList<Evenement> evenements = new ChainedList<>();
    protected Executeur executeur;
    protected Executeur executeurInnactif;
    protected boolean sensPositif = true;
    protected long updatedSpeed = 16;
    protected boolean vitesseNeedsUpdate = false;
    // protected Evenement nextNonBufferedEvent;
    /*
     * protected Maillon<Evenement> evenementInf; protected Maillon<Evenement>
     * evenementSup;
     */
    // protected Thread bufferFillerTrhead;
    /*
     * protected long marge; protected boolean buteeNext = false; protected boolean
     * buteePrevious = true;
     */

    public GestionnaireDeTachesVisu(final AnimationProcess anim)
    {
        this.anim = anim;
    }

    @Override
    public void runExecuting()
    {
        fillBuffer();
        executeur = new Executeur()
        {
            @Override
            protected Maillon<Evenement> getProchainEvent(final Maillon<Evenement> event)
            {
                return event.getSuivant();
            }

            @Override
            protected boolean peutEtreExecute(final Evenement event, final long newTime)
            {
                return event.getTime() <= newTime;
            }

            @Override
            public boolean isSensPositif()
            {
                return true;
            }

            @Override
            protected Maillon<Evenement> getPrecedentEvent(final Maillon<Evenement> event)
            {
                return event.getPrecedent();
            }
        };
        executeur.updateExecuteur(evenements.getFirst());
        executeurInnactif = new Executeur()
        {

            @Override
            protected Maillon<Evenement> getProchainEvent(final Maillon<Evenement> event)
            {
                return event.getPrecedent();
            }

            @Override
            protected boolean peutEtreExecute(final Evenement event, final long newTime)
            {
                return event.getTime() >= newTime;
            }

            @Override
            public boolean isSensPositif()
            {
                return false;
            }

            @Override
            protected Maillon<Evenement> getPrecedentEvent(final Maillon<Evenement> event)
            {
                return event.getSuivant();
            }
        };
        System.out.println("evenementActuelUpdate ");
        // runEventActuel();

    }

    @Override
    public boolean marcheArriereEnCours()
    {
        return vitesse < 0;
    }

    public void fillBuffer()
    {
        evenements.addAll(new LiseurDeJournal());
    }

    @Override
    public void update()
    {
        if (vitesse != 0l)
        {
            executeur.executerJusque(innerTime + vitesse);
        }

        if (vitesseNeedsUpdate)
        {
            vitesseNeedsUpdate = false;
            vitesse = updatedSpeed;
            vitesseUpdated();
        }
        // System.out.println("fin update");
        // fillBuffer();
    }

    public void stopper()
    {
        updatedSpeed = 0;
        vitesseNeedsUpdate = true;
    }

    public void modifVitesse(final boolean sensPos)
    {
        if (sensPos)
        {
            updatedSpeed = vitesse + 16;
        } else
        {
            updatedSpeed = vitesse - 16;
        }
        vitesseNeedsUpdate = true;

    }

    protected void vitesseUpdated()
    {
        if (vitesse != 0)
        {
            boolean switchExecuteurs = false;
            if (sensPositif)
            {
                if (vitesse < 0)
                {
                    sensPositif = false;
                    switchExecuteurs = true;
                }
            } else if (vitesse > 0)
            {
                sensPositif = true;
                switchExecuteurs = true;
            }
            if (switchExecuteurs)
            {
                System.out.println("SWITCHING Executeurs");
                final Executeur temp = executeur;
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

        public void updateExecuteur(final Maillon<Evenement> evenementRef)
        {
            paused = false;
            evenementActuel = evenementRef;
        }

        public void executerJusque(final long newTime)
        {
            while (peutEtreExecute(evenementActuel.getValue(), newTime) && !paused)
            {

                runEvent(evenementActuel.getValue());
                if (getProchainEvent(evenementActuel) == null)
                {
                    paused = true;
                } else
                {
                    evenementActuel = getProchainEvent(evenementActuel);
                }
            }
            if (!paused)
            {
                innerTime = newTime;
            }
        }

        protected abstract boolean peutEtreExecute(Evenement event, long newTime);

        protected abstract Maillon<Evenement> getProchainEvent(Maillon<Evenement> event);

        protected abstract Maillon<Evenement> getPrecedentEvent(Maillon<Evenement> event);

        public abstract boolean isSensPositif();

        public Maillon<Evenement> getMaillonRef()
        {
            if (paused)
            {
                return evenementActuel;
            } else
                return getPrecedentEvent(evenementActuel);
        }

        protected void runEvent(final Evenement event)
        {
            System.out.println(
                "RUN event" + event.getClass().getSimpleName() + " time " + event.getTime() + " innerTime " + innerTime
                + " sensPositif " + isSensPositif() + " sensPosVitesse " + !marcheArriereEnCours());
            innerTime = event.getTime();
            event.visuRun(anim);
        }
    }
}
