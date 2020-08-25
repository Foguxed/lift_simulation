package fr.fogux.lift_simulator.mind.independant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.trajets.AlgoAscenseur;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class AlgoIndependentAsc implements AlgoAscenseur,VoisinAsc
{

    protected VoisinAsc ascenseurSuperieur;
    protected final VoisinAsc ascenseurInferieur;
    protected final AscId id;

    protected final ConfigSimu config;
    protected final OutputProvider outputProvider;

    protected boolean busy;
    protected int objectifActuel;

    protected boolean savedbusy;
    protected int savedObjectifActuel;

    protected static int debugMonteeSurvey = 0;

    public AlgoIndependentAsc(final AscId id,final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        ascenseurInferieur = ascPrecedent;
        this.config = config;
        this.outputProvider = outputProvider;
        this.id = id;
        busy = false;
    }

    public void saveState()
    {
        savedbusy = busy;
        savedObjectifActuel = objectifActuel;
    }

    public void rallBack()
    {
        busy = savedbusy;
        objectifActuel = savedObjectifActuel;
    }

    @Override
    public void setAscenseurSuperieur(final VoisinAsc asc)
    {
        ascenseurSuperieur = asc;
    }

    @Override
    public void init()
    {
        reflechir();
    }

    @Override
    public void escaleTerminee()
    {
        reflechir();
    }

    protected static List<Integer> toIds(final List<AlgoPersonne> l)
    {
        final List<Integer> retour = new ArrayList<>();
        for(final AlgoPersonne p : l)
        {
            retour.add(p.id);
        }
        return retour;
    }

    public InterfacePhysique phys()
    {
        return outputProvider.out();
    }

    protected void reflechir()
    {
        //System.out.println(id + " reflechit ");
        Integer prochainArret = null;
        //System.out.println("inferieur limit sup " + ascenseurInferieur.getLimitSup());
        final Predicate<Integer> predicate = (i -> (i < ascenseurSuperieur.getLimitInf() && i > ascenseurInferieur.getLimitSup()));
        prochainArret = prochainArret(predicate);

        if(prochainArret != null)
        {
            phys().println(id + " etage choisi " + prochainArret);
            if(!predicate.test(prochainArret))
            {
                throw new SimulateurAcceptableException(id + " mauvaise destination " + prochainArret);
            }
        }
        if(prochainArret == null)
        {
            if(busy)
            {
                if(id.monteeId == debugMonteeSurvey)
                {
                    phys().println("devient afk vers " + objectifActuel);
                }
                busy = false;
                updateVoisins();
            }
            else
            {

                if(objectifActuel < ascenseurInferieur.getLimitSup() + 1)
                {
                    objectifActuel = ascenseurInferieur.getLimitSup() + 1;
                    if(id.monteeId == debugMonteeSurvey)
                    {
                        //System.out.println(id + " afk vers " + objectifActuel);
                    }
                    phys().changerDestination(id, objectifActuel, false);
                }
                else if(objectifActuel > ascenseurSuperieur.getLimitInf() - 1)
                {
                    objectifActuel = ascenseurSuperieur.getLimitInf() - 1;
                    if(id.monteeId == debugMonteeSurvey)
                    {
                        //System.out.println(id + " afk vers " + objectifActuel);
                    }
                    phys().changerDestination(id, objectifActuel, false);
                }
                else
                {
                    if(id.monteeId == debugMonteeSurvey)
                    {
                        //System.out.println(id + " reste afk en " + objectifActuel );
                    }
                }
                //phys().changerDestination(id, ascenseurInferieur.getLimitSup() + 1, false);
            }
        }
        else
        {
            if(id.monteeId == debugMonteeSurvey)
            {
                phys().println("trouve escale " + this + " dest " + prochainArret);
            }
            busy = true;
            objectifActuel = prochainArret;
            phys().changerDestination(id, prochainArret, true);
            updateVoisins();
        }
        //System.out.println(id + " fin de reflection ");
    }

    protected void updateVoisins()
    {
        ascenseurSuperieur.updateLimitVoisin(false);
        ascenseurInferieur.updateLimitVoisin(true);
    }

    public abstract Integer prochainArret(Predicate<Integer> aFiltrer);


    @Override
    public int getLimitSup()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurInferieur.getLimitSup() + 1;
        }
    }

    @Override
    public int getLimitInf()
    {
        if(busy)
        {
            return objectifActuel;
        }
        else
        {
            return ascenseurSuperieur.getLimitInf() - 1;
        }
    }

    @Override
    public void updateLimitVoisin(final boolean isSup)
    {
        if(!busy)
        {
            reflechir();
            if(!busy)// il ne s'est donc rien passé
            {
                if(isSup)
                {
                    ascenseurInferieur.updateLimitVoisin(true);
                }
                else
                {
                    ascenseurSuperieur.updateLimitVoisin(false);
                }
            }
        }
    }

    @Override
    public int getAtteignableSup()
    {
        return ascenseurSuperieur.getAtteignableSup() - 1;
    }
    @Override
    public int getAtteignableInf()
    {
        return ascenseurInferieur.getAtteignableInf() + 1;
    }

    public boolean atteignable(final int j) // supose ecart < 1 etage entre les acss
    {
        return (j >= getAtteignableInf() && j <= getAtteignableSup());
    }

    public boolean atteignable(final AlgoPersonne p)
    {
        return atteignable(p.depart) && atteignable(p.destination);
    }

    @Override
    public String toString()
    {
        return id + " d:";
    }
}
