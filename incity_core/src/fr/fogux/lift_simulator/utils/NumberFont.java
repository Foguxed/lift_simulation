package fr.fogux.lift_simulator.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ByteArray;

import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObjectNumber;

public class NumberFont
{
    public static Texture getTexture(final byte b)
    {
        return AssetsManager.chiffres[b];
    }

    public static RenderedObjectNumber getRenderedObject(final int value, final float maxWidth, final Color color)
    {
        final RenderedObjectNumber rdo = new RenderedObjectNumber();
        float decalageX = 0;

        final ByteArray byteArray = getByteArray(value);
        for (int i = byteArray.size - 1; i >= 0; i--)
        {
            final RelativeSprite sprite = new RelativeSprite(getTexture(byteArray.get(i)));
            sprite.setColor(color);
            rdo.addRelativeDrawable(sprite, new Vector2(decalageX, 0));
            decalageX += sprite.getWidth() + 30;
        }
        decalageX -= 30;
        if (decalageX > maxWidth)
        {
            rdo.strongResize(maxWidth / decalageX);
        }
        return rdo;
    }

    protected static ByteArray getByteArray(int value)
    {
        final ByteArray b = new ByteArray();
        final Boolean doAddMinus = value < 0;
        if (doAddMinus)
        {
            value = -value;
        }
        do
        {
            b.add((byte) (value % 10));
            value = value / 10;
        } while (value > 0);
        if (doAddMinus)
        {
            b.add((byte) 10);
        }
        return b;
    }
}
