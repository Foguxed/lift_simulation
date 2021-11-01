package fr.fogux.dedale.function;

public class FctConst implements FunctionDouble
{
    protected final double val;
    public FctConst(final double val)
    {
        this.val = val;
    }

    @Override
    public double getY(final double x)
    {
        return val;
    }

}
