package fr.fogux.dedale.function;

public class FctScaling implements FunctionDouble
{
	protected final FunctionDouble f;
	protected final double factorX;
	protected final double factorY;
	
	
	public FctScaling(FunctionDouble baseFct, double INVERTEDscaleFactorX,double scaleFactorY)
	{
		this.f = baseFct;
		this.factorX = INVERTEDscaleFactorX;
		this.factorY = scaleFactorY;
	}
	
	@Override
	public double getY(double x) 
	{
		return factorY*f.getY(x*factorX);
	}

}
