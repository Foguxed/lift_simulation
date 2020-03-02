package fr.fogux.lift_simulator.mind.basic;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.mind.Programme;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class ProgrammeBasique implements Programme
{
    protected List<Integer> aDesservir = new ArrayList<Integer>();
    protected int objectif = 0;
    protected boolean actif = false;
    protected boolean bloque = false;
    protected int niveauActuel;
    
    @Override
    public void appelExterieur(int niveau, boolean versLeHaut)
    {
        aDesservir.add(niveau);
        InterfacePhysique.changerEtatBouton(niveau, true, versLeHaut);
        update();
    }

    protected void update()
    {
        if(!bloque && !actif && !aDesservir.isEmpty())
        {
            objectif = aDesservir.get(0);
            updateDirection();
        }
    }
    
    protected void updateDirection()
    {
        if(objectif > niveauActuel)
        {
            actif = true;
            InterfacePhysique.deplacerAscenseur(1, true);
        }
        else if(objectif < niveauActuel)
        {
            actif = true;
            InterfacePhysique.deplacerAscenseur(1, false);
        }
        else
        {
            ouvrir();
        }
    }
    
    @Override
    public void finDeTransfertDePersonnes(int niveau, int idAscenseur)
    {
        aDesservir.removeIf(i -> i == niveau);
        InterfacePhysique.changerEtatBouton(niveau,false,true);
        InterfacePhysique.changerEtatBouton(niveau,false,false);
        InterfacePhysique.fermerLesPortes(niveau, idAscenseur);
    }

    @Override
    public void ascenseurFerme(int idAscenseur)
    {
        bloque = false;
        
        update();
    }

    @Override
    public void appelInterieur(int niveau, int idAscenseur)
    {
        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur,niveau,true);
        aDesservir.add(niveau);
        update();
    }

    @Override
    public void capteurDeNiveau(int idAscenseur, int niveau)
    {
        this.niveauActuel = niveau;
        if(niveau == objectif)
        {
            bloque = true;
            InterfacePhysique.stoperAscenseur(1);
            actif = false;
        }
    }
    
    @Override
    public void ascArrete(int idAscenseur)
    {
        System.out.println("asc arrete "+GestionnaireDeTaches.getInnerTime());
        InterfacePhysique.ouvrirLesPortes(niveauActuel, idAscenseur);
        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur,niveauActuel,false);
        actif = false;
    }
    
    protected void ouvrir()
    {
        InterfacePhysique.ouvrirLesPortes(niveauActuel, 1);
        InterfacePhysique.changerEtatBoutonAscenseur(1,niveauActuel,false);
        bloque = true;
    }
    
    @Override
    public String getName()
    {
        return "basique";
    }

    @Override
    public int getNbAscenseurs()
    {
        return 1;
    }

    @Override
    public void init()
    {
        
    }

}
