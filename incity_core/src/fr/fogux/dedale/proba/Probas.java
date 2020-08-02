package fr.fogux.dedale.proba;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import fr.fogux.dedale.function.FunctionDouble;

public class Probas
{
    protected static final Random r = new Random();
    public static <T extends FunctionDouble> T getRandom(final double x, Collection<T> fcts)
    {
        if(fcts.isEmpty())
        {
            return null;
        }
        List<FonctionWithProba<T>> tempList = new ArrayList<FonctionWithProba<T>>();
        double total = 0;
        double temp;
        for (T fct : fcts)
        {
        	
        	temp = fct.getY(x);
        	total += temp;
        	tempList.add(new FonctionWithProba<T>(fct, temp));
        }
        double radomizedDouble = r.nextDouble();
        double randomD = radomizedDouble*total;
        for(FonctionWithProba<T> f : tempList)
        {
            randomD = randomD - f.p;
            if(randomD < 0)
            {
                return f.f;
            }
        }
        return tempList.get(tempList.size()).f;
    }
    
    public static double nextDouble()
    {
    	return r.nextDouble();
    }
    
    public static void checkProba(double proba)
    {
    	if(proba > 1 | proba < 0)
        {
            throw new IllegalArgumentException("proba " + proba + " non sous forme de probabilité");
        }
    }
}
