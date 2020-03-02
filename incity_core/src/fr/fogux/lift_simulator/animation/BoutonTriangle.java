package fr.fogux.lift_simulator.animation;

import com.badlogic.gdx.graphics.g2d.Batch;

import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class BoutonTriangle extends RelativeSprite
{
    protected boolean allume;

    public BoutonTriangle(boolean versLeHaut)
    {
        super(AssetsManager.boutonHaut);
        if (!versLeHaut)
        {
            flip(false, true);
        }
    }

    @Override
    public void draw(Batch batch)
    {
        if (allume)
        {
            super.draw(batch);
        }
    }

    @Override
    public void draw(Batch batch, float alphaModulation)
    {
        if (allume)
        {
            super.draw(batch, alphaModulation);
        }
    }

    public void changeState(boolean on)
    {
        allume = on;
    }
}