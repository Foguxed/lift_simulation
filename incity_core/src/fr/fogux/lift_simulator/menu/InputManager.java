package fr.fogux.lift_simulator.menu;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import fr.fogux.lift_simulator.screens.CustomScreen;

public class InputManager implements InputProcessor
{

    protected List<Bouton> list = new ArrayList<Bouton>();
    protected CustomScreen customScreen;

    public InputManager()
    {
    }

    public InputManager(CustomScreen screen)
    {
        setScreen(screen);
    }

    public void setScreen(CustomScreen screen)
    {
        customScreen = screen;
    }

    public void register(Bouton b)
    {
        list.add(b);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        System.out.println("touchDown " + screenX + " " + screenY);

        Vector3 vec = customScreen.getViewport().unproject(new Vector3(screenX, screenY, 0));
        Vector2 vec2 = new Vector2(vec.x, vec.y);
        System.out.println("touchDown2 " + vec2);
        for (Bouton bout : new ArrayList<Bouton>(list))
        {
            bout.click(vec2, button);
        }
        return false;
    }

    public void clearButtons()
    {
        list.clear();
    }

    public void drawButtons(Batch batch)
    {
        for (Bouton b : list)
        {
            b.draw(batch);
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
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
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}
