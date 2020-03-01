package fr.fogux.lift_simulator.partition_creation;

import java.util.Random;

public class GroupProfile
{
    double totalVal;
    final double[] grpSizeProbas;
    protected final Random r;
    
    public GroupProfile(double[] grpSizeProbas)
    {
        totalVal = 0;
        this.grpSizeProbas = grpSizeProbas;
        for(int i = 0; i < grpSizeProbas.length; i ++)
        {
            totalVal += grpSizeProbas[i];
        }
        r = new Random();
    }
    
    public int getRandomGrpsSize() 
    {
        double val = r.nextDouble();
        for(int i = 0; i < grpSizeProbas.length; i ++)
        {
            val -= grpSizeProbas[i];
            if(val < 0)
            {
                return i+1;
            }
        }
        return grpSizeProbas.length-1;
    }
}
