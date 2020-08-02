package fr.fogux.dedale.function;

public class FctVerticale implements FunctionDouble
{
    protected final double x;
    
    public FctVerticale(double x)
    {
        this.x = x;
    }
    
    @Override
    public double getY(double x)
    {
        System.out.println("Warning FctVerticall.getY() has been called with x = " +x + " default value 0 returned" );
        return 0;
    }

}
