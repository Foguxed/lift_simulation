package fr.fogux.lift_simulator.utils;

public class Maillon<obj extends Object>
{
    protected final obj value;
    protected Maillon<obj> suivant;
    protected Maillon<obj> precedent;
    
    
    protected Maillon(Maillon<obj> precedent,obj object,Maillon<obj> suivant)
    {
        this.precedent = precedent;
        this.value = object;
        this.suivant = suivant;
    }
    
    protected Maillon(Maillon<obj> precedent, obj object)
    {
        this(precedent,object,null);
    }
    
    protected Maillon(obj object, Maillon<obj> suivant)
    {
        this(null,object,suivant);
    }
    
    protected Maillon(obj object)
    {
        this(null,object,null);
        System.out.println("object maillon " + object);
    }
    
    public obj getValue()
    {
        return value;
    }
    
    public Maillon<obj> getSuivant()
    {
        return suivant;
    }
    
    public Maillon<obj> getPrecedent()
    {
        return precedent;
    }
}
