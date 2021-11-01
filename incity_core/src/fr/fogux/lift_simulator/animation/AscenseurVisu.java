package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.animation.objects.RelativeDrawable;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class AscenseurVisu extends Ascenseur implements PredictedDrawable, PersonneGroupContainer

{
    protected final AnimationProcess animation;

    protected RenderedObject drawableObject;
    protected RenderedObject drawableBoutonList;
    protected TreeMap<Integer, RelativeDrawable> listBoutons = new TreeMap<>();
    protected RelativeSprite fondBoutons;
    protected List<PersonneGroup> listGroupes = new ArrayList<>();
    protected final int nbMaxPremiereLigne;

    protected final float minX;
    protected final float maxX;
    protected static final float ecartEntrePers = 80;

    public AscenseurVisu(final AnimationProcess animation,final AscId id, final int personnesMax, final float x, final float initialY)
    {
        super(animation.getConfig(),id, personnesMax, initialY);
        this.animation = animation;
        final RelativeSprite sprite = new RelativeSprite(AssetsManager.ascenseur);
        drawableObject = new RenderedObject(sprite, new Vector2(-sprite.getWidth() / 2, 22), new Vector2(x, initialY*ImmeubleVisu.hauteurEtages));
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

    @Override
    public void register(final PersonneGroup pGroup)
    {
        if (animation.gestioTaches().marcheArriereEnCours())
        {
            listGroupes.add(0, pGroup);
        } else
        {
            listGroupes.add(pGroup);
        }
        updateGroupsPos();
    }

    @Override
    public void unregister(final PersonneGroup personne)
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
    public void update(final long time)
    {
        drawableObject.setY(getPosition(time) * ImmeubleVisu.hauteurEtages);
        for (int i = 0; i < listGroupes.size(); i++)
        {
            final PersonneGroup pers = listGroupes.get(listGroupes.size() - 1 - i);
            pers.updatePos(drawableObject.getPosition().y);
            pers.update(time);
        }
    }

    

    protected float getPosition(final long time)
    {
        return planificateur.safeGetX(time);
    }

    public void changerPlanifierVers(DataTagCompound newP)
    {
    	planificateur.acceptCompound(newP);
    }
    
    @Override
    public void changerEtatBouton(final int bouton, final boolean allume)
    {
        super.changerEtatBouton(bouton, allume);
        if (allume)
        {
            if (!listBoutons.containsKey(bouton))
            {

                listBoutons.put(bouton, NumberFont.getRenderedObject(bouton, 500, Color.BLACK));
            }
        } else
        {
            final RelativeDrawable relSprite = listBoutons.remove(bouton);
            drawableBoutonList.removeRelativeDrawable(relSprite);
        }

        updateBoutonList();
    }

    protected void updateBoutonList()
    {
        final float espaceEntreChaque = 500;
        float y = -listBoutons.size() * espaceEntreChaque / 2;
        fondBoutons.setSize(500 + 60, listBoutons.size() * espaceEntreChaque + 60);
        drawableBoutonList.changerRelativePosition(fondBoutons, new Vector2(-30, y - 30));
        for (final RelativeDrawable obj : listBoutons.values())
        {
            drawableBoutonList.changerRelativePosition(obj, new Vector2(0, y));// ajoute automatiquement si l'objet
            // n'est pas dedans
            y += espaceEntreChaque;
        }

    }

    @Override
    public void draw(final Batch batch)
    {
        for (int i = 0; i < listGroupes.size(); i++)
        {
            final PersonneGroup pers = listGroupes.get(listGroupes.size() - 1 - i);
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
