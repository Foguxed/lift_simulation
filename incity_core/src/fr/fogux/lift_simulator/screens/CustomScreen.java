package fr.fogux.lift_simulator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.fogux.lift_simulator.Simulateur;

/**
 *
 * @author Detobel36
 */
public abstract class CustomScreen extends ScreenAdapter
{

    protected final Simulateur main;
    protected float proportions;
    private final OrthographicCamera guiCam;
    protected final Viewport viewport;

    public Viewport getViewport()
    {
        return viewport;
    }

    public Simulateur getMain()
    {
        return main;
    }

    public OrthographicCamera getGuiCam()
    {
        return guiCam;
    }

    public float getProportions()
    {
        return proportions;
    }

    protected CustomScreen(final Simulateur main)
    {
        this.main = main;
        guiCam = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, guiCam);
        viewport.apply();
        guiCam.position.set(guiCam.viewportWidth / 2, guiCam.viewportHeight / 2, 0);
        proportions = getScreenWidth() / getScreenHeight();
    }

    protected abstract void update();

    protected void draw()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        main.getBatch().setProjectionMatrix(guiCam.combined);
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void render(float delta)
    {
        update();
        draw();
    }

    @Override
    public void resize(final int width, final int height)
    {
        viewport.update(width, height);
    }

    public float getCenterWidth()
    {
        return getScreenWidth() / 2;
    }

    public float getCenterHeight()
    {
        return getScreenHeight() / 2;
    }

    public float getScreenWidth()
    {
        return guiCam.viewportWidth;
    }

    public float getScreenHeight()
    {
        return guiCam.viewportHeight;
    }

    public Viewport getViewPort()
    {
        return viewport;
    }

    protected Vector3 cameraUnproject(final Vector3 vector)
    {
        return guiCam.unproject(
            vector, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(),
            viewport.getScreenHeight());
    }

    public abstract void init();

}
