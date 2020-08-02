package fr.fogux.lift_simulator.structure;

import java.util.ArrayList;

import fr.fogux.lift_simulator.physic.ConfigSimu;

public abstract class Ascenseur
{
    protected final AscId id;
    public final int persMax;

    protected final DepPlannifier plannifier;
    protected ArrayList<Integer> boutonsAllumes = new ArrayList<>();

    /**
     * ce sont les updates de xObjectif (l'objectif réel) qui sont enregistrées, car la fonction de déplacement peut en être
     * déduite
     */




    public Ascenseur(final ConfigSimu c,final AscId id, final int persMax, final float initialHeight)
    {
        this.id = id;
        this.persMax = persMax;
        this.plannifier = new DepPlannifier(c, new AscState(0, initialHeight, 0f));
    }

    public Ascenseur(Ascenseur shadowed)
    {
    	this.id = shadowed.id;
    	this.persMax = shadowed.persMax;
    	this.boutonsAllumes = new ArrayList<>(shadowed.boutonsAllumes);
    	this.plannifier = new DepPlannifier(shadowed.plannifier);
    }
    
    public AscId getId()
    {
        return id;
    }

    public void changerEtatBouton(final int bouton, final boolean allume)
    {
        if (allume)
        {
            boutonsAllumes.add(bouton);
        } else
        {
            boutonsAllumes.removeIf(i -> i == bouton);
        }
    }

    @Override
    public String toString()
    {
        return " Ascenseur : " + id + " ";
    }
}
