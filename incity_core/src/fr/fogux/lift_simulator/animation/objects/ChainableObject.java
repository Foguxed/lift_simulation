package fr.fogux.lift_simulator.animation.objects;

public class ChainableObject
{
    protected final RenderedObject object;
    protected final float debatementMin;
    protected final float debatementMax;
    
    protected float xParRapportAuSpritePrecedent = 0;
    
    
    public ChainableObject(RenderedObject obj, float debatementMin, float debatementMax, float xParRapportAuPrecedent)
    {
        this.object = obj;
        this.debatementMin = debatementMin;
        this.debatementMax = debatementMax;
        this.xParRapportAuSpritePrecedent = xParRapportAuPrecedent;
        
    }
    
    public float getDebatementMin()
    {
        return debatementMin;
    }
    
    public float getDebatementMax()
    {
        return debatementMax;
    }
    
    public RenderedObject getRenderedObject()
    {
        return object;
    }
    
    public float deplacer(ChainableObject objectPrecedent, float deplacementX)
    {
        float oldX = xParRapportAuSpritePrecedent;
        xParRapportAuSpritePrecedent = xParRapportAuSpritePrecedent + deplacementX;
        if(xParRapportAuSpritePrecedent<objectPrecedent.getDebatementMin())
        {
            xParRapportAuSpritePrecedent = objectPrecedent.getDebatementMin();
        }
        else if(xParRapportAuSpritePrecedent > objectPrecedent.getDebatementMax())
        {
            xParRapportAuSpritePrecedent = objectPrecedent.getDebatementMax();
        }
        return xParRapportAuSpritePrecedent - oldX;
    }
    
    public void updateAbsolutePos(ChainableObject objectPrecedent)
    {
        object.setPosition(objectPrecedent.getRenderedObject().getPosition().add(xParRapportAuSpritePrecedent,0));
    }
    
}
