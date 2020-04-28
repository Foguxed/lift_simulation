package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.animation.objects.RelativeDrawable;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.structure.Etage;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class EtageVisu extends Etage implements PredictedDrawable, ReferencePosUsers, PersonneGroupContainer
{
    protected final AnimationProcess animation;

    protected final RenderedObject drawableObject;
    protected PorteAnimee[] portes;
    protected PersonneGroup test;
    protected List<PersonneGroup> listGroupes = new ArrayList<>();
    protected static final float ecartEntrePers = 100;
    protected float xMinAttente;
    protected float xMaxAttente;
    protected List<PersonneGroup> groupesEnAnimation = new ArrayList<>();
    protected BoutonTriangle boutonHaut;
    protected BoutonTriangle boutonBas;

    public EtageVisu(final AnimationProcess animation,final int numero, final float y)
    {
        super(numero);
        this.animation = animation;
        final RelativeSprite sprite = new RelativeSprite(AssetsManager.etage);

        drawableObject = new RenderedObject(sprite, new Vector2(0, 0), new Vector2(0, y));
        boutonHaut = new BoutonTriangle(true);
        boutonBas = new BoutonTriangle(false);
    }

    public void loadPortes(final List<AscenseurVisu>[] ascenseurs)
    {
        portes = new PorteAnimee[ascenseurs.length];
        for (int i = 0; i < ascenseurs.length; i++)
        {
            portes[i] = new PorteAnimee(new Vector2(ascenseurs[i].get(0).getX(), drawableObject.getY()));
        }
        xMinAttente = ascenseurs[ascenseurs.length - 1].get(0).getX() + 400;
        xMaxAttente = xMinAttente + 800;
        drawableObject.addRelativeDrawable(boutonHaut, new Vector2(xMinAttente - 80, 180));
        drawableObject.addRelativeDrawable(boutonBas, new Vector2(xMinAttente - 80, 80));

        final RelativeDrawable numSprite = NumberFont.getRenderedObject(num, 400, Color.WHITE);
        numSprite.resize(0.5f);
        numSprite.resetBaseSize();
        drawableObject.addRelativeDrawable(numSprite, new Vector2(xMaxAttente + 120, 90));
    }

    @Override
    public void register(final PersonneGroup pGroup)
    {
        add(pGroup);
        updateGroupsPos();
    }

    public void registerPourAnimation(final PersonneGroup pGroup)
    {
        groupesEnAnimation.add(pGroup);
    }

    public void unregisterPourAnimation(final PersonneGroup pGroup)
    {
        groupesEnAnimation.remove(pGroup);
    }

    protected void add(final PersonneGroup pGroup)
    {
        if (animation.gestioTaches().marcheArriereEnCours())
        {
            listGroupes.add(0, pGroup);
        } else
        {
            listGroupes.add(pGroup);
        }
    }

    @Override
    public void unregister(final PersonneGroup pGroup)
    {
        listGroupes.remove(pGroup);
        updateGroupsPos();
    }

    @Override
    public void update(final long time)
    {
        for (final PersonneGroup pers : new ArrayList<>(groupesEnAnimation))
        {
            pers.update(time);
        }
        for (final PersonneGroup pers : listGroupes)
        {
            pers.update(time);
        }
        for (final PorteAnimee porte : portes)
        {
            porte.update(time);
        }

    }

    @Override
    public void draw(final Batch batch)
    {
        for (final PersonneGroup pers : groupesEnAnimation)
        {
            pers.draw(batch);
        }
        for (final PersonneGroup pers : listGroupes)
        {
            pers.draw(batch);
        }
        drawableObject.draw(batch);
        for (final PorteAnimee porte : portes)
        {
            porte.draw(batch);
        }
    }

    @Override
    public void dispose()
    {
        drawableObject.dispose();
        for (final PorteAnimee porte : portes)
        {
            porte.dispose();
        }
        for (final PersonneGroup pers : listGroupes)
        {
            pers.dispose();
        }
    }

    public PorteAnimee getPorte(final int monteeId)
    {
        return portes[monteeId];
    }

    protected void updateGroupsPos()
    {
        float nouvelEcart = (xMaxAttente - xMinAttente) / listGroupes.size();
        if (nouvelEcart > ecartEntrePers)
        {
            nouvelEcart = ecartEntrePers;
        }
        for (int i = listGroupes.size() - 1; i >= 0; i--)
        {
            final Vector2 vec
            = new Vector2(xMaxAttente - (listGroupes.size() - 1 - i) * nouvelEcart, drawableObject.getY() + 20);
            listGroupes.get(i).repositionner(nouvelEcart / ecartEntrePers, vec);
        }
    }

    @Override
    public Vector2 getPosRef()
    {
        return new Vector2(xMinAttente, drawableObject.getY() + 30);
    }

    @Override
    public void arriveeDe(final int nbPersonnes, final int destination)
    {
        for (int i = 0; i < nbPersonnes; i++)
        {
            final PersonneVisu p = new PersonneVisu(destination);
            final PersonneGroup newGroup = new PersonneGroup(animation,this, p);
            p.enterPersonneGroup(newGroup);
            add(newGroup);
        }
        updateGroupsPos();
    }

    public void changerEtatBouton(final boolean haut, final boolean on)
    {
        if (haut)
        {
            boutonHaut.changeState(on);
        } else
        {
            boutonBas.changeState(on);
        }
    }

    @Override
    public String toString()
    {
        return "Etage " + getNiveau();
    }
}
