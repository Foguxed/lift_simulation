package fr.fogux.lift_simulator;

import com.badlogic.gdx.math.Vector2;

public interface MovingObject
{
    public Vector2 getPosition(long absoluteTime);
}
