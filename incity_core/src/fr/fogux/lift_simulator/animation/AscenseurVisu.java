package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.animation.objects.RelativeDrawable;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class AscenseurVisu extends Ascenseur implements PredictedDrawable, PersonneGroupContainer

{
    protected RenderedObject drawableObject;
    protected RenderedObject drawableBoutonList;
    protected TreeMap<Integer, RelativeDrawable> listBoutons = new TreeMap<Integer, RelativeDrawable>();
    protected RelativeSprite fondBoutons;
    protected List<PersonneGroup> listGroupes = new ArrayList<PersonneGroup>();
    protected final int nbMaxPremiereLigne;

    protected final float minX;
    protected final float maxX;
    protected static final float ecartEntrePers = 80;

    public AscenseurVisu(int id, int personnesMax, float x)
    {
        super(id, personnesMax);
        RelativeSprite sprite = new RelativeSprite(AssetsManager.ascenseur);
        drawableObject = new RenderedObject(sprite, new Vector2(-sprite.getWidth() / 2, 22), new Vector2(x, 0));
        drawableBoutonList = new RenderedObject();
        drawableObject.addRelativeDrawable(
            drawableBoutonList, new Vector2(sprite.getWidth() / 2 + 40, sprite.getHeight() / 2));
        fondBoutons = new RelativeSprite(AssetsManager.fondBouton);
        drawableBoutonList.resize(0.2f);
        minX = x - sprite.getWidth() / 2 + 30;
        maxX = x + sprite.getWidth() / 2 - 30;
        int nb = (int) Math.ceil(personnesMax / 3d);
        if (nb < (maxX - minX) / ecartEntrePers)
        {
            nb = (int) ((maxX - minX) / ecartEntrePers);
        }
        nbMaxPremiereLigne = nb;
        System.out.println("ascVisu minx " + minX + " maxX " + maxX + " nbPers " + nbMaxPremiereLigne);
        updateBoutonList();
    }

    public float getX()
    {
        return drawableObject.getX();
    }

    public void register(PersonneGroup pGroup)
    {
        if (GestionnaireDeTaches.marcheArriere())
        {
            listGroupes.add(0, pGroup);
        } else
        {
            listGroupes.add(pGroup);
        }
        updateGroupsPos();
    }

    public void unregister(PersonneGroup personne)
    {
        listGroupes.remove(personne);
        updateGroupsPos();
    }

    protected void updateGroupsPos()
    {
        int nbPremiereLigne = listGroupes.size();
        if (nbPremiereLigne > nbMaxPremiereLigne)
        {
            nbPremiereLigne = nbMaxPremiereLigne;
        }
        float newEcart = (maxX - minX) / nbPremiereLigne;
        float sizeMult = newEcart / ecartEntrePers;
        if (sizeMult > 1)
        {
            sizeMult = 1;
        }
        for (int i = 0; i < nbPremiereLigne; i++)
        {
            listGroupes.get(i).repositionnerDansAscenseur(this, sizeMult, minX + newEcart / 2 + newEcart * i, 30);
        }

        if (listGroupes.size() > nbMaxPremiereLigne)
        {
            newEcart = newEcart / 2;
            final float newSizeMult = sizeMult / 2;
            for (int i = nbPremiereLigne; i < listGroupes.size(); i++)
            {
                listGroupes.get(i).repositionnerDansAscenseur(
                    this, newSizeMult, minX + newEcart / 2 + newEcart * (i - nbPremiereLigne), 30 + 150 * sizeMult);
            }
        }
    }

    @Override
    public void update(long time)
    {
        drawableObject.setY(getPosition(time).y * ImmeubleVisu.hauteurEtages);
        for (int i = 0; i < listGroupes.size(); i++)
        {
            PersonneGroup pers = listGroupes.get(listGroupes.size() - 1 - i);
            pers.updatePos(drawableObject.getPosition().y);
            pers.update(time);
        }
    }

    @Override
    public void changerEtatBouton(int bouton, boolean allume)
    {
        super.changerEtatBouton(bouton, allume);
        if (allume)
        {
            if (!listBoutons.containsKey((Integer) bouton))
            {

                listBoutons.put((Integer) bouton, NumberFont.getRenderedObject(bouton, 500, Color.BLACK));
            }
        } else
        {
            RelativeDrawable relSprite = listBoutons.remove((Integer) bouton);
            drawableBoutonList.removeRelativeDrawable(relSprite);
        }

        updateBoutonList();
    }

    protected void updateBoutonList()
    {
        float espaceEntreChaque = 500;
        float y = -listBoutons.size() * espaceEntreChaque / 2;
        fondBoutons.setSize(500 + 60, listBoutons.size() * espaceEntreChaque + 60);
        drawableBoutonList.changerRelativePosition(fondBoutons, new Vector2(-30, y - 30));
        for (RelativeDrawable obj : listBoutons.values())
        {
            drawableBoutonList.changerRelativePosition(obj, new Vector2(0, y));// ajoute automatiquement si l'objet
                                                                               // n'est pas dedans
            y += espaceEntreChaque;
        }

    }

    @Override
    public void draw(Batch batch)
    {
        for (int i = 0; i < listGroupes.size(); i++)
        {
            PersonneGroup pers = listGroupes.get(listGroupes.size() - 1 - i);
            pers.draw(batch);
        }
        drawableObject.draw(batch);
    }

    @Override
    public void dispose()
    {
        drawableObject.dispose();
    }
}
