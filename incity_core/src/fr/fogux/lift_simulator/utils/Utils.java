package fr.fogux.lift_simulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.algorithmes.IdAscPersPool;
import fr.fogux.lift_simulator.mind.algorithmes.TreeExplorer;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg.AscCycleOption;
import fr.fogux.lift_simulator.mind.pool.ByEtageFewUpdatePool;
import fr.fogux.lift_simulator.mind.pool.IdByEtagePool;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.screens.CustomScreen;
import fr.fogux.lift_simulator.structure.AscId;

public class Utils
{

    private static int debugCompteur = 0;

    public static boolean startdebug = false;
    
    public static final IntEnsemble incityInferieur =
            new IntEnsemble(0, 1, 2, 18);
        public static final IntEnsemble incitySuperieur =
            new IntEnsemble(1, 2, 18, 37);
        

    public static final Consumer<?> emptyComsumer = new Consumer()
    {

        @Override
        public void accept(final Object t)
        {
        }
    };

    public static final <T> Consumer<T> emptyConsumer()
    {
        return (Consumer<T>)emptyComsumer;
    }

    public static void checkDebug(final Simulation s)
    {
        if(!(s.getPrgm() instanceof TreeExplorer))
        {
            return;
        }
        final TreeExplorer<ByEtageFewUpdatePool,AscCycleOption> t = (TreeExplorer)s.getPrgm();
        if(!s.paused() && t.montees.stream().anyMatch(m -> m.ascenseurs.stream().anyMatch(a -> !a.contenu.isEmpty())))
        {
            throw new SimulateurAcceptableException("un asc non vide sans pose");
        }

    }

    public static int newDebugCompteurId()
    {
        debugCompteur ++;
        return (debugCompteur - 1);
    }

    public static int debug()
    {
        return debugCompteur;
    }

    public static List<IdAscPersPool> formerStandardPools(final ConfigSimu c)
    {
        return formerStandardLargesPools(c.getRepartAscenseurs()[0],c.getRepartAscenseurs().length, c.getNiveauMin(), c.getNiveauMax());
    }

    public static List<IdAscPersPool> formerStandardLargesPools(final int nbAscParMontee, final int nbMontees, final int etageMin, final int etageMaxInclu)
    {
        final List<IdAscPersPool> retour = new ArrayList<>(nbAscParMontee);
        final int dernierEtageExcluPremierAsc = etageMaxInclu +2 - nbAscParMontee;
        for(int i = 0 ; i < nbAscParMontee ; i ++)
        {
            final List<AscId> ids = new ArrayList<>(nbMontees);
            for(int j = 0 ; j < nbMontees; j ++)
            {
                ids.add(new AscId(j,i));
            }
            retour.add(new IdByEtagePool(new IntEnsemble(etageMin + i,dernierEtageExcluPremierAsc + i), ids));
        }
        return retour;
    }
    
    public static List<IdAscPersPool> formerPoolsIncity(final ConfigSimu c)
    {
    	int nbMontees = c.getRepartAscenseurs().length;
    	final List<IdAscPersPool> retour = new ArrayList<>(2);
    	final List<AscId> ascSups = new ArrayList<>(nbMontees);
    	final List<AscId> ascInfs = new ArrayList<>(nbMontees);
    	for(int i = 0; i < nbMontees; i ++)
    	{
    		ascInfs.add(new AscId(i,0));
    		ascSups.add(new AscId(i, 1));
    	}
		retour.add(new IdByEtagePool(Utils.incityInferieur, ascInfs));
		retour.add(new IdByEtagePool(Utils.incitySuperieur, ascSups));
		return retour;
    }
    
    public static long timeInMilis(final String strFormatJHMinSecMil)// format J:H:Min:Sec:Milis
    {
        final Integer[] ints = new Integer[5];
        final String[] strs = strFormatJHMinSecMil.split(":");
        for (int i = 0; i < strs.length; i++)
        {
            // System.out.println("val " + i +" " + strs[i]);
            ints[i] = Integer.parseInt(strs[i]);
        }
        return ((((ints[0] * 24) + ints[1]) * 60 + ints[2]) * 60 + ints[3]) * 1000 + ints[4];
    }

    public static String getTimeString(final long timeInMilis)
    {
        final long nbSecondes = timeInMilis / 1000;
        final long nbMinutes = nbSecondes / 60;
        final long nbHeures = nbMinutes / 60;
        final long nbJours = nbHeures / 24;
        return nbJours + ":" + nbHeures % 24 + ":" + nbMinutes % 60 + ":" + nbSecondes % 60 + ":" + timeInMilis % 1000;
    }

    public static Integer safeParseInt(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException e)
        {
            return null;
        }
    }

    public static Vector2 fromMiddleToSpritePos(final Sprite sprite)
    {
        return new Vector2(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
    }

    public static void msg(final Object o, final String msg)
    {
        System.out.println(o.getClass().getSimpleName() + ": " + msg);
    }

    public static void msg(final Class<?> c, final String msg)
    {
        System.out.println(c.getSimpleName() + ": " + msg);
    }

    public static Vector2 getWorldCoordinates(final float pointerX, final float pointerY, final OrthographicCamera camera,
        final CustomScreen screen)
    {
        float mult;
        if (Gdx.graphics.getWidth() / Gdx.graphics.getHeight() > screen.getProportions())
        {
            mult = screen.getScreenHeight() / Gdx.graphics.getHeight() * camera.zoom;
        } else
        {
            mult = screen.getScreenWidth() / Gdx.graphics.getWidth() * camera.zoom;
        }
        return new Vector2(pointerX * mult, pointerY * mult);
    }

    public static <A> List<A> toMonoList(final A obj)
    {
        final List<A> l = new ArrayList<>();
        l.add(obj);
        return l;
    }

    public static float pow(final float v, final int power)
    {
        if(power < 0)
        {
            return 1/(innerPow(v,-power));
        }
        else
        {
            return innerPow(v,power);
        }
    }

    private static float innerPow(final float v, final int power)
    {
        if(power <= 1)
        {
            if(power == 1)
            {
                return v;
            }
            else
            {
                return 1f;
            }
        }
        if((power & 1) == 0)
        {
            final float x = innerPow(v,power >> 1);
            return x*x;
        }
        else
        {
            final float x = innerPow(v, power >> 1);
            return v*x*x;
        }
    }
}
