package fr.fogux.lift_simulator.structure;

public abstract class Etage
{
    protected boolean hautAllume;
    protected boolean basAllume;
    protected final int num;

    public Etage(int niveau)
    {
        this.num = niveau;
        hautAllume = false;
        basAllume = false;
    }
    
    public Etage(Etage shadowed)
    {
    	this.num = shadowed.num;
    	this.hautAllume = shadowed.hautAllume;
    	this.basAllume = shadowed.basAllume;
    }

    public int getNiveau()
    {
        return num;
    }

    public boolean boutonHautAllume()
    {
        return hautAllume;
    }

    public boolean boutonBasAllume()
    {
        return basAllume;
    }

    public abstract void arriveeDe(int nbPersonnes, int destination);

    public void setBoutonState(boolean allume, boolean boutonDuHaut)
    {
        if (boutonDuHaut)
        {
            hautAllume = allume;
        } else
        {
            basAllume = allume;
        }
    }

}
