package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.GestionnaireDeTachesVisu;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.animation.objects.RelativeSprite;
import fr.fogux.lift_simulator.animation.objects.RenderedObject;
import fr.fogux.lift_simulator.animation.objects.RenderedObjectNumber;
import fr.fogux.lift_simulator.screens.GameScreen;
import fr.fogux.lift_simulator.structure.Ascenseur;
import fr.fogux.lift_simulator.structure.Personne;
import fr.fogux.lift_simulator.utils.AssetsManager;
import fr.fogux.lift_simulator.utils.NumberFont;

public class PersonneGroup implements PredictedDrawable
{
    protected List<Personne> persList = new ArrayList<Personne>();
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
    
    public PersonneGroup(PersonneGroupContainer container,PersonneVisu personneDeDepart)
    {
        this.container = container;
        rendered = new RenderedObject();
        sprite = new RelativeSprite(AssetsManager.personne);
        sprite.setSize(sprite.getWidth()*0.4f,sprite.getHeight()*0.4f);
        rendered.addRelativeDrawable(sprite,new Vector2(-sprite.getWidth()/2,-20));
        //System.out.println("avant relative text zone " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        //System.out.println("apres relative text zone " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        updateText(personneDeDepart.getDestination());
        heureCreation = GestionnaireDeTaches.getInnerTime();
    }
    
    public void repositionnerDansAscenseur(Ascenseur asc, float sizeMultiplicator ,float fixedX, float relativeY)
    {
        rendered.resize(sizeMultiplicator);
        rendered.setX(fixedX);
        YrelToAsc = relativeY;
    }
    
    protected Color getColor(long timeArrivee, long timeActuel)
    {
        long duree = timeActuel - timeArrivee;
        float val = duree/(float)(1000*60*3);;
        if(val > 2)
        {
            val = 2;
        }
        if(val < 0)
        {
            val = 0;
        }
        if(val <= 1)
        {
            return new Color(0.40f,0.50f + 0.5f*val,0.9f-0.7f*val,1);
        }
        else
        {
            val --;
            return new Color(0.40f+0.6f*val,0.9f-0.7f*val,0.20f,1);
        }
    }
    
    public void repositionner(float sizeMultiplicator,Vector2 pos)
    {
        rendered.repositionner(sizeMultiplicator, pos);
    }
    
    public void animationDeplacement(long time, long duree,EtageVisu depart,AscenseurVisu arrivee)
    {
        update(GestionnaireDeTachesVisu.getInnerTime());
        container.unregister(this);
        container = null;
        depart.registerPourAnimation(this);
        animationDep =  new AnimationDeplacement(time,duree,depart,arrivee);
        System.out.println("animation dep ");
        this.departAnimation = depart.getPosRef();
        this.deplacementAnimation = depart.getPorte(arrivee.getId()).getPosRef().sub(departAnimation);
    }
    
    public void animationSortie(long time, long duree)
    {
        update(GestionnaireDeTachesVisu.getInnerTime());
        container.unregister(this);
        Simulateur.getImmeubleVisu().registerSortie(this);
        Simulateur.getImmeubleVisu().register(txt);
        System.out.println("animSortie2 ");
        animationSortie = new Animation(time, duree)
        {
            @Override
            public void depassementPositif()
            {
                unregisterSortie();
                Simulateur.getImmeubleVisu().unregister(txt);
            }
            
            @Override
            public void depassementNegatif()
            {
                unregisterSortie();
                retournerDansContainer();
            }
        };
        System.out.println("animSortie3 ");
    }
    
    private void unregisterSortie()
    {
        animationSortie = null;
        Simulateur.getImmeubleVisu().unregisterSortie(this);
    }
    
    private void retournerDansContainer()
    {
        container.register(this);
    }
    
    public void finAnimationDep(EtageVisu etage)
    {
        etage.unregisterPourAnimation(this);
        animationDep = null;
    }
    
    public void entrer(PersonneGroupContainer container)
    {
        this.container = container;
        container.register(this);
    }
    
    public void add(Personne pers)
    {
        
        if(persList.isEmpty())
        {
            System.out.println("avant relText zone setText update " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
            updateText(pers.getDestination());
            System.out.println("apres relText zone setText update " + String.valueOf(System.currentTimeMillis() - GameScreen.realTimeUpdate));
        }
        persList.add(pers);
   }
    
    protected void updateText(int destination)
    {
        rendered.removeRelativeDrawable(txt);
        Simulateur.getImmeubleVisu().unregister(txt);
        txt = NumberFont.getRenderedObject(destination,450,Color.WHITE);
        txt.strongResize(0.14f);
        rendered.addRelativeDrawable(txt, new Vector2(-20,110));
        Simulateur.getImmeubleVisu().register(txt);
    }
    
    public void remove(Personne pers)
    {
         persList.remove(pers);      
         if(persList.isEmpty())
         {
             dispose();
         }
    }
    
    public void updatePos(float ascY)
    {
        rendered.setY(ascY+YrelToAsc);
    }
    
    @Override
    public void update(long time)
    {
        Color c = getColor(heureCreation, time);
        //System.out.println("color"  + c);
        sprite.setColor(c);
        if(animationSortie != null)
        {
            final float f = animationSortie.avancement(time);
            if(animationSortie != null)
            {
                rendered.resize(0.9f + (1.1f)*f);
                nextAlphaModulation = 1-f;
            }
        }
        else
        {
            nextAlphaModulation = -1;
            if(animationDep != null)
            {
                float mult = animationDep.avancement(time);
                if(animationDep != null)
                {
                    final Vector2 dep = deplacementAnimation.cpy();
                    dep.set(dep.x*mult,dep.y*mult);
                    rendered.setPosition(dep.add(departAnimation));
                }
            }
            
        }
    }
    
    @Override
    public void draw(Batch batch)
    {
        if(nextAlphaModulation != -1)
        {
            rendered.draw(batch,nextAlphaModulation);
        }
        else
        {
            rendered.draw(batch);
        }
    }


    
    @Override
    public void dispose()
    {
        System.out.println("UNREGISTER pergroup");
        if(container!=null)
        {
            container.unregister(this);
        }
        Simulateur.getImmeubleVisu().unregister(txt);
    }
    
    
    
    private class AnimationDeplacement extends Animation
    {
        protected final EtageVisu etage;
        protected final AscenseurVisu asc;
        
        public AnimationDeplacement(long time, long duree,EtageVisu depart, AscenseurVisu arrivee)
        {
            super(time,duree);
            this.etage = depart;
            this.asc = arrivee;
            
        }
        
        @Override
        public void depassementNegatif()
        {
            System.out.println("depassementNegatif animation Deplacement");
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
