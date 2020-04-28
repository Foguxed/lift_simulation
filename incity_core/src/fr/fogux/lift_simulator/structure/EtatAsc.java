package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.physic.EtatAscenseur;

public class EtatAsc
{
    public final EtatAscenseur etat;
    public final int premierEtageAtteignable;

    public EtatAsc(final EtatAscenseur etat, final float positionActuelle, final int premierEtageAtteignable)
    {
        this.etat = etat;
        this.premierEtageAtteignable = premierEtageAtteignable;
    }
}
