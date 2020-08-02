package fr.fogux.dedale.function;

import java.util.Arrays;

public class FctPolynome implements Integrable<FctPolynome>
{
    protected final double[] coefs;
    /**
     * 
     * @param coeficient des puissances ATTENTION ordonées de x^0 à x^(coefs.length-1), le plus grand coefficient doit être different de 0
     * donc par exemple c,b,a pour un polynome du second degre
     * 
     */

    
    public FctPolynome(final double ... coefsInput)
    {
        this(coefsInput,true);
    }
    
    /**
     * 
     * @param coefsInput
     * @param checkCoefsNull pour ne pas tenter de descendre le degre du polynome 
     */
    public FctPolynome(final double[] coefsInput,final boolean checkCoefsNull)
    {
        if(checkCoefsNull & coefsInput.length > 0)
        {
            int i = coefsInput.length-1;
            int nbNulls = 0;
            while(i < coefsInput.length &&  coefsInput[i] == 0)
            {
                i++;
                nbNulls ++;
            }
            if(nbNulls > 0)
            {
                this.coefs = new double[coefsInput.length - nbNulls];
                for(int j = 0; j < coefs.length; j ++)
                {
                    coefs[j] = coefsInput[j];
                }
            }
            else
            {
                this.coefs = coefsInput;
            }
        }
        else
        {
            this.coefs = coefsInput;
        }
    }
    
    public static FctPolynome getDroite(double xA, double yA, double xB, double yB)
    {
        double a = (yB-yA)/(xB-xA);
        return new FctPolynome(yA-a*xA,a);
    }
    
    
    protected double[] getCoefs()
    {
        return coefs;
    }
    
    @Override
    public double getY(double x)
    {
        double retour = 0;
        for(int i = 0; i < coefs.length; i ++)
        {
            retour = retour + coefs[i]*Math.pow(x, i);
        }
        return retour;
    }
    
    private FctPolynome shiftToPoint(double x, double y)
    {
        if(coefs.length>0)
        {
            coefs[0] += y - getY(x);
            return this;
        }
        else
        {
            return new FctPolynome(y);
        }
        
    }
    
    
    @Override
    public FctPolynome getIntegrale(double xRefPoint, double refPointY)
    {
        double[] newCoefs = new double[coefs.length+1];
        for(int i = 1; i < coefs.length+1; i ++)
        {
            newCoefs[i] = coefs[i-1]/i;
        }
        return new FctPolynome(newCoefs,false).shiftToPoint(xRefPoint,refPointY);
    }
    
    public void scale(double multiplicator)
    {
        for(int i = 0; i < coefs.length; i ++)
        {
            coefs[i] = coefs[i]*multiplicator;
        }
    }

    public FunctionDouble getResolution(boolean partieCroissante)
    {
        if(coefs.length == 0)
        {
            return new FctVerticale(0);
        }
        else if(coefs.length == 1)
        {
            return new FctVerticale(coefs[0]);
        }
        else if(coefs.length == 2)
        {
            return new FctPolynome(-coefs[0]/coefs[1],1/coefs[1]);
        }
        else if(coefs.length == 3)
        {
            return new FctResolSecondDegre(coefs[2],coefs[1],coefs[0],partieCroissante);
        }
        throw new IllegalStateException("la fonction n'est pas polynome du second degre");
    }
    
    public String toString()
    {
        return "FctPolynome " + Arrays.toString(coefs);
    }
}
