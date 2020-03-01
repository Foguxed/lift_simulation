package fr.fogux.lift_simulator.evenements.animation;

import fr.fogux.lift_simulator.GestionnaireDeTaches;

public abstract class EvenementChangementEtat extends EvenementAnimationOnly
{
    
    public EvenementChangementEtat()
    {
        super();
    }
    
    public EvenementChangementEtat(long time)
    {
        super(time);
    }
    @Override
    public void visuRun()
    {
        if(GestionnaireDeTaches.marcheArriere())
        {
            visuRunetatPrecedent();
        }
        else
        {
            visuRunetatSuivant();
        }
    }
    
    public abstract void visuRunetatSuivant();
    
    public abstract void visuRunetatPrecedent();
}
