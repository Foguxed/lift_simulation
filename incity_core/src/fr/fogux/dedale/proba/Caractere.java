package fr.fogux.dedale.proba;

public class Caractere<T extends Object>
{
    protected T type;
    protected ProbaReparter p;
    
    public Caractere(ProbaReparter reparter,T type)
    {
        this.p = reparter;
        this.type = type;
    }
    
    public T getType()
    {
        return type;
    }
    
    public ProbaReparter getReparter()
    {
        return p;
    }
    
    public String toString()
    {
    	return "type " + type + " probareparter " + p;
    }
}
