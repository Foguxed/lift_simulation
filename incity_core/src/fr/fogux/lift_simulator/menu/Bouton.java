package fr.fogux.lift_simulator.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Bouton extends Sprite
{
    protected BitmapFont font;
    protected Vector2 textPos;
    protected String text;

    public Bouton(String text, Texture texture, BitmapFont font)
    {
        super(texture);
        this.text = text;
        this.font = font;
    }

    public Bouton(String text, Texture texture, BitmapFont font, float width, float height, float x, float y)
    {
        super(texture);
        this.text = text;
        this.font = font;
        setSize(width, height);
        textPos = new Vector2(x + 5, y + height - 5);
        setPosition(x, y);
    }

    public void click(Vector2 vec, int buttonValue)
    {
        if (this.getBoundingRectangle().contains(vec))
        {
            doAction();
        }
    }

    @Override
    public void draw(Batch batch)
    {
        super.draw(batch);
        font.draw(batch, text, textPos.x, textPos.y);
    }

    @Override
    public void draw(Batch batch, float alphaModulation)
    {
        super.draw(batch, alphaModulation);

    }

    public abstract void doAction();
}
