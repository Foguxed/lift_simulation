package fr.fogux.dedale.proba;

import fr.fogux.dedale.function.FctMultiPartIntegrable;
import fr.fogux.dedale.function.FunctionDouble;

public class RawProbaCurve
{
	protected final FunctionDouble f;
	protected final double x0;
	
	public RawProbaCurve(final double [][] repartParPoints)
	{
		this(repartParPoints,1);
	}
    
	public RawProbaCurve(final double [][] repartParPoints, double maxDefinedX)
	{
		for(int i = 0 ; i < repartParPoints.length; i ++)
        {
            if(repartParPoints[i][1] < 0)
            {
                throw new IllegalArgumentException("Expliquez moi ce que c'est qu'une probabilité négative");
            }
        }
		FctMultiPartIntegrable integrale = FctMultiPartIntegrable.fromPoints(repartParPoints).getIntegrale(repartParPoints[0][0],0);
		integrale.scale(maxDefinedX/integrale.getY(repartParPoints[repartParPoints.length-1][0]));
        f = integrale.getResolutionSecondDegre(true);
        x0 = integrale.getY(0);
	}
	
	public boolean isResultPositiveOnly()
	{
		return f.getY(0) >= 0;
	}
	
	public double getXWereYEqual0() 
	{
		return x0;
	}
	
	public FunctionDouble getFunction()
	{
		return f;
	}
}
