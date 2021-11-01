package fr.fogux.lift_simulator.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.ChainedList;
import fr.fogux.lift_simulator.utils.Utils;

/**
 *
 * @author Detobel36
 */
public class GameScreen extends CustomScreen
{
    protected final AnimationProcess animp;

    protected Viewport viewPort;
    protected static final float worldWidth = 1920;
    protected static final float worldHeight = 1080;
    protected OrthographicCamera camera;
    protected BitmapFont athFont;
    protected BitmapFont consoleFont;
    protected BitmapFont errorFont;

    protected Matrix4 baseMatrix = null;
    protected boolean firstDraw = true;
    public static long realTimeUpdate;

    protected String displayedError = null;
    protected ChainedList<String> console;
    protected InputCore inputs;
    // protected Sprite delMe = new Sprite(new
    // NumberFont(AssetsManager.fontGenerator.getBitmapFont()).getTexture(0));

    public GameScreen(final Simulateur main, final AnimationProcess animp)
    {
        super(main);
        this.animp = animp;
        console = new ChainedList<>();
        camera = new OrthographicCamera(getScreenWidth(), getScreenHeight());
        camera.translate(worldWidth / 2, 0);
        inputs = new InputCore();
        consoleFont = AssetsManager.fontGenerator.getNewBitmapFont(40);
        athFont = AssetsManager.fontGenerator.getNewBitmapFont(90);
        errorFont = AssetsManager.fontGenerator.getNewBitmapFont(120, Color.RED);
        animp.gestioTaches().init();
        // delMe.setPosition(40, 40);
    }

    @Override
    public void init()
    {
        Gdx.input.setInputProcessor(inputs);
    }

    public OrthographicCamera getCamera()
    {
        return camera;
    }

    public void ajouterLigneConsole(final String str)
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
        animp.gestioTaches().update();
        animp.getImmeubleVisu().update(animp.getTime());

        // System.out.println("elapsed time for update immeuble" +
        // String.valueOf(System.currentTimeMillis() - timeAfterGestioTaches));
        if (Gdx.input.isKeyJustPressed(Input.Keys.D))
        {
            System.out.println("screen size ");
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        }
        // System.out.println("roll " + Gdx.input.getInputProcessor().scrolled(1));
        if (Gdx.input.isKeyPressed(Input.Keys.P))
        {
            System.out.println("x " + Gdx.input.getX() + " y " + Gdx.input.getY());
        }
        inputs.update();
        camera.update();
    }

    @Override
    protected void draw()
    {
        super.draw();
        // athFont.draw(main.getBatch(), "test", 20, 20);

        if (firstDraw)
        {
            baseMatrix = main.getBatch().getProjectionMatrix().cpy();
            firstDraw = false;
        }
        // baseMatrix.set(main.getBatch().getProjectionMatrix());
        main.getBatch().setProjectionMatrix(camera.combined);
        main.getBatch().begin();

        animp.draw(main.getBatch());

        main.getBatch().setProjectionMatrix(baseMatrix);

        athFont.draw(main.getBatch(), Utils.getTimeString(animp.getTime()), 20, 100);

        int hauteur = 400;
        final Iterator<String> iterator = console.iterator();

        while (iterator.hasNext() && hauteur < 1400)
        {
            final List<String> strs = divideIfToLong(iterator.next(),85);
            for(final String str : strs)
            {
                consoleFont.draw(main.getBatch(), str, 20, hauteur);
                hauteur += 45;
            }
        }

        if (displayedError != null && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        {
            errorFont.draw(main.getBatch(), displayedError, 100, 800, 1800, Align.left, true);
        }

        main.getBatch().end();

    }

    public List<String> divideIfToLong(final String str, final int caractLimit)
    {
        final List<String> result = new ArrayList<>();
        int currentVal = 0;
        while(currentVal < str.length())
        {
            result.add(str.substring(currentVal, Math.min(currentVal + caractLimit,str.length())));
            currentVal += caractLimit;
        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        animp.dispose();
        animp.gestioTaches().dispose();
    }

    public void setDisplayedError(final String text)
    {
        displayedError = text;
    }

    class InputCore implements InputProcessor
    {
        protected boolean touched;
        protected int lastInputed;
        protected int timer = 20;

        @Override
        public boolean scrolled(final int amount)
        {
            camera.zoom += 0.2 * amount;
            return true;
        }

        @Override
        public boolean keyDown(final int keycode)
        {
            switch (keycode)
            {
                case Input.Keys.LEFT:
                    animp.gestioTaches().modifVitesse(false);
                    lastInputed = keycode;
                    break;
                case Input.Keys.RIGHT:
                    animp.gestioTaches().modifVitesse(true);
                    lastInputed = keycode;
                    break;
                case Input.Keys.DOWN:
                    animp.gestioTaches().stopper();
                    break;
            }

            return false;
        }

        protected void executeLastKey()
        {
            switch (lastInputed)
            {
                case Input.Keys.LEFT:
                    animp.gestioTaches().modifVitesse(false);
                    break;
                case Input.Keys.RIGHT:
                    animp.gestioTaches().modifVitesse(true);
                    break;
            }
        }

        public void update()
        {
            if (lastInputed != 0)
            {
                if (timer <= 0)
                {
                    executeLastKey();
                    timer = 2;
                } else
                {
                    timer--;
                }
            }

        }

        @Override
        public boolean keyUp(final int keycode)
        {
            if (keycode == lastInputed)
            {
                lastInputed = 0;
                timer = 20;
            }
            return false;
        }

        @Override
        public boolean keyTyped(final char character)
        {
            //System.out.println(character);
            return false;
        }

        @Override
        public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button)
        {
            return false;
        }

        @Override
        public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button)
        {
            return false;
        }

        @Override
        public boolean touchDragged(final int screenX, final int screenY, final int pointer)
        {
            float mult;
            if (Gdx.graphics.getWidth() / Gdx.graphics.getHeight() > proportions)
            {
                mult = getScreenHeight() / Gdx.graphics.getHeight() * camera.zoom;
            } else
            {
                mult = getScreenWidth() / Gdx.graphics.getWidth() * camera.zoom;
            }
            camera.translate(-Gdx.input.getDeltaX() * mult, Gdx.input.getDeltaY() * mult);
            return false;
        }

        @Override
        public boolean mouseMoved(final int screenX, final int screenY)
        {
            return false;
        }
    }

}
