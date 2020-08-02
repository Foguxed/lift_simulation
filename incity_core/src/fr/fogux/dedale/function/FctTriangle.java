package fr.fogux.dedale.function;

public class FctTriangle implements FunctionDouble
{
    protected final FctPolynome f1;
    protected final FctPolynome f2;
    protected final double xrupt;
    
    
    public FctTriangle(double Xmin,double Xmax,double xPointe,double yPointe, double yMins)
    {
        f1 = FctPolynome.getDroite(Xmin, yMins, xPointe, yPointe);
        f2 = FctPolynome.getDroite(xPointe, yPointe, Xmax, yMins);
        xrupt = xPointe;
    }

    @Override
    public double getY(double x)
    {
    	if(x < xrupt)
    	{
    		return f1.getY(x);
    	}
    	else
    	{
    		return f2.getY(x);
    	}
    }
}
