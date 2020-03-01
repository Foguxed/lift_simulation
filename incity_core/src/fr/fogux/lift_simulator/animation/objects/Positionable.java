package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.math.Vector2;

public interface Positionable extends IDrawable
{
    public abstract void addToPosition(Vector2 vec);
    public abstract void setPosition(Vector2 vec);
    public abstract Vector2 getPosition();
}
