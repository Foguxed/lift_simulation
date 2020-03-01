package fr.fogux.lift_simulator.menu;

import com.badlogic.gdx.Input.Keys;

public abstract class FileInputManager extends InputManager
{

    public FileInputManager()
    {
        super();
    }
    
    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Keys.ESCAPE)
        {
            escapePressed();
        }
        return false;
    }
    
    protected abstract void escapePressed();

}
