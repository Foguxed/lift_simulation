package fr.fogux.lift_simulator.screens;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.menu.Bouton;
import fr.fogux.lift_simulator.menu.FileInputManager;
import fr.fogux.lift_simulator.menu.FileQuerryProtocol;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class FileResearchScreen extends CustomScreen
{
    protected static final Texture textureBouton = textureBouton();

    protected FileInputManager inputs;
    protected CustomScreen screenPrecedent;
    protected final FileQuerryProtocol fQuerryProtocol;
    protected final int researchDeep;
    float y = 1020;

    protected FileResearchScreen(final Simulateur main, final File directoryFile, final CustomScreen screenPrecedent, final int researchDeep,
        final FileQuerryProtocol fQuerryProtocol)
    {
        super(main);
        this.fQuerryProtocol = fQuerryProtocol;
        this.researchDeep = researchDeep;
        this.screenPrecedent = screenPrecedent;
        inputs = new FileInputManager()
        {
            @Override
            public void escapePressed()
            {
                backToParent();
            }
        };
        inputs.setScreen(this);
        fQuerryProtocol.updateFileScreen(this, directoryFile, researchDeep);
    }



    public void registerFichierAsButton(final File f)
    {
        inputs.register(new BoutonFichier(f, textureBouton, AssetsManager.fichierFont, 800, 45, 300, y));
        y -= 55;
    }

    public void registerFichiersAsButton(final File[] files)
    {
        sortByName(files);
        for (final File file : files)
        {
            registerFichierAsButton(file);
        }
    }

    private void sortByName(final File[] files)
    {
        Arrays.sort(files, 0, files.length, new Comparator<File>()
        {

            @Override
            public int compare(final File o1, final File o2)
            {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    protected static final Texture textureBouton()
    {
        final Pixmap map = new Pixmap(10, 10, Format.RGB565);
        map.setColor(Color.GRAY);
        map.fill();
        return new Texture(map);
    }

    @Override
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

    public void subSearch(final File f)
    {
        final CustomScreen scr = new FileResearchScreen(main, f, this, researchDeep + 1, fQuerryProtocol);
        main.setScreen(scr);
        scr.init();
    }

    protected void fichierSelectionne(final File f)
    {
        fQuerryProtocol.onClic(this,f,researchDeep);
        System.out.println("fichier selc " + f.getName() + " depth " + researchDeep);
    }

    protected void backToParent()
    {
        System.out.println("screen precedent " + screenPrecedent.getClass());
        main.setScreen(screenPrecedent);
        screenPrecedent.init();
    }

    public void displayInfo(final String s)
    {
        System.out.println(s);
    }

    class BoutonFichier extends Bouton
    {
        protected File f;

        public BoutonFichier(final File f, final Texture texture, final BitmapFont font, final float width, final float height, final float x, final float y)
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
