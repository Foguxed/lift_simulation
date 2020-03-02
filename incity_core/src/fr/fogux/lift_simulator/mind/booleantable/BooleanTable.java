package fr.fogux.lift_simulator.mind.booleantable;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.mind.Programme;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class BooleanTable implements Programme
{
    boolean[] exterieur;
    boolean[] interieur;
    int etageMin;
    boolean enAttente = true;
    
    
    protected int objectif = 0;
    protected int niveau;
    
    @Override
    public void init()
    {
        etageMin = InterfacePhysique.getEtageMin();
        exterieur = new boolean[InterfacePhysique.getEtageMaxNonInclu()-InterfacePhysique.getEtageMin()];
        interieur = new boolean[exterieur.length];
    }
    
    @Override
    public void appelExterieur(int niveau, boolean versLeHaut)
    {
        exterieur[niveau - etageMin] = true;
        InterfacePhysique.changerEtatBouton(niveau, true, versLeHaut);
        update();
    }

    protected void update()
    {
        if(enAttente)
        {
            boolean continuerRechercheEtage = true;
            int n = 0;
            while(continuerRechercheEtage && n < interieur.length)
            {
              if(interieur[n] == true)
              {
                deplacerVers(n);
                continuerRechercheEtage = false;
              }
              n++;
            }
            
            int x = 0;
            while(continuerRechercheEtage && x < exterieur.length)
            {
              if(exterieur[x] == true)
              {
                deplacerVers(x);
                continuerRechercheEtage = false;
              }
              x++;
            }
        }
    }
    
    protected void deplacerVers(int tableauIndex)
    {
        objectif = tableauIndex + etageMin;
        InterfacePhysique.println("deplacervers " + tableauIndex);
        updateDirection();
    }
    
    protected void updateDirection()
    {
        enAttente = false;
        if(objectif > niveau)
        {
            InterfacePhysique.deplacerAscenseur(1, true);
        }
        else if(objectif < niveau)
        {
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
        exterieur[niveau - etageMin] = false;
        interieur[niveau - etageMin] = false;
        InterfacePhysique.changerEtatBouton(niveau,false,true);
        InterfacePhysique.changerEtatBouton(niveau,false,false);
        InterfacePhysique.fermerLesPortes(niveau, idAscenseur);
    }

    @Override
    public void ascenseurFerme(int idAscenseur)
    {
        enAttente = true;
        update();
    }

    @Override
    public void appelInterieur(int niveau, int idAscenseur)
    {
        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur,niveau,true);
        interieur[niveau - etageMin] = true;
        update();
    }

    @Override
    public void capteurDeNiveau(int idAscenseur, int niveau)
    {
        this.niveau = niveau;
        System.out.println(" on me dit niveau " + niveau + " mon obj c " + objectif);
        if(niveau == objectif)
        {
            InterfacePhysique.stoperAscenseur(1);
        }
    }
    
    @Override
    public void ascArrete(int idAscenseur)
    {
        System.out.println("asc arrete "+GestionnaireDeTaches.getInnerTime());
        InterfacePhysique.ouvrirLesPortes(niveau, idAscenseur);
        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur,niveau,false);
    }
    
    protected void ouvrir()
    {
        InterfacePhysique.ouvrirLesPortes(niveau, 1);
        InterfacePhysique.changerEtatBoutonAscenseur(1,niveau,false);
    }
    
    @Override
    public String getName()
    {
        return "booolean_table";
    }

    @Override
    public int getNbAscenseurs()
    {
        return 1;
    }
}
