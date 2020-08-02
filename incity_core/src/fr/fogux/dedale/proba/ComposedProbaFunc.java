package fr.fogux.dedale.proba;

import fr.fogux.dedale.function.FunctionDouble;

public class ComposedProbaFunc implements FunctionDouble
{
    protected final FunctionDouble baseFct;
    protected final double x1Split;
    protected final double proba0;
    
    
    
    /**
     * 
     * @param toDecomp definie sur 0 -> 1-proba0
     * @param proba0 largeur de la bande
     * @param xSplit x auquel toDecomp = 0
     */
    public ComposedProbaFunc(FunctionDouble toDecomp, double proba0, double xSplit)
    {
        baseFct = toDecomp;
        x1Split = xSplit;
        this.proba0 = proba0;
    }

    @Override
    public double getY(double x)
    {
        if(x < x1Split)
        {
            return baseFct.getY(x);
        }
        else
        {
            if(x < proba0 + x1Split)
            {
                return 0;
            }
            else
            {
                return baseFct.getY(x - proba0);
            }
        }
    }
}
