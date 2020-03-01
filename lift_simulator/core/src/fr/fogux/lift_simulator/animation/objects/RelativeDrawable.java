package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface RelativeDrawable extends IDrawable
{
    public void resize(float sizeMultiplicator);
    public void resetBaseSize();
    public void setPosition(float x, float y);
    public void setAlpha(float alpha);
    public void draw(Batch batch,float alphaModulation);
}
