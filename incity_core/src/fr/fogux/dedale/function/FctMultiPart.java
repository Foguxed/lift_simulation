package fr.fogux.dedale.function;

public class FctMultiPart<F extends FunctionDouble> implements FunctionDouble
{
    protected final double[] funcMins;
    protected final F[] functions;
    
    /**
     * 
     * @param funcOrigins lew orginies x des fonctions souhaitées.
     * @param functions2
     * @param checkVerticalFunction si les functions peuvent contenir des FctVerticale et que les origines ne sont pas bien ajustées
     */
    
    public FctMultiPart(double[] funcOrigins, F[] functions, boolean checkVerticalFunction)
    {
        if(funcOrigins.length != functions.length)
        {
            throw new IllegalArgumentException();
        }
        if(checkVerticalFunction)
        {
            for(int i = 0; i < functions.length-2; i ++)
            {
                if(functions[i] instanceof FctVerticale)
                {
                    funcOrigins[i+1] = funcOrigins[i];
                }
            }
        }
        
        this.funcMins = funcOrigins;
        this.functions = functions;
        
        
    }
    
    
    
    @Override
    public double getY(double x)
    {
        return functions[getFunction(x)].getY(x);
    }
    
    protected int getFunction(double x)
    {
        return Utils.rechercheDichotomiqueBornee(funcMins, x);
    }
    
    public String toString()
    {
        String str = "FctMultiPart \n";
        for(int i = 0; i < funcMins.length; i ++)
        {
            str = str + "xMin: " + funcMins[i] + " func: " + functions[i] + " ,\n";
        }
        return str;
    }
}
