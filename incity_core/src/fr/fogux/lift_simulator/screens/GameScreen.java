package fr.fogux.lift_simulator.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.GestionnaireDeTachesVisu;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.animation.ImmeubleVisu;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.ChainedList;
import fr.fogux.lift_simulator.utils.Utils;

/**
 *
 * @author Detobel36
 */
public class GameScreen extends CustomScreen {
    
    protected Viewport viewPort;
    protected static final float worldWidth = 1920;
    protected static final float worldHeight = 1080;
    protected OrthographicCamera camera;
    protected ImmeubleVisu immeuble;
    protected BitmapFont athFont;
    protected BitmapFont consoleFont;
    protected BitmapFont errorFont;
    
    protected Matrix4 baseMatrix = null;
    protected boolean firstDraw = true;
    public static long realTimeUpdate;
    
    protected GestionnaireDeTachesVisu gestioTaches;
    protected String displayedError = null;
    protected ChainedList<String> console;
    protected InputCore inputs;
    //protected Sprite delMe = new Sprite(new NumberFont(AssetsManager.fontGenerator.getBitmapFont()).getTexture(0));
    
    public GameScreen(final Simulateur main, GestionnaireDeTachesVisu gestioTaches) {
        
        super(main);
        this.gestioTaches = gestioTaches;
        console = new ChainedList<String>();
        camera = new OrthographicCamera(this.getScreenWidth(),this.getScreenHeight());
        camera.translate(worldWidth/2, 0);
        inputs = new InputCore();
        consoleFont = AssetsManager.fontGenerator.getNewBitmapFont(40);
        athFont = AssetsManager.fontGenerator.getNewBitmapFont(90);
        errorFont = AssetsManager.fontGenerator.getNewBitmapFont(120,Color.RED);
        //delMe.setPosition(40, 40);
    }
    
    public void init()
    {
        Gdx.input.setInputProcessor(inputs);
    }
    
    public OrthographicCamera getCamera()
    {
        return camera;
    }
    
    public void loadVisualisation(int etageMin, int etageMax, int nbAscenseurs)
    {
        this.immeuble = new ImmeubleVisu(etageMin,etageMax,nbAscenseurs);
        Simulateur.setImmeubleVisu(immeuble);
        gestioTaches.runExecuting();
    }
    
    public void ajouterLigneConsole(String str)
    {
        console.addDebut(str);
    }
    
    public void enleverDernierLigneConsole()
    {
        console.removeFirst();
    }
    
    @Override
    protected void update() 
    {
        realTimeUpdate = System.currentTimeMillis();
        GestionnaireDeTaches.getInstance().update();
        
        if(immeuble!= null)
        {
            immeuble.update(GestionnaireDeTaches.getInnerTime());
        }
        //System.out.println("elapsed time for update immeuble" + String.valueOf(System.currentTimeMillis() - timeAfterGestioTaches));
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))
        {
            System.out.println("screen size ");
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        }
        //System.out.println("roll " + Gdx.input.getInputProcessor().scrolled(1));
        if(Gdx.input.isKeyPressed(Input.Keys.P))
        {
            System.out.println("x "  + Gdx.input.getX() + " y " + Gdx.input.getY());
        }
        inputs.update();
        camera.update();
    }
    
    @Override
    protected void draw() {
        super.draw();
      //athFont.draw(main.getBatch(), "test", 20, 20);
        
        
        
        
        if(firstDraw)
        {
            baseMatrix = main.getBatch().getProjectionMatrix().cpy();
            firstDraw = false;
        }
        //baseMatrix.set(main.getBatch().getProjectionMatrix());
        main.getBatch().setProjectionMatrix(camera.combined);
        main.getBatch().begin();
        immeuble.draw(main.getBatch());
        
        main.getBatch().setProjectionMatrix(baseMatrix);
        
        athFont.draw(main.getBatch(), Utils.getTimeString(GestionnaireDeTaches.getInnerTime()),20, 100);
        
        int hauteur = 600;
        Iterator<String> iterator = console.iterator();
        while(iterator.hasNext() && hauteur < 1400)
        {
            consoleFont.draw(main.getBatch(),iterator.next() ,20, hauteur);
            hauteur += 45;
        }
        
        if(displayedError != null)
        {
            errorFont.draw(main.getBatch(), displayedError, 100,800,1800,Align.left,true);
        }
        
        
        main.getBatch().end();
        
        
    }
    
    @Override
    public void dispose() 
    {
        super.dispose();
        immeuble.dispose();
        GestionnaireDeFichiers.dispose();
        GestionnaireDeTaches.getInstance().dispose();
    }
    
    public void setDisplayedError(String text)
    {
        displayedError = text;
    }
    
    class InputCore implements InputProcessor
    {
        protected boolean touched;
        protected int lastInputed;
        protected int timer = 20;
        
        @Override
        public boolean scrolled(int amount) 
        {
            camera.zoom += 0.2*amount;
            return true;
        }



        @Override
        public boolean keyDown(int keycode)
        {
            switch(keycode)
            {
                case Input.Keys.LEFT:
                    gestioTaches.modifVitesse(false);
                    lastInputed = keycode;
                    break;
                case Input.Keys.RIGHT:
                    gestioTaches.modifVitesse(true);
                    lastInputed = keycode;
                    break;
                case Input.Keys.DOWN:
                    gestioTaches.stopper();
                    break;
            }
            
            return false;
        }

        protected void executeLastKey()
        {
            switch(lastInputed)
            {
                case Input.Keys.LEFT:
                    gestioTaches.modifVitesse(false);
                    break;
                case Input.Keys.RIGHT:
                    gestioTaches.modifVitesse(true);
                    break;
            }
        }
        
        public void update()
        {
            if(lastInputed != 0)
            {
                if(timer <= 0)
                {
                    executeLastKey();
                    timer = 2;
                }
                else
                {
                    timer --;
                }
            }
            
        }
        
        @Override
        public boolean keyUp(int keycode)
        {
            if(keycode == lastInputed)
            {
                lastInputed = 0;
                timer=20;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character)
        {
            System.out.println(character);
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button)
        {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button)
        {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer)
        {
            float mult;
            if(Gdx.graphics.getWidth()/Gdx.graphics.getHeight()>proportions)
            {
                mult = getScreenHeight()/Gdx.graphics.getHeight()*camera.zoom;
            }
            else
            {
                mult = getScreenWidth()/Gdx.graphics.getWidth()*camera.zoom;
            }
            camera.translate(-Gdx.input.getDeltaX() *mult , Gdx.input.getDeltaY()*mult);
            return false;
        }



        @Override
        public boolean mouseMoved(int screenX, int screenY)
        {
            // TODO Auto-generated method stub
            return false;
        }
    }
    
    
    
}
