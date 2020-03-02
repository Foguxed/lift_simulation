package fr.fogux.lift_simulator.animation;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface PredictedDrawable
{
    public void update(long time);

    public void draw(Batch batch);

    public void dispose();
}
