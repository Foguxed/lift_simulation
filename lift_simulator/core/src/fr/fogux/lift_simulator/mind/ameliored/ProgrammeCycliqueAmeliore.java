package fr.fogux.lift_simulator.mind.ameliored;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.mind.Programme;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.TimeConfig;

public class ProgrammeCycliqueAmeliore implements Programme
{    
    protected SortedSet<Integer> aDesservirVersHaut = new TreeSet<Integer>();
    protected SortedSet<Integer> aDesservirVersBas = new TreeSet<Integer>();
    protected SortedSet<Integer> appeleDepuisInterieur = new TreeSet<Integer>();
    
    
    protected int objectif = 0;
    //protected boolean actif = false;
    protected boolean bloque = false;
    
    protected boolean enMouvement;
    
    protected boolean vaVersHaut;
    
    protected int niveau;
    
    @Override
    public void appelExterieur(int niveau, boolean versLeHaut)
    {
        if(versLeHaut)
        {
                aDesservirVersHaut.add(niveau);
        }
        else
        {
                aDesservirVersBas.add(niveau);
        }
        
        InterfacePhysique.changerEtatBouton(niveau, true, versLeHaut);
        update();
    }

    protected Integer getProchain(boolean haut)
    {
        boolean plein = InterfacePhysique.getNbPersonnes(1) == TimeConfig.nbPersMaxAscenseur();
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
        println("nouveau getProchain haut " + haut + " plein " + plein);
            if(haut)
            {
                if(!plein)
                {
                    SortedSet<Integer> restantVHaut = aDesservirVersHaut.tailSet(premierNiveauPossible);
                    if(!restantVHaut.isEmpty())
                    {
                        return restantVHaut.first();
                    }
                    else
                    {
                        SortedSet<Integer> restantVersBas = aDesservirVersBas.tailSet(premierNiveauPossible);
                        if(!restantVersBas.isEmpty())
                        {
                            return restantVersBas.last();
                        }
                        
                    }
                }
                else
                {
                    SortedSet<Integer> aDesservir = appeleDepuisInterieur.tailSet(premierNiveauPossible);
                    if(!aDesservir.isEmpty())
                    {
                        println("aDesservir part 1 " + aDesservir);
                        return aDesservir.first();
                    }
                }
            }
            else
            {
                if(!plein)
                {
                    SortedSet<Integer> restantVBas = aDesservirVersBas.headSet(premierNiveauPossible+1);
                    if(!restantVBas.isEmpty())
                    {
                        return restantVBas.last();
                    }
                    else
                    {
                        SortedSet<Integer> restantVersHaut = aDesservirVersHaut.headSet(premierNiveauPossible+1);
                        if(!restantVersHaut.isEmpty())
                        {
                            return restantVersHaut.first();
                        }
                    }
                }
                else
                {
                    println("appeleDepuisInter " + appeleDepuisInterieur);
                    SortedSet<Integer> aDesservir = appeleDepuisInterieur.headSet(premierNiveauPossible+1);
                    println("aDesservir part 2 " + aDesservir);
                    if(!aDesservir.isEmpty())
                    {
                        return aDesservir.last();
                    }
                }
            }
            return null;
        }
    
    protected List<Integer> getTousNiveau(boolean auDessus,List<Integer> niveaux)
    {
        return niveaux.stream().filter(i -> i>niveau).collect(Collectors.toList());
    }
    
    protected void update()
    {
        if(!bloque)
        {
            Integer val = getProchain(vaVersHaut);
            println("premiereVal " +val);
            if(val == null)
            {
                vaVersHaut = !vaVersHaut;
                val = getProchain(vaVersHaut);
            }
            println("derniereVal " +val);
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
        }
        else if(objectif < niveau)
        {
            InterfacePhysique.deplacerAscenseur(1, false);
            enMouvement = true;
        }
        else
        {
            bloque = true;
            ouvrir();
        }
    }
    
    @Override
    public void finDeTransferDePersonnes(int niveau, int idAscenseur)
    {
        aDesservirVersBas.remove(niveau);
        aDesservirVersHaut.remove(niveau);
        appeleDepuisInterieur.remove(niveau);
        InterfacePhysique.changerEtatBouton(niveau,false,true);
        InterfacePhysique.changerEtatBouton(niveau,false,false);
        InterfacePhysique.fermerLesPortes(niveau, idAscenseur);
    }

    @Override
    public void ascenseurFerme(int idAscenseur)
    {
        System.out.println("ascenseur ferme ");
        bloque = false;
        update();
    }
    
    @Override
    public void appelInterieur(int niveau, int idAscenseur)
    {

        InterfacePhysique.changerEtatBoutonAscenseur(idAscenseur,niveau,true);
        aDesservirVersBas.add(niveau);
        aDesservirVersHaut.add(niveau);
        appeleDepuisInterieur.add(niveau);
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
        return "ameliore_cyclique";
    }

    @Override
    public int getNbAscenseurs()
    {
        return 1;
    }
    
    protected void println(String msg)
    {
        InterfacePhysique.println(msg);
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
    }
}
