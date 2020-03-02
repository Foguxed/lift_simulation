package fr.fogux.lift_simulator.structure;

public abstract class Etage
{
    protected boolean hautAllume = false;
    protected boolean basAllume = false;
    protected final int num;

    public Etage(int niveau)
    {
        this.num = niveau;
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
