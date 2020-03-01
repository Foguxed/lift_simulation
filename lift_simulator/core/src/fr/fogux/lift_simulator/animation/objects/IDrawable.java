package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 *
 * @author Detobel36
 */
public interface IDrawable 
{
    void draw(Batch batch);
    void dispose();
}
