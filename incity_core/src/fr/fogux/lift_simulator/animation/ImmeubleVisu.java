package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.ReDrawable;
import fr.fogux.lift_simulator.exceptions.SimulateurException;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class ImmeubleVisu implements PredictedDrawable
{

    protected EtageVisu[] etages;
    protected List<AscenseurVisu>[] ascenseurs;
    public final int niveauMin;
    protected float lastLevelScreenY;
    protected static final float ecartAscenseurs = AssetsManager.ascenseur.getWidth() + 300;
    protected float lastAscenScreenX = ecartAscenseurs / 2;
    public static final float hauteurEtages = 380;
    protected final List<PersonneGroup> sorties = new ArrayList<>();
    protected List<ReDrawable> textesList = new ArrayList<>();

    public ImmeubleVisu(final AnimationProcess animation)
    {
        niveauMin = animation.getConfig().getNiveauMin();
        final int niveauMax = animation.getConfig().getNiveauMax();
        if (niveauMin > 0 || niveauMax < 0)
        {
            throw new SimulateurException("niveau 0 obligatoire");
        }
        final int taille = niveauMax - niveauMin + 1;

        etages = new EtageVisu[taille];
        lastLevelScreenY = niveauMin * hauteurEtages;
        for (int i = 0; i < taille; i++)
        {
            etages[i] = new EtageVisu(animation, i + niveauMin, lastLevelScreenY);
            lastLevelScreenY += hauteurEtages;
        }


        final int[] repartAsc = animation.getConfig().getRepartAscenseurs();
        ascenseurs = new ArrayList[repartAsc.length];

        for (int j = 0; j < ascenseurs.length; j++)
        {
            ascenseurs[j] = new ArrayList<>(repartAsc[j]);
            for(int i = 0; i < repartAsc[j]; i ++)
            {
                ascenseurs[j].add(new AscenseurVisu(animation, new AscId(j, i), 5, lastAscenScreenX, i));
            }

            lastAscenScreenX += ecartAscenseurs;
        }

        for (final EtageVisu et : etages)
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
    public void update(final long time)
    {
        for (final PersonneGroup p : new ArrayList<>(sorties))
        {
            p.update(time);
        }
        for(final List<AscenseurVisu> list : ascenseurs)
        {
            for (final AscenseurVisu asc : list)
            {
                asc.update(time);
            }
        }
        for (final EtageVisu et : etages)
        {
            et.update(time);
        }
    }

    @Override
    public void draw(final Batch batch)
    {
        for (final PersonneGroup p : sorties)
        {
            p.draw(batch);
        }
        for(final List<AscenseurVisu> list : ascenseurs)
        {
            for (final AscenseurVisu asc : list)
            {
                asc.draw(batch);
            }
        }
        for (final EtageVisu et : etages)
        {
            et.draw(batch);
        }
        for (final ReDrawable rd : textesList)
        {
            rd.redraw(batch);
        }
    }

    @Override
    public void dispose()
    {
        for(final List<AscenseurVisu> list : ascenseurs)
        {
            for (final AscenseurVisu asc : list)
            {
                asc.dispose();
            }
        }
        for (final EtageVisu et : etages)
        {
            et.dispose();
        }
    }

    public AscenseurVisu getAscenseur(final AscId id)
    {
        return ascenseurs[id.monteeId].get(id.stackId);
    }

    public EtageVisu getEtage(final int niveau)
    {
        return etages[niveau - niveauMin];
    }

    public void registerSortie(final PersonneGroup group)
    {
        sorties.add(group);
    }

    public void unregisterSortie(final PersonneGroup group)
    {
        sorties.remove(group);
    }

    public void register(final ReDrawable redrawable)
    {
        if (!textesList.contains(redrawable))
        {
            textesList.add(redrawable);
        }
    }

    public void unregister(final ReDrawable redrawable)
    {
        textesList.remove(redrawable);
    }
}
