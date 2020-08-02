package fr.fogux.dedale.proba;

public class TemplateProbaCurve extends RawProbaCurve
{
	/**
	 * tableau de forme {{0.1,2},{0.5,3}};
	 * l'abscisse des points de cette courbe se répartie entre -1 et 1, l'ordonnée est > 0
	 */
	public TemplateProbaCurve(double[][] repartParPoints) 
	{
		super(repartParPoints);
		if(repartParPoints.length < 1 || 
				repartParPoints[0][0] < -1 ||
				repartParPoints[repartParPoints.length-1][0] > 1)
		{
			throw new IllegalArgumentException("Template curve should be defined between -1 and 1 values ");
		}
	}
}
