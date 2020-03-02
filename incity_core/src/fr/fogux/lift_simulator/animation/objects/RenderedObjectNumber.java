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
    public void redraw(Batch batch)
    {
        this.draw(batch, 0.5f);
        // System.out.println(" number " + this.getPosition() + " size
        // renderedobjectnumber " + this.relativeDrawables.size());
    }

}
