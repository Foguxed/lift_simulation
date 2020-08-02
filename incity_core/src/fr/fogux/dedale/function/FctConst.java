package fr.fogux.dedale.function;

public class FctConst implements FunctionDouble
{
	protected final double val;
	public FctConst(double val)
	{
		this.val = val;
	}
	
	@Override
	public double getY(double x)
	{
		// TODO Auto-generated method stub
		return val;
	}

}
