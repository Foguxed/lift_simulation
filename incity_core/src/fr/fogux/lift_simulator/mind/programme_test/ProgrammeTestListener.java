package fr.fogux.lift_simulator.mind.programme_test;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.Programme;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class ProgrammeTestListener implements Programme
{
    protected int niveauAscenseurA = 0;
    protected int niveauAscenseurB = 0;
    protected Integer destiA = null;
    protected Integer destiB = null;
    protected boolean aBloque = false;
    protected boolean bBloque = false;
    
    
    protected List<Integer> destinations = new ArrayList<Integer>();
    protected final int IdA = 1;
    protected final int IdB = 2;
    
    @Override
    public String getName()
    {
        return "Test";
    }
    
    public void appelExterieur(int niveau, boolean versLeHaut)
    {
        InterfacePhysique.changerEtatBouton(niveau, true, versLeHaut);
        destinations.add(niveau);
        System.out.println("asc appeler");
        updateComportement();
    }
    
    
    
    protected void updateComportement()
    {
        if(destiA == null && !destinations.isEmpty())
        {
            destiA = destinations.get(0);
        }
        
        if(destiB == null && destiA != null)
        {
            destiB = destiA;
        }
        
        if(destiA != null && !aBloque)
        {
            InterfacePhysique.deplacerAscenseur(IdA,niveauAscenseurA < destiA);
        }
        if(destiB != null && !bBloque)
        {
            InterfacePhysique.deplacerAscenseur(IdB,niveauAscenseurB < destiB);
        }
        System.out.println("destinations2: " + destinations.toString());
    }
    
    public void finDeTransfertDePersonnes(int niveau,int idAscenseur)
    {
        InterfacePhysique.fermerLesPortes(niveau,idAscenseur);
    }
    
    public void ascenseurFerme(int idAscenseur)
    {
        System.out.println("asc ferme");
        if(idAscenseur == IdA)
        {
            aBloque = false;
        }
        else if(idAscenseur == IdB)
        {
            bBloque = false;
        }
        updateComportement();
        
    }
    
    public void appelInterieur(int niveau,int idAscenseur)
    {
        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur, niveau, true);//TODO pas si deja allume?
        if(destiA == null)
        {
            destiA = niveau;
        }
        else
        {
            destinations.add(niveau);
        }
    }
    
    public void capteurDeNiveau(int idAscenseur,int niveau)
    {
        if(idAscenseur == IdA)
        {
            niveauAscenseurA = niveau;
            //System.out.println("niveau " + niveau + " destination " + destiA);
            
            if(niveau == (int)destiA)
            {
                destinations.removeIf(val-> val == niveau);
                destiA = null;
                aBloque = true;
                InterfacePhysique.stoperAscenseur(IdA);
                InterfacePhysique.ouvrirLesPortes(niveau, IdA);
                InterfacePhysique.changerEtatBouton(niveau, false, true);
                InterfacePhysique.changerEtatBouton(niveau, false, false);
                InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur, niveau, false);
            }
        }
        else if(idAscenseur == IdB)
        {
            niveauAscenseurB = niveau;
            if(destiB != null && niveau == destiB)
            {
                destiB = null;
                bBloque = true;
                InterfacePhysique.stoperAscenseur(IdB);
                InterfacePhysique.ouvrirLesPortes(niveau, IdB);
                InterfacePhysique.changerEtatBouton(niveau, false, true);
                InterfacePhysique.changerEtatBouton(niveau, false, false);
                InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur, niveau, false);
            }
        }
    }
    @Override
    public void ascArrete(int idAscenseur)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getNbAscenseurs()
    {
        return 2;
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
    }

    
}
