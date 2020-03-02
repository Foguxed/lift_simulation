package fr.fogux.lift_simulator.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ByteArray;

import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObjectNumber;

public class NumberFont
{
    public static Texture getTexture(byte b)
    {
        return AssetsManager.chiffres[b];
    }

    public static RenderedObjectNumber getRenderedObject(int value, float maxWidth, Color color)
    {
        RenderedObjectNumber rdo = new RenderedObjectNumber();
        float decalageX = 0;

        ByteArray byteArray = getByteArray(value);
        System.out.println("nb chiffres " + byteArray.size);
        for (int i = byteArray.size - 1; i >= 0; i--)
        {
            System.out.println("nouveau chiffre " + byteArray.get(i));
            RelativeSprite sprite = new RelativeSprite(getTexture(byteArray.get(i)));
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
        ByteArray b = new ByteArray();
        Boolean doAddMinus = value < 0;
        if (doAddMinus)
        {
            value = -value;
        }
        System.out.println("NumberFont value " + value + " length " + b.size);
        do
        {
            b.add((byte) (value % 10));
            value = value / 10;
            System.out.println("NumberFont int " + value);
        } while (value > 0);
        System.out.println("NumberFont int " + b);
        if (doAddMinus)
        {
            b.add((byte) 10);
        }
        return b;
    }
}
