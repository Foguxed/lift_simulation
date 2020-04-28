package fr.fogux.lift_simulator;

import com.badlogic.gdx.graphics.g2d.Batch;

import fr.fogux.lift_simulator.animation.ImmeubleVisu;
import fr.fogux.lift_simulator.physic.ConfigSimu;

public class AnimationProcess
{
    protected final ImmeubleVisu immeuble;
    protected final GestionnaireDeTachesVisu gestioTaches;
    protected final ConfigSimu c;

    public AnimationProcess(final ConfigSimu c)
    {
        gestioTaches = new GestionnaireDeTachesVisu(this);
        this.c = c;
        immeuble = new ImmeubleVisu(this);
    }

    public GestionnaireDeTachesVisu gestioTaches()
    {
        return gestioTaches;
    }

    public ConfigSimu getConfig()
    {
        return c;
    }

    public long getTime()
    {
        return gestioTaches.innerTime();
    }

    public ImmeubleVisu getImmeubleVisu()
    {
        return immeuble;
    }

    public void draw(final Batch b)
    {
        immeuble.draw(b);
    }

    public void dispose()
    {
        immeuble.dispose();
    }
}
