package fr.fogux.dedale.function;

public class FctScalingAndMoving extends FctScaling
{
	protected final double moveY;
	
	public FctScalingAndMoving(FunctionDouble baseFct, double INVERTEDscaleFactorX,double scaleFactorY, double moveY)
	{
		super(baseFct,INVERTEDscaleFactorX,scaleFactorY);
		this.moveY = moveY;
	}
	
	@Override
	public double getY(double x) 
	{
		return moveY + factorY*f.getY(x*factorX);
	}

}
