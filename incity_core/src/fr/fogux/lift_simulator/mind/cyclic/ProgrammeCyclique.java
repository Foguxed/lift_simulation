package fr.fogux.lift_simulator.mind.cyclic;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.mind.Programme;
import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class ProgrammeCyclique implements Programme
{    
    protected SortedChainedList aDesservirVersHaut = new SortedChainedList();
    protected SortedChainedList aDesservirVersBas = new SortedChainedList();
    
    protected int objectif = 0;
    //protected boolean actif = false;
    protected boolean bloque;
    
    protected boolean enMouvement;
    
    protected boolean vaVersHaut;
    
    protected int niveau;
    
    @Override
    public void appelExterieur(int niveau, boolean versLeHaut)
    {
        if(versLeHaut)
        {
           aDesservirVersHaut.addMaillon(niveau);
        }
        else
        {
           aDesservirVersBas.addMaillon(niveau);
        }
        InterfacePhysique.changerEtatBouton(niveau, true, versLeHaut);
        update();
    }
/**
 * 
 * 
 * @param haut
 * @return le prochain niveau objectif ou null si il faut changer de sens
 */
    protected Integer getProchain(boolean haut)
    {
        int premierNiveauPossible = niveau;
        if(enMouvement)
        {
            if(vaVersHaut)
            {
                premierNiveauPossible ++;
            }
            else
            {
                premierNiveauPossible --;
            }
        }
        if(haut)
        {
            Maillon suivantVersHaut = aDesservirVersHaut.getFirstEqualOrBiggerThan(premierNiveauPossible);
            println("suivantVersHaut " + suivantVersHaut);
            if(suivantVersHaut != null)
            {
                return suivantVersHaut.getValeur();
            }
            else
            {
                if(!aDesservirVersBas.isEmpty())
                {
                    return aDesservirVersBas.last.getValeur();
                }
                else
                {
                    return null;
                }
            }
        }
        else
        {
            Maillon suivantVersBas = aDesservirVersBas.getFirstEqualOrSmallerThan(premierNiveauPossible);
            println("suivantVersBas " + suivantVersBas);
            if(suivantVersBas != null)
            {
                return suivantVersBas.getValeur();
            }
            else
            {
                if(!aDesservirVersHaut.isEmpty())
                {
                    return aDesservirVersHaut.first.getValeur();
                }
                else
                {
                    return null;
                }
            }
        }
    }
    
    protected void update()
    {
        if(!bloque)
        {
            Integer val = getProchain(vaVersHaut);
            if(val == null)
            {
                val = getProchain(!vaVersHaut);
            }
            if(val != null)
            {
                objectif = val;
                if(!enMouvement)
                {
                    updateDirection();
                }
            }
        }
    }
    
    protected void updateDirection()
    {
        if(objectif > niveau)
        {
            InterfacePhysique.deplacerAscenseur(1, true);
            enMouvement = true;
            vaVersHaut = true;
        }
        else if(objectif < niveau)
        {
            InterfacePhysique.deplacerAscenseur(1, false);
            enMouvement = true;
            vaVersHaut = false;
        }
        else
        {
            bloque = true;
            ouvrir();
        }
    }
    
    @Override
    public void finDeTransfertDePersonnes(int niveau, int idAscenseur)
    {
        aDesservirVersBas.removeVal(niveau);
        aDesservirVersHaut.removeVal(niveau);
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
        aDesservirVersBas.addMaillon(niveau);
        aDesservirVersHaut.addMaillon(niveau);
        update();
    }

    @Override
    public void capteurDeNiveau(int idAscenseur, int niveau)
    {
        this.niveau = niveau;
        System.out.println(" on me dit niveau " + niveau + " mon obj c " + objectif);
        if(niveau == objectif)
        {
            bloque = true;
            InterfacePhysique.stoperAscenseur(1);
            enMouvement = false;
        }
    }
    
    @Override
    public void ascArrete(int idAscenseur)
    {
        System.out.println("asc arrete "+GestionnaireDeTaches.getInnerTime());
        ouvrir();
        //actif = false;
    }
    
    protected void ouvrir()
    {
        InterfacePhysique.ouvrirLesPortes(niveau, 1);
        InterfacePhysique.changerEtatBoutonAscenseur(1,niveau,false);
    }
    
    
    
    @Override
    public String getName()
    {
        return "standar_cyclique";
    }

    @Override
    public int getNbAscenseurs()
    {
        return 1;
    }
    
    public static void println(String msg)
    {
        InterfacePhysique.println(msg);
    }
    
    class Maillon{
       
          private int val;
        
          public Maillon suivant;
          public Maillon precedent;
          
          Maillon(int valeur)
          {
            val = valeur;
          }
          
          int getValeur()
          {
            return val;
          }
      };

      class SortedChainedList
      {
        private Maillon first;
        private Maillon last;
          
          
        public void insert(Maillon paramPrecedent, Maillon newMaillon)
        {
          if(paramPrecedent != null)
          {
            newMaillon.precedent = paramPrecedent;
            if(paramPrecedent.suivant != null)
            {
              newMaillon.suivant = paramPrecedent.suivant;
              paramPrecedent.suivant.precedent = newMaillon;
            }
            else
            {
              last =newMaillon;
            }
            paramPrecedent.suivant = newMaillon;
          }
          else
          {
            if(first == null)
            {
                last = newMaillon;
            }
            else
            {
                first.precedent = newMaillon;
                newMaillon.suivant = first;
            }
            first = newMaillon;
          }
        }

        void removeMaillon(Maillon toRemove)
        {
          if(toRemove.precedent != null)
          {
            toRemove.precedent.suivant = toRemove.suivant;
          }
          else
          {
            first = toRemove.suivant;
          }
          if(toRemove.suivant != null)
          {
            toRemove.suivant.precedent = toRemove.precedent;
          }
          else
          {
            last = toRemove.precedent;
          }
        }
        
        public Maillon getFirstEqualOrBiggerThan(int val)
        {
            Maillon retour = first;
            while(retour != null && val > retour.getValeur())
            {
               retour = retour.suivant;
            }
            return retour;
        }

        public Maillon getFirstEqualOrSmallerThan(int val)
        {
            Maillon retour = last;
            while(retour != null && val < retour.getValeur())
            {
              retour = retour.precedent;
            }
            return retour;
        }
        
        public boolean isEmpty()
        {
          return first == null;
        }
        
        public void addMaillon(int val)
        {
          Maillon mTrouve = getFirstEqualOrSmallerThan(val);
          if((mTrouve == null || mTrouve.getValeur() < val))
          {
            insert(mTrouve,new Maillon(val));
          }
        }

        void removeVal(int val)
        {
          Maillon mTrouve = getFirstEqualOrBiggerThan(val);
          if(mTrouve!= null && mTrouve.getValeur() == val)
          {
            removeMaillon(mTrouve);
          }
        }
      }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
    };
}