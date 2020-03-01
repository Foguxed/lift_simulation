package fr.fogux.lift_simulator.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 *
 * @author Detobel36
 */
public class FontGenerator {
    
    private final FreeTypeFontGenerator generator;
    private final FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont font;
    
    public FontGenerator() 
    {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/spaceranger.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    }
    
    public BitmapFontCache getNewBitmapFontCache()
    {
        return font.newFontCache();
    }
    
    public BitmapFont getNewBitmapFont(int size,Color color)
    {
        parameter.size = size;
        parameter.color = color;
        return generator.generateFont(parameter);
    }
    
    public BitmapFont getNewBitmapFont(int size)
    {
        return getNewBitmapFont(size,Color.WHITE);
    }
    
    public BitmapFont getNewBitmapFont()
    {
        return getNewBitmapFont(16);
    }
    
    public void setColor(final Color color) {
        parameter.color = color;
        font = generator.generateFont(parameter);
    }
    
    public void setSize(final int size) {
        parameter.size = size;
        font = generator.generateFont(parameter);
    }

    /*public Vector2 getBounds(String str) {
        layout.setText(font, str);
        return new Vector2(layout.width, layout.height);
    }*/
    
    public void draw(final SpriteBatch batch, final String text, final float x, final float y) {
        font.draw(batch, text, x, y);
    }
    
    public void dispose() {
        generator.dispose();
    }
    
}
