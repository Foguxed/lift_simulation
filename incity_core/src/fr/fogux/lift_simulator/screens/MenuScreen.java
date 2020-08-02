package fr.fogux.lift_simulator.screens;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.GestFichiers;
import fr.fogux.lift_simulator.fichiers.NomsFichiers;
import fr.fogux.lift_simulator.menu.Bouton;
import fr.fogux.lift_simulator.menu.FileQuerryAnimation;
import fr.fogux.lift_simulator.menu.FileQuerryBatch;
import fr.fogux.lift_simulator.menu.FileQuerryPartitionGen;
import fr.fogux.lift_simulator.menu.FileQuerryProtocol;
import fr.fogux.lift_simulator.menu.FileQuerrySimulation;
import fr.fogux.lift_simulator.menu.FileQuerryToArray;
import fr.fogux.lift_simulator.menu.InputManager;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class MenuScreen extends CustomScreen
{
    protected BitmapFont bigMenuFont;
    protected InputManager inputs;
    protected boolean simulation;

    public MenuScreen(final Simulateur main)
    {
        super(main);
        bigMenuFont = AssetsManager.fontGenerator.getNewBitmapFont(100, Color.WHITE);
        inputs = new InputManager(this);
        final Pixmap map = new Pixmap(10, 10, Format.RGB565);
        map.setColor(Color.GRAY);
        map.fill();
        final Texture textureBouton = new Texture(map);
        inputs.register(new Bouton("Simulation", textureBouton, bigMenuFont, 800, 80, 300, 400)
        {

            @Override
            public void doAction()
            {
                executeSimulation();
                System.out.println("Simulation");
            }
        });

        inputs.register(new Bouton("newPartition", textureBouton, bigMenuFont, 800, 80, 300, 300)
        {

            @Override
            public void doAction()
            {
                System.out.println("newPartition");
                executeNewPartition();
            }
        });

        inputs.register(new Bouton("Animation ", textureBouton, bigMenuFont, 800, 80, 300, 200)
        {

            @Override
            public void doAction()
            {
                System.out.println("Animation ");
                executeAnimation();
            }
        });

        inputs.register(new Bouton("generateTemplates", textureBouton, bigMenuFont, 800, 80, 300, 100)
        {

            @Override
            public void doAction()
            {
                System.out.println("generateTemplates");
                Simulateur.safeGenerateTemplates();
            }
        });
        
        inputs.register(new Bouton("toArrayProducer ", textureBouton, bigMenuFont, 800, 80, 300, 500)
        {

            @Override
            public void doAction()
            {
                System.out.println("toArray ");
                executeToArray();
            }
        });
        
        inputs.register(new Bouton("batch ", textureBouton, bigMenuFont, 800, 80, 300, 600)
        {

            @Override
            public void doAction()
            {
                System.out.println("Batch ");
                executeNewBatch();
            }
        });
    }

    @Override
    public void init()
    {
        Gdx.input.setInputProcessor(inputs);
    }

    protected void executeSimulation()
    {
        final FileQuerryProtocol protocol = new FileQuerrySimulation();
        final FileResearchScreen newS = new FileResearchScreen(main, new File(GestFichiers.getRootFichier(), NomsFichiers.simulations), this, 2, protocol);
        main.setScreen(newS);
        newS.init();
    }
    
    protected void executeNewBatch()
    {
        final FileQuerryProtocol protocol = new FileQuerryBatch();
        final FileResearchScreen newS = new FileResearchScreen(main, new File(GestFichiers.getRootFichier(), NomsFichiers.batchs), this, 2, protocol);
        main.setScreen(newS);
        newS.init();
    }
    
    protected void executeNewPartition()
    {
        final FileQuerryProtocol protocol = new FileQuerryPartitionGen();
        final FileResearchScreen newS = new FileResearchScreen(main, new File(GestFichiers.getRootFichier(), NomsFichiers.simulations), this, 2, protocol);
        main.setScreen(newS);
        newS.init();
    }

    protected void executeAnimation()
    {
        final FileQuerryProtocol protocol = new FileQuerryAnimation();
        final FileResearchScreen newS = new FileResearchScreen(main, GestFichiers.getRootFichier(), this, 1, protocol);
        main.setScreen(newS);
        newS.init();
    }
    
    protected void executeToArray()
    {
    	final FileQuerryProtocol protocol = new FileQuerryToArray();
        final FileResearchScreen newS = new FileResearchScreen(main, GestFichiers.getRootFichier(), this, 1, protocol);
        main.setScreen(newS);
        newS.init();
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
}
