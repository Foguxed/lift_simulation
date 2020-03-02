package fr.fogux.lift_simulator.physic;

public class TimeConfig
{
    private static float hauteurNiveau()
    {
        return 2.50f;// 2m 50
    }

    public static long getDureeSortieEntreePersonne()
    {
        
    	return 600; //1200
    }

    public static float getAscenseurSpeed()
    {
        return ((1.2f / hauteurNiveau()) / 1000f); // niveau par milisseconde
    }

    public static float getAscenseurAcceleration()
    {
        return (0.30f / hauteurNiveau()) / (1000f * 1000f); // niveau par milis au carre
    }

    public static float getAscenseurDecelleration()
    {
        return (0.30f / hauteurNiveau()) / (1000f * 1000f); // niveau par sec au carre
    }

    public static long getDureePortes()
    {
        return 2000;
    }

    public static int nbPersMaxAscenseur()
    {
        return 5;
    }
}
