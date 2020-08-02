package fr.fogux.dedale.proba;

import fr.fogux.dedale.function.FunctionDouble;

public  class FonctionWithProba<T extends FunctionDouble>
{
    protected final T f;
    protected final double p;
    
    public FonctionWithProba(T func, double proba)
    {
        this.f = func;
        this.p = proba;
    }
    
    public String toString()
    {
    	return "proba " + p + " func " + f;
    }
}
