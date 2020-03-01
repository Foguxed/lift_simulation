package fr.fogux.lift_simulator.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.menu.Bouton;
import fr.fogux.lift_simulator.menu.FileInputManager;
import fr.fogux.lift_simulator.menu.FileSearcher;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class FileResearchScreen extends CustomScreen
{
    
    protected FileInputManager inputs;
    protected CustomScreen screenPrecedent;
    protected final FileSearcher output;
    protected final int researchDeep;
    
    protected FileResearchScreen(Simulateur main, Path basePath,CustomScreen screenPrecedent,int researchDeep,FileSearcher output)
    {
         super(main);
         this.researchDeep = researchDeep;
         this.screenPrecedent = screenPrecedent;
         this.output = output;
            inputs = new FileInputManager()
         {
            @Override
            public void escapePressed()
            {
                backToParent();
            }
         };
         inputs.setScreen(this);
        Pixmap map = new Pixmap(10,10,Format.RGB565);
        map.setColor(Color.GRAY);
        map.fill();
        Texture textureBouton = new Texture(map);
        float y = 1020;
        try
        {
            for(Path p : Files.newDirectoryStream(basePath))
            {
                File f = p.toFile();
                if(f.isDirectory())
                {
                    inputs.register(new BoutonFichier(f,textureBouton,AssetsManager.fichierFont,800,45,300,y));
                    y -= 55;
                }
                
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init()
    {
        Gdx.input.setInputProcessor(inputs);
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
    
    protected void fichierSelectionne(File f)
    {
        System.out.println("fichier selc " + f.getName() + " depth " + researchDeep);
        if(researchDeep > 0)
        {
            CustomScreen scr = new FileResearchScreen(main,
                    Paths.get(f.getAbsolutePath()), this, researchDeep-1,output);
            main.setScreen(scr);
            scr.init();
        }
        else
        {
            this.output.fichierChoisi(f);
        }
    }
    
    protected void backToParent()
    {
        System.out.println("screen precedent " +screenPrecedent.getClass() );
        main.setScreen(screenPrecedent);
        screenPrecedent.init();
    }
    
    
    class BoutonFichier extends Bouton
    {
        protected File f;
        
        public BoutonFichier(File f, Texture texture, BitmapFont font, float width, float height, float x, float y)
        {
            super(f.getName(), texture, font, width, height, x, y);
            this.f = f;
        }

        @Override
        public void doAction()
        {
            fichierSelectionne(f);
        }
    }
}
