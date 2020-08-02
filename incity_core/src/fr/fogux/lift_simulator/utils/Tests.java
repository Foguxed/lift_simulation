package fr.fogux.lift_simulator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import fr.fogux.dedale.function.FunctionDouble;
import fr.fogux.dedale.proba.ProbaReparter;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscDeplacementFunc;
import fr.fogux.lift_simulator.structure.AscSoftDep;
import fr.fogux.lift_simulator.structure.AscState;
import fr.fogux.lift_simulator.structure.DepPlannifier;
import fr.fogux.lift_simulator.structure.DeplacementFunc;
import fr.fogux.lift_simulator.structure.Polynome;

public class Tests
{
	private static void tests()
	{
		 /*final int b = (int)Math.floor(-2.3d);
        System.out.println(b);*/
        /*
         *  final ConfigSimu c = new ConfigSimu(new DataTagCompound("{acceleration:1.2000001E-7,dureePortes:2000,ascenseurSpeed:0.0023,capaciteAsc:5,repartAscenseurs:{{val:2},{val:2},{val:2},{val:2}},margeInterAsc:0.8,dureeEntreeSortiePers:1200,niveauMin:-3,niveauMax:20}"));
        final long t0 = 0;
        final float xi = 0.00000006f;
        final float vi = -0.000000012f;
        final float xf = 12f;
       
	*/
    	//final ConfigSimu c = new ConfigSimu(new DataTagCompound("{acceleration:1.2000001E-7,dureePortes:2000,ascenseurSpeed:0.0023,capaciteAsc:5,repartAscenseurs:{{val:2},{val:2},{val:2},{val:2}},margeInterAsc:0.8,dureeEntreeSortiePers:1200,niveauMin:-3,niveauMax:20}"));
    	
    	final ConfigSimu c = new ConfigSimu(new DataTagCompound("{acceleration:0.0000003f,dureePortes:2000,ascenseurSpeed:0.01f,capaciteAsc:5,repartAscenseurs:{{val:2},{val:2},{val:2},{val:2}},margeInterAsc:0.8,dureeEntreeSortiePers:1200,niveauMin:-3,niveauMax:20}"));
    	
        final long t0 = 0;
        final float xi = -350f;
        System.out.println("maxspeed " + c.getAscenseurSpeed());
        final float vi = 0.003f;
       
        
		final float vLim = 0.01f;
		final float a = 0.0000003f;
		
		final Polynome p = new Polynome(10000, 20, 0, a);

    	final AscSoftDep softDep = AscDeplacementFunc.getDeplacementTangeant(c, t0, xi, vi, p);
    	System.out.println(softDep);
		tester(p,0,100000,200);
		AscDeplacementFunc fc = AscDeplacementFunc.getDeplacementFunc(c, t0, xi, vi, softDep);
    	/*final AscSoftDep softDep = AscTripleDeplacementFunc.getDeplacementTangeant(t0, xi, vi, a, vLim , p);
    	final AscTripleDeplacementFunc fc = new AscTripleDeplacementFunc(t0, xi, vi, a, vLim, softDep);
    	*/
    	//final AscDoubleDeplacementFunc fc = new AscDoubleDeplacementFunc(t0, -xi, -vi, -a, softDep);
    	
    	System.out.println("FC ");
    	tester(fc,0,100000,200);
    	System.out.println(fc);
        //tester(fc, -60*1000,60*1000,100);

	}
    public static void main (final String[] arg) {

       //tests();
    	//testDesAscDepplannifiers();
    	//testDesAscDepplannifiers();
    	testTreeMapSpeed();
    }
    
    private static long fromMinutes(long minutes)
    {
    	return 60*1000*minutes;
    }
    
    private static void testProbas()
    {
    	double[][] repart = { {0d,0d}, {fromMinutes(5),0.2}, {fromMinutes(10),0.3}, {fromMinutes(12),1d}, {fromMinutes(13),0.5d}, {fromMinutes(17),0.4},{fromMinutes(25),0}};
    	
    	Random r = new Random();
    	ProbaReparter reparter = new ProbaReparter(r,repart);
    	//tester(FctMultiPartIntegrable.fromPoints(repart),0,fromMinutes(25),200);
    	FunctionDouble f = reparter.getFct();
    	//tester(f, 0, 1, 200);
    	double y = f.getY(0.30);
    	//System.out.println(y);
    	//System.out.println(reparter.getFct());
    	
    	//&étesterReparter(reparter,10000);
    	
    }
    
    public static void testerReparter(ProbaReparter reparter, long nbEssais)
    {
    	for(int i = 0; i < nbEssais; i ++)
    	{
    		System.out.println((long)reparter.getRandomValue());
    	}
    }
    
