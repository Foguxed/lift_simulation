package fr.fogux.lift_simulator.animation.objects;

import com.badlogic.gdx.math.Vector2;

public abstract class PositionableObject implements Positionable
{
    protected final Vector2 position = new Vector2(0,0);
    
    public PositionableObject()
    {
    }
    
    public Vector2 getPosition()
    {
        return position.cpy();
    }
    
    public void addToPosition(Vector2 vec)
    {
        position.add(vec);
    }
    
    public void setPosition(Vector2 vec)
    {
        position.set(vec);
    }
    
    public float getX()
    {
        return position.x;
    }
    
    public float getY()
    {
        return position.y;
    }
    
    public void setX(float x)
    {
        position.x = x;
    }
    
    public void setY(float y)
    {
        position.y = y;
    }

}
