package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.animation.objects.RelativeDrawable;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.screens.GameScreen;
import fr.fogux.lift_simulator.structure.Etage;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class EtageVisu extends Etage implements PredictedDrawable, ReferencePosUsers, PersonneGroupContainer
{
    protected final RenderedObject drawableObject;
    protected PorteAnimee[] portes;
    protected PersonneGroup test;
    protected List<PersonneGroup> listGroupes = new ArrayList<PersonneGroup>();
    protected static final float ecartEntrePers = 100;
    protected float xMinAttente;
    protected float xMaxAttente;
    protected List<PersonneGroup> groupesEnAnimation = new ArrayList<PersonneGroup>();
    protected BoutonTriangle boutonHaut;
    protected BoutonTriangle boutonBas;

    public EtageVisu(int numero, float y)
    {
        super(numero);
        final RelativeSprite sprite = new RelativeSprite(AssetsManager.etage);

        drawableObject = new RenderedObject(sprite, new Vector2(0, 0), new Vector2(0, y));
        boutonHaut = new BoutonTriangle(true);
        boutonBas = new BoutonTriangle(false);

        System.out.println("monY " + y + "draw " + drawableObject.getY());
    }

    public void loadPortes(AscenseurVisu[] ascenseurs)
    {
        portes = new PorteAnimee[ascenseurs.length];
        for (int i = 0; i < ascenseurs.length; i++)
        {
            portes[i] = new PorteAnimee(new Vector2(ascenseurs[i].getX(), drawableObject.getY()));
        }
        xMinAttente = ascenseurs[ascenseurs.length - 1].getX() + 400;
        xMaxAttente = xMinAttente + 800;
        drawableObject.addRelativeDrawable(boutonHaut, new Vector2(xMinAttente - 80, 180));
        drawableObject.addRelativeDrawable(boutonBas, new Vector2(xMinAttente - 80, 80));

        RelativeDrawable numSprite = NumberFont.getRenderedObject(num, 400, Color.WHITE);
        numSprite.resize(0.5f);
        numSprite.resetBaseSize();
        drawableObject.addRelativeDrawable(numSprite, new Vector2(xMaxAttente + 120, 90));
        System.out.println("DRAWABLE " + drawableObject.getY());
    }

    public void register(PersonneGroup pGroup)
    {
        System.out.println("personne register ");
        add(pGroup);
        System.out.println("groupe size" + listGroupes.size());
        updateGroupsPos();
    }

    public void registerPourAnimation(PersonneGroup pGroup)
    {
        groupesEnAnimation.add(pGroup);
    }

    public void unregisterPourAnimation(PersonneGroup pGroup)
    {
        groupesEnAnimation.remove(pGroup);
    }

    protected void add(PersonneGroup pGroup)
    {
        if (GestionnaireDeTaches.marcheArriere())
        {
            listGroupes.add(0, pGroup);
        } else
        {
            listGroupes.add(pGroup);
        }
    }

    public void unregister(PersonneGroup pGroup)
    {
        System.out.println("personne unreg ");
        listGroupes.remove(pGroup);
        updateGroupsPos();
    }

    @Override
    public void update(long time)
    {

        for (PersonneGroup pers : new ArrayList<PersonneGroup>(groupesEnAnimation))
        {
            pers.update(time);
        }
        for (PersonneGroup pers : listGroupes)
        {
            pers.update(time);
        }
        for (PorteAnimee porte : portes)
        {
            porte.update(time);
        }

    }

    @Override
    public void draw(Batch batch)
    {
        for (PersonneGroup pers : groupesEnAnimation)
        {
            pers.draw(batch);
        }
        for (PersonneGroup pers : listGroupes)
        {
            pers.draw(batch);
        }
        drawableObject.draw(batch);
        for (PorteAnimee porte : portes)
        {
            porte.draw(batch);
        }
    }

    @Override
    public void dispose()
    {
        drawableObject.dispose();
        for (PorteAnimee porte : portes)
        {
            porte.dispose();
        }
        for (PersonneGroup pers : listGroupes)
        {
            pers.dispose();
        }
    }

    public PorteAnimee getPorte(int ascenseurId)
    {
        return portes[ascenseurId - 1];
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
            Vector2 vec
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
    public void arriveeDe(int nbPersonnes, int destination)
    {
        System.out.println(
            "update evenement debut arrivee " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        for (int i = 0; i < nbPersonnes; i++)
        {
            System.out.println("boucle 1 " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
            PersonneVisu p = new PersonneVisu(destination);
            PersonneGroup newGroup = new PersonneGroup(this, p);
            System.out.println("boucle 2 " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
            p.enterPersonneGroup(newGroup);
            System.out.println("boucle 3 " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
            add(newGroup);
            System.out.println("boucle 4 " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        }
        System.out.println(
            "update evenement fin arrivee " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        updateGroupsPos();
    }

    public void changerEtatBouton(boolean haut, boolean on)
    {
        if (haut)
        {
            boutonHaut.changeState(on);
        } else
        {
            boutonBas.changeState(on);
        }
    }
}
