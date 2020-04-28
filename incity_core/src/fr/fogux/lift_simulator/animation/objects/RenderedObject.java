package fr.fogux.lift_simulator.animation.objects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class RenderedObject extends PositionableObject implements RelativeDrawable
{
    protected List<Vector2> relativeVectors = new ArrayList<>();
    protected List<Vector2> relativeVectorsBase = new ArrayList<>();
    protected List<RelativeDrawable> relativeDrawables = new ArrayList<>();
    protected float sizeMultiplicator = 1;

    public RenderedObject()
    {
    }

    public RenderedObject(final RelativeDrawable drawable, final Vector2 relativeToPositionVec)
    {
        addRelativeDrawable(drawable, relativeToPositionVec);
    }

    public RenderedObject(final RelativeDrawable drawable, final Vector2 relativeToPositionVec, final Vector2 position)
    {
        this(drawable, relativeToPositionVec);
        setPosition(position);
    }

    public void addRelativeDrawable(final RelativeDrawable drawable, final Vector2 relativeToPositionVec)
    {
        relativeDrawables.add(drawable);
        relativeVectorsBase.add(relativeToPositionVec.cpy());
        relativeVectors.add(
            new Vector2(relativeToPositionVec.x * sizeMultiplicator, relativeToPositionVec.y * sizeMultiplicator));
        drawable.resize(sizeMultiplicator);
        refreshRelPos(drawable, relativeToPositionVec);
    }

    public void changerRelativePosition(final RelativeDrawable drawable, final Vector2 relativeToPositionVec)
    {
        if (relativeDrawables.contains(drawable))
        {
            final int index = relativeDrawables.indexOf(drawable);
            relativeVectorsBase.get(index).set(relativeToPositionVec);
            relativeVectors.get(index).set(
                relativeToPositionVec.x * sizeMultiplicator, relativeToPositionVec.y * sizeMultiplicator);
            drawable.resize(sizeMultiplicator);
        } else
        {
            addRelativeDrawable(drawable, relativeToPositionVec);
        }
    }

    public boolean removeRelativeDrawable(final RelativeDrawable drawable)
    {
        if (relativeDrawables.contains(drawable))
        {
            final int index = relativeDrawables.indexOf(drawable);
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

    protected void refreshRelPos(final RelativeDrawable relDrawable, final Vector2 attachedVector)
    {
        relDrawable.setPosition(position.x + attachedVector.x, position.y + attachedVector.y);
    }

    @Override
    public void addToPosition(final Vector2 vec)
    {
        super.addToPosition(vec);
        refreshAllRelPos();
    }

    @Override
    public void setPosition(final Vector2 vec)
    {
        super.setPosition(vec);
        refreshAllRelPos();
    }

    @Override
    public void setPosition(final float x, final float y)
    {
        setPosition(new Vector2(x, y));
    }

    @Override
    public void setX(final float x)
    {
        super.setX(x);
        refreshAllRelPos();
    }

    @Override
    public void setY(final float y)
    {
        super.setY(y);
        refreshAllRelPos();
    }

    @Override
    public void resize(final float sizeMultiplicator)
    {
        innerResize(sizeMultiplicator);
        refreshAllRelPos();
    }

    public void strongResize(final float sizeMultiplicator)
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
        for (final RelativeDrawable relD : relativeDrawables)
        {
            relD.resetBaseSize();
        }
    }

    private void innerResize(final float sizeMultiplicator)
    {
        this.sizeMultiplicator = sizeMultiplicator;
        for (int i = 0; i < relativeDrawables.size(); i++)
        {
            relativeDrawables.get(i).resize(sizeMultiplicator);
        }
        resizeRefs(sizeMultiplicator);
    }

    private void resizeRefs(final float sizeMultiplicator)
    {
        for (int i = 0; i < relativeVectors.size(); i++)
        {
            final Vector2 baseVector = relativeVectorsBase.get(i);

            relativeVectors.get(i).set(baseVector.x * sizeMultiplicator, baseVector.y * sizeMultiplicator);
        }
    }

    public float getSizeMult()
    {
        return sizeMultiplicator;
    }

    public void repositionner(final float sizeMultiplicator, final Vector2 position)
    {
        innerResize(sizeMultiplicator);
        setPosition(position);
    }

    @Override
    public void draw(final Batch batch)
    {
        for (final RelativeDrawable rdw : relativeDrawables)
        {
            rdw.draw(batch);
        }
    }

    @Override
    public void draw(final Batch batch, final float alphaModulation)
    {
        for (final RelativeDrawable rdw : relativeDrawables)
        {
            rdw.draw(batch, alphaModulation);
        }
    }

    @Override
    public void setAlpha(final float alpha)
    {
        for (final RelativeDrawable rdw : relativeDrawables)
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
