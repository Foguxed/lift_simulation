package fr.fogux.lift_simulator.animation.objects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class ObjectChain implements Positionable
{
    protected List<ChainableObject> objList = new ArrayList<ChainableObject>();;

    public ObjectChain(List<ChainableObject> objList)
    {
        this.objList = objList;
    }

    public ObjectChain(List<ChainableObject> objList, Vector2 position)
    {
        this(objList);
        this.setPosition(position);
    }

    public void tirer(float xValue)
    {
        // System.out.println("tirer chaine " + xValue);
        int i = objList.size();
        while (xValue != 0 && i > 1)
        {
            i--;
            xValue = xValue - deplacer(i, xValue);
        }
        updatePositionsAPartirDuRang(i);
    }

    public void setPosition(Vector2 vec)
    {
        objList.get(0).getRenderedObject().setPosition(vec);
        updatePositionsAPartirDuRang(1);
    }

    public void addToPosition(Vector2 vec)
    {
        objList.get(0).getRenderedObject().addToPosition(vec);
        updatePositionsAPartirDuRang(1);
    }

    protected void updatePositionsAPartirDuRang(int rang)
    {
        for (int j = rang; j < objList.size(); j++)
        {
            objList.get(j).updateAbsolutePos(objList.get(j - 1));
        }
    }

    public float deplacer(int spriteNb, float deplacement)
    {
        return objList.get(spriteNb).deplacer(objList.get(spriteNb - 1), deplacement);
    }

    @Override
    public void draw(Batch batch)
    {
        for (ChainableObject obj : objList)
        {
            obj.getRenderedObject().draw(batch);
        }
    }

    public void draw(Batch batch, float alphaModulation)
    {
        for (ChainableObject obj : objList)
        {
            obj.getRenderedObject().draw(batch, alphaModulation);
        }
    }

    public void setAlpha(float alpha)
    {
        for (ChainableObject obj : objList)
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
