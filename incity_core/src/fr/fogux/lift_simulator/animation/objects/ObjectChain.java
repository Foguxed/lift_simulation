package fr.fogux.lift_simulator.animation.objects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class ObjectChain implements Positionable
{
    protected List<ChainableObject> objList = new ArrayList<>();

    public ObjectChain(final List<ChainableObject> objList)
    {
        this.objList = objList;
    }

    public ObjectChain(final List<ChainableObject> objList, final Vector2 position)
    {
        this(objList);
        setPosition(position);
    }

    public void tirer(float xValue)
    {
        int i = objList.size();
        while (xValue != 0 && i > 1)
        {
            i--;
            xValue = xValue - deplacer(i, xValue);
        }
        updatePositionsAPartirDuRang(i);
    }

    @Override
    public void setPosition(final Vector2 vec)
    {
        objList.get(0).getRenderedObject().setPosition(vec);
        updatePositionsAPartirDuRang(1);
    }

    @Override
    public void addToPosition(final Vector2 vec)
    {
        objList.get(0).getRenderedObject().addToPosition(vec);
        updatePositionsAPartirDuRang(1);
    }

    protected void updatePositionsAPartirDuRang(final int rang)
    {
        for (int j = rang; j < objList.size(); j++)
        {
            objList.get(j).updateAbsolutePos(objList.get(j - 1));
        }
    }

    public float deplacer(final int spriteNb, final float deplacement)
    {
        return objList.get(spriteNb).deplacer(objList.get(spriteNb - 1), deplacement);
    }

    @Override
    public void draw(final Batch batch)
    {
        for (final ChainableObject obj : objList)
        {
            obj.getRenderedObject().draw(batch);
        }
    }

    public void draw(final Batch batch, final float alphaModulation)
    {
        for (final ChainableObject obj : objList)
        {
            obj.getRenderedObject().draw(batch, alphaModulation);
        }
    }

    public void setAlpha(final float alpha)
    {
        for (final ChainableObject obj : objList)
        {
            obj.getRenderedObject().setAlpha(alpha);
        }
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Vector2 getPosition()
    {
        return objList.get(0).getRenderedObject().getPosition();
    }

}
