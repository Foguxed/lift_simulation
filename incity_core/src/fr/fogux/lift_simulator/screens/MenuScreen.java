package fr.fogux.lift_simulator.screens;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;
import fr.fogux.lift_simulator.menu.Bouton;
import fr.fogux.lift_simulator.menu.FileSearcher;
import fr.fogux.lift_simulator.menu.InputManager;
import fr.fogux.lift_simulator.partition_creation.PartitionCreator;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class MenuScreen extends CustomScreen implements FileSearcher
{
    protected BitmapFont bigMenuFont;
    protected InputManager inputs;
    protected boolean simulation;

    public MenuScreen(Simulateur main)
    {
        super(main);
        bigMenuFont = AssetsManager.fontGenerator.getNewBitmapFont(100, Color.WHITE);
        inputs = new InputManager(this);
        Pixmap map = new Pixmap(10, 10, Format.RGB565);
        map.setColor(Color.GRAY);
        map.fill();
        Texture textureBouton = new Texture(map);
        inputs.register(new Bouton("Simulation", textureBouton, bigMenuFont, 800, 80, 300, 800)
        {

            @Override
            public void doAction()
            {
                System.out.println("Simulation");
                initFileResearch(true);
            }
        });
        inputs.register(new Bouton("Animation", textureBouton, bigMenuFont, 800, 80, 300, 400)
        {

            @Override
            public void doAction()
            {
                System.out.println("Animation");
                initFileResearch(false);
            }
        });

        inputs.register(new Bouton("newPartition", textureBouton, bigMenuFont, 800, 80, 300, 200)
        {

            @Override
            public void doAction()
            {
                System.out.println("newPartition");
                new PartitionCreator();
            }
        });
    }

    public void init()
    {
        Gdx.input.setInputProcessor(inputs);
    }

    protected void initFileResearch(boolean simulation)
    {
        this.simulation = simulation;
        FileResearchScreen scr
            = new FileResearchScreen(main, GestionnaireDeFichiers.getBasePath(), this, simulation ? 0 : 1, this);
        main.setScreen(scr);
        scr.init();
    }

    @Override
    protected void update()
    {
    }

    @Override
    protected void draw()
    {
        main.getBatch().begin();
        super.draw();
        inputs.drawButtons(main.getBatch());
        main.getBatch().end();
    }

    @Override
    public void fichierChoisi(File f)
    {
        /*
         * main.setScreen(this); init();
         */
        if (simulation)
        {
            Simulateur.getSimulateur().startSimulation(f, Gdx.input.isKeyPressed(Keys.CONTROL_LEFT));
        } else
        {
            Simulateur.getSimulateur().startVisualisation(f);
        }

        System.out.println("simulation " + simulation + " file " + f.getAbsolutePath());
    }
}
