package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.animation.objects.RenderedObjectNumber;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.structure.Personne;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class PersonneGroup implements PredictedDrawable
{
    protected final AnimationProcess animation;

    protected List<Personne> persList = new ArrayList<>();
    protected RenderedObjectNumber txt;
    protected RelativeSprite sprite;
    protected RenderedObject rendered;
    protected float YrelToAsc;
    protected Animation animationDep;
    protected Animation animationSortie;
    protected PersonneGroupContainer container;

    protected Vector2 departAnimation;
    protected Vector2 deplacementAnimation;
    protected float nextAlphaModulation = -1;
    protected long heureCreation;

    public PersonneGroup(final AnimationProcess animation,final PersonneGroupContainer container, final PersonneVisu personneDeDepart)
    {
        this.container = container;
        this.animation = animation;
        rendered = new RenderedObject();
        sprite = new RelativeSprite(AssetsManager.personne);
        sprite.setSize(sprite.getWidth() * 0.4f, sprite.getHeight() * 0.4f);
        rendered.addRelativeDrawable(sprite, new Vector2(-sprite.getWidth() / 2, -20));
        updateText(personneDeDepart.getDestination());
        heureCreation = animation.getTime();
    }

    public void repositionnerDansAscenseur(final Ascenseur asc, final float sizeMultiplicator, final float fixedX, final float relativeY)
    {
        rendered.resize(sizeMultiplicator);
        rendered.setX(fixedX);
        YrelToAsc = relativeY;
    }

    protected Color getColor(final long timeArrivee, final long timeActuel)
    {
        final long duree = timeActuel - timeArrivee;
        float val = duree / (float) (1000 * 60 * 3);

        if (val > 2)
        {
            val = 2;
        }
        if (val < 0)
        {
            val = 0;
        }
        if (val <= 1)
        {
            return new Color(0.40f, 0.50f + 0.5f * val, 0.9f - 0.7f * val, 1);
        } else
        {
            val--;
            return new Color(0.40f + 0.6f * val, 0.9f - 0.7f * val, 0.20f, 1);
        }
    }

    public void repositionner(final float sizeMultiplicator, final Vector2 pos)
    {
        rendered.repositionner(sizeMultiplicator, pos);
    }

    public void animationDeplacement(final long time, final long duree, final EtageVisu depart, final AscenseurVisu arrivee)
    {
        update(animation.getTime());
        if(container!=null)
        {
            container.unregister(this);
        }
        container = null;
        depart.registerPourAnimation(this);
        animationDep = new AnimationDeplacement(time, duree, depart, arrivee);
        departAnimation = depart.getPosRef();
        deplacementAnimation = depart.getPorte(arrivee.getId().monteeId).getPosRef().sub(departAnimation);
    }

    public void animationSortie(final long time, final long duree)
    {
        update(animation.getTime());
        container.unregister(this);
        animation.getImmeubleVisu().registerSortie(this);
        animation.getImmeubleVisu().register(txt);
        animationSortie = new Animation(time, duree)
        {
            @Override
            public void depassementPositif()
            {
                unregisterSortie();
                animation.getImmeubleVisu().unregister(txt);
            }

            @Override
            public void depassementNegatif()
            {
                unregisterSortie();
                retournerDansContainer();
            }
        };
    }

    private void unregisterSortie()
    {
        animationSortie = null;
        animation.getImmeubleVisu().unregisterSortie(this);
    }

    private void retournerDansContainer()
    {
        container.register(this);
    }

    public void finAnimationDep(final EtageVisu etage)
    {
        etage.unregisterPourAnimation(this);
        animationDep = null;
    }

    public void entrer(final PersonneGroupContainer container)
    {
        this.container = container;
        container.register(this);
    }

    public void add(final Personne pers)
    {

        if (persList.isEmpty())
        {
            updateText(pers.getDestination());
        }
        persList.add(pers);
    }

    protected void updateText(final int destination)
    {
        rendered.removeRelativeDrawable(txt);
        animation.getImmeubleVisu().unregister(txt);
        txt = NumberFont.getRenderedObject(destination, 450, Color.WHITE);
        txt.strongResize(0.14f);
        rendered.addRelativeDrawable(txt, new Vector2(-20, 110));
        animation.getImmeubleVisu().register(txt);
    }

    public void remove(final Personne pers)
    {
        persList.remove(pers);
        if (persList.isEmpty())
        {
            dispose();
        }
    }

    public void updatePos(final float ascY)
    {
        rendered.setY(ascY + YrelToAsc);
    }

    @Override
    public void update(final long time)
    {
        final Color c = getColor(heureCreation, time);
        sprite.setColor(c);
        if (animationSortie != null)
        {
            final float f = animationSortie.avancement(time);
            if (animationSortie != null)
            {
                rendered.resize(0.9f + (1.1f) * f);
                nextAlphaModulation = 1 - f;
            }
        } else
        {
            nextAlphaModulation = -1;
            if (animationDep != null)
            {
                final float mult = animationDep.avancement(time);
                if (animationDep != null)
                {
                    final Vector2 dep = deplacementAnimation.cpy();
                    dep.set(dep.x * mult, dep.y * mult);
                    rendered.setPosition(dep.add(departAnimation));
                }
            }

        }
    }

    @Override
    public void draw(final Batch batch)
    {
        if (nextAlphaModulation != -1)
        {
            rendered.draw(batch, nextAlphaModulation);
        } else
        {
            rendered.draw(batch);
        }
    }

    @Override
    public void dispose()
    {
        if (container != null)
        {
            container.unregister(this);
        }
        animation.getImmeubleVisu().unregister(txt);
    }

    private class AnimationDeplacement extends Animation
    {
        protected final EtageVisu etage;
        protected final AscenseurVisu asc;

        public AnimationDeplacement(final long time, final long duree, final EtageVisu depart, final AscenseurVisu arrivee)
        {
            super(time, duree);
            etage = depart;
            asc = arrivee;

        }

        @Override
        public void depassementNegatif()
        {
            finAnimationDep(etage);
            entrer(etage);
        }

        @Override
        public void depassementPositif()
        {
            finAnimationDep(etage);
            entrer(asc);
        }
    }
}
