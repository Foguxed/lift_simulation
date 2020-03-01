package fr.fogux.lift_simulator.partition_creation;

public class EtageDestination
{
    protected final int etage;
    protected final double probabilite;
    
    public EtageDestination(int etage, double probabilite)
    {
        this.etage = etage;
        this.probabilite = probabilite;
    }
    
    public double getProbabilite()
    {
        return probabilite;
    }
    
    public int getEtage()
    {
        return etage;
    }
}
