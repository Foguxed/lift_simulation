package fr.fogux.dedale.proba;

import fr.fogux.dedale.function.FunctionDouble;
import fr.fogux.dedale.repartiteur.Intervalle;

public class Event extends Intervalle implements FunctionDouble
{
    protected final FunctionDouble probaFct;
    
    public Event(double min, double max,FunctionDouble probaFct)
    {
        super(min,max);
        this.probaFct = probaFct;
    }

    @Override
    public double getY(double absoluteRarety)
    {
        return probaFct.getY(absoluteRarety);
    }
}
