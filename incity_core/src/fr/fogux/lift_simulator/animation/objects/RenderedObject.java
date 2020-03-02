package fr.fogux.lift_simulator.animation.objects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class RenderedObject extends PositionableObject implements RelativeDrawable
{
    protected List<Vector2> relativeVectors = new ArrayList<Vector2>();
    protected List<Vector2> relativeVectorsBase = new ArrayList<Vector2>();
    protected List<RelativeDrawable> relativeDrawables = new ArrayList<RelativeDrawable>();
    protected float sizeMultiplicator = 1;

    public RenderedObject()
    {
    }

    public RenderedObject(RelativeDrawable drawable, Vector2 relativeToPositionVec)
    {
        addRelativeDrawable(drawable, relativeToPositionVec);
    }

    public RenderedObject(RelativeDrawable drawable, Vector2 relativeToPositionVec, Vector2 position)
    {
        this(drawable, relativeToPositionVec);
        setPosition(position);
    }

    public void addRelativeDrawable(RelativeDrawable drawable, Vector2 relativeToPositionVec)
    {
        relativeDrawables.add(drawable);
        // System.out.println("added vector base " + relativeToPositionVec);
        relativeVectorsBase.add(relativeToPositionVec.cpy());
        relativeVectors.add(
            new Vector2(relativeToPositionVec.x * sizeMultiplicator, relativeToPositionVec.y * sizeMultiplicator));
        drawable.resize(sizeMultiplicator);
        refreshRelPos(drawable, relativeToPositionVec);
    }

    public void changerRelativePosition(RelativeDrawable drawable, Vector2 relativeToPositionVec)
    {
        if (relativeDrawables.contains(drawable))
        {
            int index = relativeDrawables.indexOf(drawable);
            // System.out.println(" changmenet derel position " + relativeToPositionVec);
            relativeVectorsBase.get(index).set(relativeToPositionVec);
            relativeVectors.get(index).set(
                relativeToPositionVec.x * sizeMultiplicator, relativeToPositionVec.y * sizeMultiplicator);
            drawable.resize(sizeMultiplicator);
        } else
        {
            addRelativeDrawable(drawable, relativeToPositionVec);
        }
    }

    public boolean removeRelativeDrawable(RelativeDrawable drawable)
    {
        if (relativeDrawables.contains(drawable))
        {
            int index = relativeDrawables.indexOf(drawable);
            relativeDrawables.remove(index);
            relativeVectorsBase.remove(index);
            relativeVectors.remove(index);
            return true;
        } else
            return false;
    }

    protected void refreshAllRelPos()
    {
        for (int i = 0; i < relativeDrawables.size(); i++)
        {
            refreshRelPos(relativeDrawables.get(i), relativeVectors.get(i));
        }
    }

    protected void refreshRelPos(RelativeDrawable relDrawable, Vector2 attachedVector)
    {
        relDrawable.setPosition(this.position.x + attachedVector.x, this.position.y + attachedVector.y);
    }

    @Override
    public void addToPosition(Vector2 vec)
    {
        super.addToPosition(vec);
        refreshAllRelPos();
    }

    @Override
    public void setPosition(Vector2 vec)
    {
        super.setPosition(vec);
        refreshAllRelPos();
    }

    @Override
    public void setPosition(float x, float y)
    {
        setPosition(new Vector2(x, y));
    }

    @Override
    public void setX(float x)
    {
        super.setX(x);
        refreshAllRelPos();
    }

    @Override
    public void setY(float y)
    {
        super.setY(y);
        refreshAllRelPos();
    }

    public void resize(float sizeMultiplicator)
    {
        innerResize(sizeMultiplicator);
        refreshAllRelPos();
    }

    public void strongResize(float sizeMultiplicator)
    {
        resize(sizeMultiplicator);
        resetBaseSize();
    }

    @Override
    public void resetBaseSize()
    {
        for (int i = 0; i < relativeVectors.size(); i++)
        {
            relativeVectorsBase.get(i).set(relativeVectors.get(i));
        }
        sizeMultiplicator = 1;
        for (RelativeDrawable relD : relativeDrawables)
        {
            relD.resetBaseSize();
        }
    }

    private void innerResize(float sizeMultiplicator)
    {
        this.sizeMultiplicator = sizeMultiplicator;
        for (int i = 0; i < relativeDrawables.size(); i++)
        {
            relativeDrawables.get(i).resize(sizeMultiplicator);
        }
        resizeRefs(sizeMultiplicator);
    }

    private void resizeRefs(float sizeMultiplicator)
    {
        for (int i = 0; i < relativeVectors.size(); i++)
        {
            Vector2 baseVector = relativeVectorsBase.get(i);

            relativeVectors.get(i).set(baseVector.x * sizeMultiplicator, baseVector.y * sizeMultiplicator);
        }
    }

    public float getSizeMult()
    {
        return sizeMultiplicator;
    }

    public void repositionner(float sizeMultiplicator, Vector2 position)
    {
        innerResize(sizeMultiplicator);
        setPosition(position);
    }

    @Override
    public void draw(Batch batch)
    {
        for (RelativeDrawable rdw : relativeDrawables)
        {
            rdw.draw(batch);
        }
    }

    public void draw(Batch batch, float alphaModulation)
    {
        for (RelativeDrawable rdw : relativeDrawables)
        {
            rdw.draw(batch, alphaModulation);
        }
    }

    @Override
    public void setAlpha(float alpha)
    {
        for (RelativeDrawable rdw : relativeDrawables)
        {
            rdw.setAlpha(alpha);
        }
    }

    public List<RelativeDrawable> getDrawables()
    {
        return relativeDrawables;
    }

    @Override
    public void dispose()
    {

    }
}
