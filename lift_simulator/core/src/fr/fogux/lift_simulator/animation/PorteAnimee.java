package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.animation.objects.ChainableObject;
import fr.fogux.lift_simulator.animation.objects.ObjectChain;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class PorteAnimee implements PredictedDrawable,ReferencePosUsers
{
    protected final ObjectChain porteGauche;
    protected final ObjectChain porteDroite;
    private static final float ecart = 400f;
    private static final float debatement = 80f;
    private static final float deplacement = ecart/2;
    protected Animation animation;
    protected boolean ouverture = false;
    protected final Vector2 position;
    protected float oldFermeture = 0f;
    
    public PorteAnimee(Vector2 position)
    {
        porteGauche = genererPorte(true,new Vector2(-ecart/2,0).add(position));
        porteDroite = genererPorte(false,new Vector2(ecart/2,0).add(position));
        porteGauche.setAlpha(0.7f);
        porteDroite.setAlpha(0.7f);
        fermer(1f);
        this.position = position;
    }
    
    protected ObjectChain genererPorte(boolean sensPositif, Vector2 centrePremierPanneau)
    {
        final List<ChainableObject> panneauxDePorte = new ArrayList<ChainableObject>();
        float debatementMin;
        float debatementMax;
        if(sensPositif)
        {
            debatementMin = 0;
            debatementMax = debatement;
        }
        else
        {
            debatementMin = -debatement;
            debatementMax = 0;
        }
        
        for(int i = 0; i < 3 ; i ++)
        {
            final RelativeSprite objSprite = new RelativeSprite(AssetsManager.panneauPorte);
            panneauxDePorte.add
            (
                new ChainableObject
                (
                    new RenderedObject
                    (
                        objSprite,
                        new Vector2(-objSprite.getWidth()/2,0)
                    )
                    , debatementMin, debatementMax,0
                )
            );
        }
        return new ObjectChain(panneauxDePorte,centrePremierPanneau);
    }
    
    public void fermer(float newFermeture)
    {
        float relProportion = newFermeture - oldFermeture;
        porteGauche.tirer(deplacement*relProportion);
        porteDroite.tirer(-deplacement*relProportion);
        oldFermeture = newFermeture;
    }
    
    @Override
    public void update(long time)
    {
        if(animation != null)
        {
            float proportion = animation.avancement(time);
            if(ouverture)
            {
                fermer(1-proportion);
            }
            else
            {
                fermer(proportion);
            }
        }
    }
    
    @Override
    public void draw(Batch batch)
    {
        porteGauche.draw(batch);
        porteDroite.draw(batch);
    }

    public void animation(long time, long duree,boolean ouverture)
    {
        //System.out.println("portes animation " + ouverture + " " + duree);
        this.animation = new Animation(time,duree)
                {
                    @Override
                    public void depassementNegatif()
                    {
                        animation = null;
                        System.out.println("animation portes dep NEG");
                    }

                    @Override
                    public void depassementPositif()
                    {
                        animation = null;
                        System.out.println("animation portes dep POS");
                    }
                };
        this.ouverture = ouverture;
    }
    
    @Override
    public void dispose()
    {
        porteGauche.dispose();
        porteDroite.dispose();
    }

    @Override
    public Vector2 getPosRef()
    {
        return position.cpy().add(0,30);
    }   
}
