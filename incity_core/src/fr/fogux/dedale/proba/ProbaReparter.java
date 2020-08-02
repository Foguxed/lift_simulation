package fr.fogux.dedale.proba;

import java.util.Random;

import fr.fogux.dedale.function.FctScaling;
import fr.fogux.dedale.function.FctScalingAndMoving;
import fr.fogux.dedale.function.FunctionDouble;

public class ProbaReparter implements FunctionDouble
{
    protected final FunctionDouble fct;
    protected final Random r;
    
    public ProbaReparter(Random r,TemplateProbaCurve template, double scale, final double additionnal0proba)
    {
        Probas.checkProba(additionnal0proba);
        this.r = r;
    	fct = new ComposedProbaFunc(new FctScaling(template.getFunction(), 1/(1-additionnal0proba), scale), additionnal0proba,
    			template.getXWereYEqual0() * (1-additionnal0proba));
    }
    
    public ProbaReparter(Random r,TemplateProbaCurve template,final double minValue,final double maxValue, final double additionnal0proba)
    {
    	this.r = r;
    	if(minValue >= maxValue)
    	{
    		throw new IllegalArgumentException();
    	}
        Probas.checkProba(additionnal0proba);
        double scaleY = (maxValue - minValue)/(template.getFunction().getY(1) - template.getFunction().getY(0));
    	fct = new ComposedProbaFunc(new FctScalingAndMoving(template.getFunction(), 1/(1-additionnal0proba), scaleY,
    			minValue - template.getFunction().getY(0)*scaleY),additionnal0proba,template.getXWereYEqual0() * (1-additionnal0proba));
    }
    
    public ProbaReparter(Random r, double[][] pointsDeLaCourbe)
    {
    	this.r = r;
        fct = new RawProbaCurve(pointsDeLaCourbe).getFunction();
    }
    
    public ProbaReparter(Random r, double[][] pointsDeLaCourbe,double additionnal0proba)
    {
    	this.r = r;
        Probas.checkProba(additionnal0proba);
        RawProbaCurve curve = new RawProbaCurve(pointsDeLaCourbe, 1 - additionnal0proba);
        fct = new ComposedProbaFunc(curve.getFunction(), additionnal0proba, curve.getXWereYEqual0());
    }
    
    
    
    public double getRandomValue()
    {
        return fct.getY(r.nextDouble());
    }

    @Override
    public double getY(double xBetween0and1)
    {
        return fct.getY(xBetween0and1);
    }
    
    public FunctionDouble getFct()
    {
    	return fct;
    }
    
    public String toString()
    {
    	//Utils.tester(fct,0,1,50);
    	return "probareparter fct " + fct;
    }
    
}
