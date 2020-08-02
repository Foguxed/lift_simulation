package fr.fogux.dedale.function;


public class FctMultiPartIntegrable extends FctMultiPart<FctPolynome> implements Integrable<FctMultiPartIntegrable>
{

    public FctMultiPartIntegrable(double[] funcMins,FctPolynome[] functions,boolean checkFunctionVerticale)
    {
        super(funcMins, functions,checkFunctionVerticale);
    }

    @Override
    public FctMultiPartIntegrable getIntegrale(double xRefPoint, double refPointY)
    {
        FctPolynome[] newFcts = new FctPolynome[functions.length];
        int refPointFctIndex = getFunction(xRefPoint);
        newFcts[refPointFctIndex] = functions[refPointFctIndex].getIntegrale(xRefPoint, refPointY);
        for(int i = refPointFctIndex + 1; i < functions.length; i ++)
        {
            newFcts[i] = functions[i].getIntegrale(funcMins[i], newFcts[i-1].getY(funcMins[i]));
        }
        for(int i = refPointFctIndex -1 ; i > -1; i --)
        {
            newFcts[i] = functions[i].getIntegrale(funcMins[i+1], newFcts[i+1].getY(funcMins[i+1]));
        }
        return new FctMultiPartIntegrable(funcMins, newFcts,false);
    }
    

    public void scale(double multiplier)
    {
        for(int i = 0; i < functions.length; i ++)
        {
            functions[i].scale(multiplier);
        }
    }

    /**
     * 
     * @param points ORDONES SUR X
     * @return une fonction qui relie les points avec des droites
     */
    
    public static FctMultiPartIntegrable fromPoints(double[][] points)
    {
        int nbPointsMemeAbscisse = 0;
        for(int i = 0; i < points.length-1; i ++)
        {
        	if(points[i][0] >= points[i+1][0])
        	{
        		if(points[i][0] == points[i+1][0])
                {
                    nbPointsMemeAbscisse ++;
                }
        		else
        		{
        			throw new IllegalArgumentException("Les points decrivant une coube doivent imperativement etres odronnes par abscisse croissante,"
        				+ " probleme au point " + i );
        		}
        	}
            
        }
        
        double[] funcOrigin = new double[points.length-1 - nbPointsMemeAbscisse];
        FctPolynome[] fcts = new FctPolynome[points.length-1 - nbPointsMemeAbscisse];
        double a;
        int j = 0;
        for(int i = 0; i < points.length-1; i ++)
        {
            if(points[i][0] != points[i+1][0])
            {
                funcOrigin[j] = points[i][0];
                a = (points[i+1][1]-points[i][1])/(points[i+1][0]-points[i][0]);
                fcts[j] = new FctPolynome(points[i][1]-a*points[i][0],a);
                j ++;
            }
        }
        return new FctMultiPartIntegrable(funcOrigin,fcts,false);
    }
    
    public FctMultiPart<FunctionDouble> getResolutionSecondDegre(boolean partieCroissante)
    {
        FunctionDouble[] resolvedFcts = new FunctionDouble[functions.length];
        double[] newMins = new double[functions.length];
        for(int i = 0; i < functions.length; i ++)
        {
            resolvedFcts[i] = functions[i].getResolution(partieCroissante);
            newMins[i] = functions[i].getY(funcMins[i]);
        }
        return new FctMultiPart<FunctionDouble>(newMins,resolvedFcts,true);
    }

    public void pushUp(double value)
    {
        for(int i = 0; i < functions.length; i ++)
        {
            if(functions[i].getCoefs().length > 0)
            {
                functions[i].getCoefs()[0] += value;
            }
            else
            {
                functions[i] = new FctPolynome(value);
            }
        }
    }

}
