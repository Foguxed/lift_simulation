package fr.fogux.lift_simulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Chargement de tous les assets<br/>
 * Chargé au début de main
 * 
 * @author Detobel36
 */
public class AssetsManager
{

    public static Texture background;
    public static Texture panneauPorte;
    public static Texture etage;
    public static Texture ascenseur;
    public static Texture personne;
    public static Texture boutonHaut;
    public static Texture fondBouton;
    public static Texture boutonMenu;
    public static Texture[] chiffres = new Texture[11];

    public static FontGenerator fontGenerator;
    public static BitmapFont fichierFont;

    public static void loadAnimationAssets()
    {
        panneauPorte = loadTexture("porte_recad.png");
        etage = loadTexture("etage_recadre.png");
        ascenseur = loadTexture("ascenseur_recad.png");
        personne = loadTexture("StickMan4.png");
        boutonHaut = loadTexture("triangle2.png");
        fondBouton = loadTexture("fond_boutons.png");

        for (int i = 0; i < 11; i++)
        {
            chiffres[i] = loadTexture(i + ".png");
        }
        loadCommunAssets();

    }

    public static void loadMainMenuAssets()
    {
        loadCommunAssets();
        fichierFont = AssetsManager.fontGenerator.getNewBitmapFont(40, Color.WHITE);
    }

    protected static void loadCommunAssets()
    {
        fontGenerator = new FontGenerator();

    }

    public static Texture loadTexture(String file)
    {
        return new Texture(Gdx.files.internal(file));
    }

    protected static Sound loadSound(final String soundName)
    {
        final Sound sound = Gdx.audio.newSound(Gdx.files.internal(soundName));
        // allSound.add(sound);
        return sound;
    }

}
