package fr.fogux.dedale.function;

public class FunctionPart<F extends FunctionDouble> implements Comparable<FunctionPart<?>>
{
    protected final F f;
    protected final double xMin;
    
    public FunctionPart(double xMin, F f)
    {
        this.xMin = xMin;
        this.f = f;
    }

    @Override
    public int compareTo(FunctionPart<?> o)
    {
        return Double.compare(xMin,o.xMin);
    }
    
    public F getFunction()
    {
        return f;
    }
}
