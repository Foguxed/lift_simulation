package fr.fogux.lift_simulator.animation;

public abstract class Animation extends MarqueurTemporel
{
    protected final long duree;
    public Animation(long debut, long duree)
    {
        super(debut);
        this.duree = duree;
    }
    
    public float avancement(long timeActuel)
    {
        float f = intervale(timeActuel)/(float)duree;
        //System.out.println("Animation av " + f +"intervale" + intervale(timeActuel) +" time Anim " + time);
        if(f < 0)
        {
            f=0;
            depassementNegatif();
            
        }
        else if(f>1)
        {
            f=1;
            depassementPositif();
        }
        return f;
        
    }
    
    public float avancementUnlimit(long timeActuel)
    {
        return intervale(timeActuel)/(float)duree;
    }
    
    public abstract void depassementNegatif();
    
    public abstract void depassementPositif();
    /*public boolean terminee(long timeActuel)
    {
        return timeActuel>time+duree;
    }*/
}
