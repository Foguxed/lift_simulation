package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;

import fr.fogux.lift_simulator.ReDrawable;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class ImmeubleVisu implements PredictedDrawable
{

    protected EtageVisu[] etages;
    protected AscenseurVisu[] ascenseurs;
    public final int niveauMin;
    protected float lastLevelScreenY;
    protected static final float ecartAscenseurs = AssetsManager.ascenseur.getWidth() + 300;
    protected float lastAscenScreenX = ecartAscenseurs / 2;
    public static final float hauteurEtages = 380;
    protected final List<PersonneGroup> sorties = new ArrayList<PersonneGroup>();
    protected List<ReDrawable> textesList = new ArrayList<ReDrawable>();

    public ImmeubleVisu(int niveauMin, int niveauMax, int nbAscenseurs)
    {
        if (niveauMin > 0 || niveauMax < 0)
        {
            throw new SimulateurException("niveau 0 obligatoire");
        }
        int taille = niveauMax - niveauMin + 1;
        this.niveauMin = niveauMin;
        etages = new EtageVisu[taille];
        lastLevelScreenY = niveauMin * hauteurEtages;
        for (int i = 0; i < taille; i++)
        {
            etages[i] = new EtageVisu(i + niveauMin, lastLevelScreenY);
            lastLevelScreenY += hauteurEtages;
        }
        ascenseurs = new AscenseurVisu[nbAscenseurs];

        for (int j = 0; j < nbAscenseurs; j++)
        {
            ascenseurs[j] = new AscenseurVisu(j + 1, 5, lastAscenScreenX);
            lastAscenScreenX += ecartAscenseurs;
        }

        for (EtageVisu et : this.etages)
        {
            et.loadPortes(ascenseurs);
        }
    }

    /*
     * @Override public Ascenseur generateAscenseur(int id, int personnesMax) {
     * final AscenseurVisu asc = new AscenseurVisu(id,
     * personnesMax,lastAscenScreenX); lastAscenScreenX += ecartAscenseurs; return
     * asc; }
     * 
     * @Override public Etage<?> generateEtage(int num) { final EtageVisu etage =
     * new EtageVisu(num,lastLevelScreenY); lastLevelScreenY += 380; return etage; }
     */

    @Override
    public void update(long time)
    {
        // TODO Auto-generated method stub
        for (PersonneGroup p : new ArrayList<PersonneGroup>(sorties))
        {
            p.update(time);
        }
        for (AscenseurVisu asc : this.ascenseurs)
        {
            asc.update(time);
        }
        for (EtageVisu et : this.etages)
        {
            et.update(time);
        }
    }

    @Override
    public void draw(Batch batch)
    {
        for (PersonneGroup p : sorties)
        {
            p.draw(batch);
        }
        for (AscenseurVisu asc : this.ascenseurs)
        {
            asc.draw(batch);
        }
        for (EtageVisu et : this.etages)
        {
            et.draw(batch);
        }
        for (ReDrawable rd : textesList)
        {
            rd.redraw(batch);
        }
    }

    @Override
    public void dispose()
    {
        for (AscenseurVisu asc : this.ascenseurs)
        {
            asc.dispose();
        }
        for (EtageVisu et : this.etages)
        {
            et.dispose();
        }
    }

    public AscenseurVisu getAscenseur(int id)
    {
        return ascenseurs[id - 1];
    }

    public EtageVisu getEtage(int niveau)
    {
        // System.out.println(" etages "+ etages.length + " niveau min " + niveauMin + "
        // niveau " + niveau);
        return etages[niveau - niveauMin];
    }

    public void registerSortie(PersonneGroup group)
    {
        sorties.add(group);
    }

    public void unregisterSortie(PersonneGroup group)
    {
        sorties.remove(group);
    }

    public void register(ReDrawable redrawable)
    {
        if (!textesList.contains(redrawable))
        {
            textesList.add(redrawable);
        }
    }

    public void unregister(ReDrawable redrawable)
    {
        textesList.remove(redrawable);
    }
}
