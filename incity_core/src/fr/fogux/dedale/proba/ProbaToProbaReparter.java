package fr.fogux.dedale.proba;

import java.util.Random;

public class ProbaToProbaReparter extends ProbaReparter
{
	/**
	 * 
	 * 
	 * Un proba reparter qui renvoie forcement une valeur entre 0 et 1
	 * 
	 * @param tempalteCurve le template (si entre-1 et 1 , recentré sur 0.5)
	 * @param additionnal0proba
	 */
	public ProbaToProbaReparter(Random r,TemplateProbaCurve tempalteCurve)
	{
		super(r,tempalteCurve,0,1,0);
	}

}
