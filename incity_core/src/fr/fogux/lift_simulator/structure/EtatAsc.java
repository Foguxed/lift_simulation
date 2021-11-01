package fr.fogux.lift_simulator.structure;

import java.util.function.Predicate;

import fr.fogux.lift_simulator.physic.EtatAscenseur;

public class EtatAsc
{
    public final EtatAscenseur etat;
    public final int premierEtageAtteignable;
    public final float positionActuelle;

    public EtatAsc(final EtatAscenseur etat, final float positionActuelle, final int premierEtageAtteignable)
    {
        this.etat = etat;
        this.premierEtageAtteignable = premierEtageAtteignable;
        this.positionActuelle = positionActuelle;
    }

    public Predicate<Integer> filtreAntiDemiTour()
    {
        if(etat == EtatAscenseur.ARRET | etat == EtatAscenseur.BLOQUE)
        {
            return (i -> true);
        }
        else
        {
            if(etat == EtatAscenseur.MONTEE)
            {
                return (i -> i >= premierEtageAtteignable);
            }
            else
            {
                return (i -> i <= premierEtageAtteignable);
            }
        }
    }

    public int etageAtteignablePlusProche()
    {
        if(etat == EtatAscenseur.BLOQUE || etat == EtatAscenseur.ARRET)
        {
            return (int) positionActuelle;
        }
        else
        {
            return premierEtageAtteignable;
        }
    }

    @Override
    public String toString()
    {
        return etat + " premierEtageAtteignable " + premierEtageAtteignable + " posActuelle " + positionActuelle;
    }
}
