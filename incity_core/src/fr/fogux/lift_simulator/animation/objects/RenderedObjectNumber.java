package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.graphics.g2d.Batch;

import fr.fogux.lift_simulator.ReDrawable;

public class RenderedObjectNumber extends RenderedObject implements ReDrawable
{

    public RenderedObjectNumber()
    {
        super();

    }

    @Override
    public void redraw(final Batch batch)
    {
        this.draw(batch, 0.5f);
    }

}
