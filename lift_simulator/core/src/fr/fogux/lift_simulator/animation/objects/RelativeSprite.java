package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class RelativeSprite extends Sprite implements RelativeDrawable
{
    protected Vector2 baseSize;
    public RelativeSprite(Texture texture)
    {
        super(texture);
        baseSize = new Vector2(getWidth(),getHeight());
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void resize(float sizeMultiplicator)
    {
        super.setSize(baseSize.x*sizeMultiplicator, baseSize.y*sizeMultiplicator);
    }
    
    @Override
    public void setSize(float width, float height)
    {
        baseSize = new Vector2(width,height);
        super.setSize(width, height);
    }
    
    
    @Override
    public void resetBaseSize()
    {
        baseSize.set(getWidth(),getHeight());
    }
    
}
