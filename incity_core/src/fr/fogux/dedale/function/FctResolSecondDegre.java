package fr.fogux.dedale.function;

public class FctResolSecondDegre implements FunctionDouble
{
    protected double moinsbsur2a;
    protected double valInterieurRacine;
    protected double facteurRacine;
    protected boolean apositif;
    public FctResolSecondDegre(double a, double b, double c,boolean partieCroissante)
    {
        moinsbsur2a = -0.5*b/a;
        apositif =  a >= 0;
        facteurRacine = (apositif == partieCroissante? 1 : -1)/Math.sqrt(Math.abs(a));
        valInterieurRacine = (apositif ? 1 : -1) * ((b*b*0.25/a) - c);
    }

    @Override
    public double getY(double x)
    {
        if(!apositif)
        {
            x = -x;
        }
        return moinsbsur2a + facteurRacine*cheatedSqrt(valInterieurRacine + x);// pour eviter les arrondis, mais ça va donner une valeur fixe (la derniere valeur avant de ne pas être defini)
    }
    
    private double cheatedSqrt(double val)
    {
    	if(val < 0)
    	{
    		if(val < -0.1)
    		{
    			System.out.println("FctResolSecondDegree vient de supposer que " + val + " etait un mauvais arrondi pour 0");
    		}
    		return 0; // et vos soucis disparaissent.
    	}
    	else
    	{
    		return Math.sqrt(val);
    	}
    }
    
    public void scale(double multiplicator)
    {
        moinsbsur2a *= multiplicator;
        facteurRacine *= multiplicator;
    }
}
