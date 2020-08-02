package fr.fogux.dedale.function;

public class FctComposee implements FunctionDouble
{
    /**
     * c'est la fonction f(g(x))
     * @param f
     * @param g
     */
    protected final FunctionDouble f;
    protected final FunctionDouble g;
    
    public FctComposee(FunctionDouble f, FunctionDouble g)
    {
        this.f = f;
        this.g = g;
    }

    @Override
    public double getY(double x)
    {
        return f.getY(g.getY(x));
    }
}
