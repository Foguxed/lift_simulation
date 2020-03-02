package fr.fogux.lift_simulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.evenements.PrintableEvenement;
import fr.fogux.lift_simulator.screens.CustomScreen;

public class Utils
{
    public static long timeInMilis(String strFormatJHMinSecMil)// format J:H:Min:Sec:Milis
    {
        Integer[] ints = new Integer[5];
        String[] strs = strFormatJHMinSecMil.split(":");
        for (int i = 0; i < strs.length; i++)
        {
            // System.out.println("val " + i +" " + strs[i]);
            ints[i] = Integer.parseInt(strs[i]);
        }
        return ((((ints[0] * 24) + ints[1]) * 60 + ints[2]) * 60 + ints[3]) * 1000 + ints[4];
    }

    public static String getTimeString(long timeInMilis)
    {
        final long nbSecondes = (long) (timeInMilis / 1000);
        final long nbMinutes = (long) (nbSecondes / 60);
        final long nbHeures = (long) (nbMinutes / 60);
        final long nbJours = (long) (nbHeures / 24);
        return nbJours + ":" + nbHeures % 24 + ":" + nbMinutes % 60 + ":" + nbSecondes % 60 + ":" + timeInMilis % 1000;
    }

    public static Vector2 fromMiddleToSpritePos(Sprite sprite)
    {
        return new Vector2(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
    }

    public static void printCreationOf(PrintableEvenement event)
    {
        event.print();
    }

    public static void msg(Object o, String msg)
    {
        System.out.println(o.getClass().getSimpleName() + ": " + msg);
    }

    public static void msg(Class<?> c, String msg)
    {
        System.out.println(c.getSimpleName() + ": " + msg);
    }

    public static Vector2 getWorldCoordinates(float pointerX, float pointerY, OrthographicCamera camera,
        CustomScreen screen)
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
