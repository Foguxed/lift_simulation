package fr.fogux.lift_simulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.screens.CustomScreen;

public class Utils
{
    public static long timeInMilis(final String strFormatJHMinSecMil)// format J:H:Min:Sec:Milis
    {
        final Integer[] ints = new Integer[5];
        final String[] strs = strFormatJHMinSecMil.split(":");
        for (int i = 0; i < strs.length; i++)
        {
            // System.out.println("val " + i +" " + strs[i]);
            ints[i] = Integer.parseInt(strs[i]);
        }
        return ((((ints[0] * 24) + ints[1]) * 60 + ints[2]) * 60 + ints[3]) * 1000 + ints[4];
    }

    public static String getTimeString(final long timeInMilis)
    {
        final long nbSecondes = timeInMilis / 1000;
        final long nbMinutes = nbSecondes / 60;
        final long nbHeures = nbMinutes / 60;
        final long nbJours = nbHeures / 24;
        return nbJours + ":" + nbHeures % 24 + ":" + nbMinutes % 60 + ":" + nbSecondes % 60 + ":" + timeInMilis % 1000;
    }

    public static Integer safeParseInt(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException e)
        {
            return null;
        }
    }

    public static Vector2 fromMiddleToSpritePos(final Sprite sprite)
    {
        return new Vector2(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
    }

    public static void msg(final Object o, final String msg)
    {
        System.out.println(o.getClass().getSimpleName() + ": " + msg);
    }

    public static void msg(final Class<?> c, final String msg)
    {
        System.out.println(c.getSimpleName() + ": " + msg);
    }

    public static Vector2 getWorldCoordinates(final float pointerX, final float pointerY, final OrthographicCamera camera,
        final CustomScreen screen)
    {
        float mult;
        if (Gdx.graphics.getWidth() / Gdx.graphics.getHeight() > screen.getProportions())
        {
            mult = screen.getScreenHeight() / Gdx.graphics.getHeight() * camera.zoom;
        } else
        {
            mult = screen.getScreenWidth() / Gdx.graphics.getWidth() * camera.zoom;
        }
        return new Vector2(pointerX * mult, pointerY * mult);
    }
}
