package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import java.util.List;

public class FloatGradParam
{
    public float value;
    public float gradScale;
    public float deriveeCalcScale;
    public long tempCalcValueDesc;
    public long tempCalcValueUp;

    public FloatGradParam(final float positiveValue)
    {
        this(positiveValue, 0.02f* positiveValue,0.02f*positiveValue);
    }
    public FloatGradParam(final float value, final float gradScale,final float deriveeCalcScale)
    {
        this.value = value;
        this.gradScale = gradScale;
        this.deriveeCalcScale = deriveeCalcScale;
    }

    public void goToDeriveeCalcValue(boolean positive)
    {
    	if(positive)
    	{
    		value = value + deriveeCalcScale;
    	}
    	else
    	{
    		value = value - deriveeCalcScale;
    	}
    }

    public void backToValue(float v)
    {
        value = v;
    }

    public void deplacer(final boolean positif)
    {
    	if(positif)
    	{
    		value = value +gradScale;
    	}
    	else
    	{
    		value = value -gradScale;
    	}
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    public static String listToString(final List<FloatGradParam> list)
    {
        String str = "";
        for(final FloatGradParam param : list)
        {
            str += param.toString() + ",";
        }
        return str;
    }
}