    private static void debugRpSimpleAlgo()
    {
    	final ConfigSimu c = new ConfigSimu(new DataTagCompound("{acceleration:1.2000001E-7,dureePortes:2000,ascenseurSpeed:0.0023,capaciteAsc:5,repartAscenseurs:{{val:2},{val:2},{val:2},{val:2}},margeInterAsc:0.8,dureeEntreeSortiePers:1200,niveauMin:-3,niveauMax:20}"));
    	final DataTagCompound ascUpPlannifier = new DataTagCompound("{listeEtapes:{{t:42299,v:0.0,x:4.0},{t:48072,v:0.0,x:5.0,softDepType:1}}}");
    	final DepPlannifier ascUp = new DepPlannifier(c, null);
    	ascUp.acceptCompound(ascUpPlannifier);
    	
    	final DataTagCompound ascDownPlannifier = new DataTagCompound("{listeEtapes:{{t:38049,v:-2.4612003E-4,x:2.252869},{t:46423,v:0.0,x:3.2,softDepType:1}}}");
    	final DepPlannifier ascDown = new DepPlannifier(c, null);
    	ascDown.acceptCompound(ascDownPlannifier);;
    	
    	ascDown.initiateMovement(42570, 4.0f, ascUp, null);
    	
    	System.out.println(ascDown);
    	
    	final long nbPoints = 200l;
    	float xMin = 42570;
    	float xMax = 60000;
    	
    	tester(ascUp,xMin,xMax,nbPoints);
    	tester(ascDown,xMin,xMax,nbPoints);
    }
    
    private static void testDesAscDepplannifiers()
    {
    	final ConfigSimu c = new ConfigSimu(new DataTagCompound("{acceleration:5.0000001E-7,dureePortes:2000,ascenseurSpeed:0.02,capaciteAsc:5,repartAscenseurs:{{val:2},{val:2},{val:2},{val:2}},"
    			+ "margeInterAsc:0.8,dureeEntreeSortiePers:1200,niveauMin:-3,niveauMax:20}"));
    	final DepPlannifier asc0 = new DepPlannifier(c,new AscState(0,0,-0.0001f));
    	final DepPlannifier asc1 = new DepPlannifier(c,new AscState(0,-1.1f,0));
    	final DepPlannifier asc2 = new DepPlannifier(c,new AscState(0,-60f,0f));
    	System.out.println("Step2");
    	System.out.println(asc2);
    	asc1.initiateMovement(0, -59.15f, asc0, asc2);
    	
    	
    	System.out.println("CPARTI");
    	final double xMin = 0;
    	final double xMax = 40000;
    	final long nbPoints = 100;
    	

    	asc0.initiateMovement(5000, -58f, null, asc1);
    	
    	
    	
    	asc2.initiateMovement(13800, -150f, asc1,null);

    	final long t2 = 14000L;
    	tester(asc0, xMin, t2, nbPoints);
    	tester(asc1, xMin, t2, nbPoints);
    	tester(asc2, xMin, t2, nbPoints);
    	
    	
    	asc1.initiateMovement(t2, -140.15f, asc0, asc2);

    	asc0.initiateMovement(t2, -135f, null ,asc1);
    	
    	//tester(asc0, t2, xMax, nbPoints);
    	tester(asc1, t2, xMax, nbPoints);
    	tester(asc2, t2, xMax, nbPoints);
    	
    	
    	tester(asc0, t2, xMax, nbPoints);
    	System.out.println(asc0);
    }
    
    public static void tester(final DeplacementFunc fct,final double xMin,final double xMax,final long nbPoints)
    {
        final Fct fctTest = new Fct() {

            @Override
            public double getY(final double x)
            {
                return fct.getX((long) x);
            }

        };
        tester(fctTest, xMin,xMax,nbPoints);
    }
    
    public static void testTreeMapSpeed()
    {
    	Random r = new Random();
    	

		Map<Long,Integer> map = new HashMap<Long,Integer>();
		List<Long> longList = new ArrayList<>();
		List<Integer> intList = new ArrayList<>();
    	for(int i = 0; i < 1000000; i ++)
    	{
    		long l = r.nextLong();
    		int inte = r.nextInt();
    		map.put(l, inte);
    		longList.add(l);
    		intList.add(inte);
    	}
    	TreeMap<Long, Integer> tree1 = new TreeMap<>();
    	TreeMap<Long, Integer> tree2 = new TreeMap<>();
    	long ti = System.currentTimeMillis();
    	tree2.putAll(map);
    	System.out.println(System.currentTimeMillis() - ti);
    	ti = System.currentTimeMillis();
    	for(int j = 0 ; j < longList.size(); j ++)
    	{
    		tree1.put(longList.get(j), intList.get(j));
    	}
    	System.out.println(System.currentTimeMillis() - ti);
    }
    
    public static void tester(final FunctionDouble fct,final double xMin,final double xMax,final long nbPoints)
    {
        final Fct fctTest = new Fct() {

            @Override
            public double getY(final double x)
            {
                return fct.getY(x);
            }

        };
        tester(fctTest, xMin,xMax,nbPoints);
    }
    
    public static void tester(final DepPlannifier fct,final double xMin,final double xMax,final long nbPoints)
    {
        final Fct fctTest = new Fct() {

            @Override
            public double getY(final double x)
            {
                return fct.safeGetX((long) x);
            }

        };
        tester(fctTest, xMin,xMax,nbPoints);
    }

    public static void tester(final Fct fct,final double xMin,final double xMax,final long nbPoints)
    {
        final double ecart = (xMax-xMin)/nbPoints;
        double v;
        for(double d = xMin; d <= xMax; d += ecart)
        {
            v = fct.getY(d);
            System.out.println(d + " \t " + (Double.isNaN(v)?"":v));
        }
    }
}
