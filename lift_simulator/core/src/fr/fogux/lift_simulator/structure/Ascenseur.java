package fr.fogux.lift_simulator.structure;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.MovingObject;

public abstract class Ascenseur implements MovingObject
{
    protected final int id;
    public final int persMax;
    
    protected float vi;
    protected float xi;
    protected long ti;
    
    protected float acceleration;
    
    protected ArrayList<Integer> boutonsAllumes = new ArrayList<Integer>();
    
    public Ascenseur(int id, int persMax)
    {
        this.id = id;
        this.persMax = persMax;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void changerEtatBouton(int bouton,boolean allume)
    {
        if(allume)
        {
            boutonsAllumes.add((Integer)bouton);
        }
        else
        {
            boutonsAllumes.removeIf(i -> i == bouton);
        }
    }
    
    public void setDeplacement(long debutDeplacement,float xI,float vI,float gamma)
    {
        this.ti = debutDeplacement;
        this.vi = vI;
        this.xi = xI;
        acceleration = gamma;
        
    }
    
    @Override
    public Vector2 getPosition(long absoluteVal)
    {
        long relT = (absoluteVal-ti);
        return new Vector2(0f,(float)acceleration*(relT*relT)/2 + vi * relT + xi);
    }
}
