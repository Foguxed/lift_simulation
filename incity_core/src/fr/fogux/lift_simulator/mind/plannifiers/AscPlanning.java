package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import fr.fogux.lift_simulator.mind.independant.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.independant.VoisinAsc;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class AscPlanning extends AlgoIndependentAsc
{
    public List<AlgoRequete> reqs;// rem, une sortie ne peut pas être apres une entree au même etage
    protected int readIndex;

    protected Set<AlgoPersonne> contenu;

    public AscPlanning(final AscId id, final ConfigSimu config, final OutputProvider phys, final VoisinAsc precedent)
    {
        super(id, config, phys, precedent);
        reqs = new ArrayList<>() ;
        readIndex = -1;
        contenu = new HashSet<>();

    }

    @Override
    public void init()
    {
        super.init();
        /*
        System.out.println( id + " tente " + reqs + " contenu " + contenu);
        if(!reqs.isEmpty())
        {
            phys().println( id + " tente " + reqs + " contenu " + contenu);
        }*/
    }

    public int[] current0()
    {
        final int[] current0 = new int[2];
        current0[0] = 0;
        current0[1] = contenu.size();
        return current0;
    }

    public boolean exists(final int[] c)
    {
        return c[0] < reqs.size();
    }

    public void next(final int[] current)
    {
        if(reqs.get(current[0]).isEntree())
        {
            current[1] ++;
        }
        else
        {
            current[1] --;
        }
        current[0] ++;
    }

    protected boolean hardForEachEchangeInterne(final int currentIndex, final int capaciteMax, final Supplier<Boolean> tacheUntilTrue )
    {
        final AlgoRequete req = reqs.get(currentIndex);
        final List<AlgoRequete> original = new ArrayList<>(reqs);
        for(int i = currentIndex + 1 ; i < reqs.size(); i ++)
        {
            swap(capaciteMax, i, reqs);
            if(normaliserReqList(capaciteMax))
            {
                if(tacheUntilTrue.get())
                {
                    return true;
                }
            }
            reqs.clear();
            reqs.addAll(original);
        }
        reqs = original;
        return false;
    }

    protected boolean forEachEchangeExterne(final int[] current, final int maxCapacite, final AscPlanning concurrent, final Supplier<Boolean> doUntilTrue)
    {
        if(reqs.get(current[0]).isEntree())
        {
            final int s1 = getSortie(current[0]);
            final SortiePers sp = (SortiePers)reqs.get(s1);
            final EntreePers ep = (EntreePers)reqs.get(current[0]);
            for(int i = 0 ; i < concurrent.reqs.size(); i ++)
            {
                if(concurrent.reqs.get(i).isEntree())
                {
                    final int s2 = concurrent.getSortie(i);
                    final SortiePers sp2 = (SortiePers)concurrent.reqs.get(s2);
                    final EntreePers ep2 = (EntreePers)concurrent.reqs.get(i);
                    if(atteignable2(ep2,sp2) && concurrent.atteignable2(ep, sp))
                    {
                        final ArrayList<AlgoRequete> reqs1 = new ArrayList<>(reqs);
                        final ArrayList<AlgoRequete> reqs2 = new ArrayList<>(concurrent.reqs);

                        reqs.set(current[0], ep2);
                        concurrent.reqs.set(i,ep);
                        reqs.set(s1, sp2);
                        concurrent.reqs.set(s2, sp);
                        normaliserReqList(maxCapacite);
                        //System.out.println( id +" apres norm " + reqs);
                        concurrent.normaliserReqList(maxCapacite);
                        //System.out.println( concurrent.id +" apres norm " + concurrent.reqs);
                        if(doUntilTrue.get())
                        {
                            return true;
                        }
                        reqs = reqs1;
                        concurrent.reqs = reqs2;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param current doit être correct : il doit y a voir current[1] pers à l'étape current[0]
     * @param nbPersSimultanesMax
     * @param tacheUntilTrue
     * @return
     */
    protected boolean forEachEchangeInterne(final int[] current, final int nbPersSimultanesMax, final Supplier<Boolean> tacheUntilTrue)
    {
        final AlgoRequete req = reqs.get(current[0]);
        final List<AlgoRequete> original = new ArrayList<>(reqs);
        final Set<AlgoPersonne> personnesEntreesPlusTard = new HashSet<>();
        final int[] current2 = current.clone();
        int sortie = -1;
        if(req.isEntree())
        {
            sortie = getSortie(current[0]);
            current2[1] ++;
        }
        else
        {
            passerEnDerniereSortie(current2);
            current2[1] --;
        }
        current2[0] ++;
        int maxContenu = current2[1]; // utile seulement si req est une sortie
        boolean memeEscale = true;
        while(current2[0] < reqs.size())
        {
            if(!req.isEntree() && reqs.get(current2[0]).getEtage() == req.getEtage())
            {
                break;
            }
            final AlgoRequete req2 = reqs.get(current2[0]);
            if(memeEscale)
            {
                if(req2.getEtage() != req.getEtage())
                {
                    memeEscale = false;
                }
            }
            else
            {
                if(essayerSwap(current,current2,personnesEntreesPlusTard,maxContenu,nbPersSimultanesMax,sortie,tacheUntilTrue))
                {
                    return true;
                }
            }

            if(req2.isEntree())
            {
                personnesEntreesPlusTard.add(req2.concernee);
                current2[1] ++;
            }
            else
            {
                current2[1] --;
            }
            if(current2[1] > maxContenu)
            {
                maxContenu = current2[1];
            }
            current2[0] ++;

        }
        reqs = original;
        return false;
    }

    private int[] getCurrent(final int index)
    {
        final int[] r = {index, current0()[1]};
        for(int i = 0; i < index; i ++)
        {
            if(reqs.get(i).isEntree())
            {
                r[1] ++;
            }
            else
            {
                r[1] --;
            }
        }
        return r;
    }
    /**
     *
     * @param current si c'est une sortie alors déjà en dernière sortie
     * @param current2
     * @param entresPlusTard
     * @param maxContenu
     * @param maxCapa
     * @param eventuelleSortie
     * @param tacheUntilTrue
     * @return
     */
    private boolean essayerSwap(final int[] current, int[] current2, final Set<AlgoPersonne> entresPlusTard, final int maxContenu, final int maxCapa,final int eventuelleSortie, final Supplier<Boolean> tacheUntilTrue)
    {
        final AlgoRequete req = reqs.get(current[0]);
        final AlgoRequete req2 = reqs.get(current2[0]);
        List<AlgoRequete> original = reqs;
        if(req2.isEntree())
        {
            original = new ArrayList<>(reqs);
            if(req.isEntree())
            {
                final int sortie2 = getSortie(current2[0]);
                swap(current2[0],current[0],reqs);
                if(eventuelleSortie < current2[0])
                {
                    swap(eventuelleSortie,sortie2,reqs);
                }
            }
            else
            {
                swap(current2[0],current[0],reqs);
            }
            if(normaliserReqList(maxCapa))
            {
                if(tacheUntilTrue.get())
                {
                    return true;
                }
            }
        }
        else
        {
            if(!entresPlusTard.contains(req2.concernee))
            {
                original = new ArrayList<>(reqs);
                if(req.isEntree())
                {
                    if(eventuelleSortie > current2[0])
                    {
                        current2 = current2.clone();
                        passerEnDerniereSortie(current2);
                        swap(current2[0],current[0],reqs);
                        arrangerSorties(current2);
                        if(tacheUntilTrue.get())
                        {
                            return true;
                        }
                    }
                }
                else
                {
                    //System.out.println(id + " avant swap " + " va swap " + current[0]  + " avec " + current2[0] + " vals " + reqs );
                    current2 = current2.clone();
                    current2[0] = passerEnPremiereSortie(current2[0]);
                    //System.out.println(id + " est passé " +current2[0] + " en premiere sortie" + reqs);
                    swap(current2[0],current[0],reqs);
                    arrangerSorties(current2);
                    arrangerSorties(current);
                    //System.out.println(id + " apres swap " + reqs);

                    if(tacheUntilTrue.get())
                    {
                        return true;
                    }
                }
            }
        }
        reqs = original;
        return false;
    }

    private boolean normaliserReqList(final int maxCapa)
    {
        final Set<AlgoPersonne> c = new HashSet<>(contenu);
        int i = 0;
        while(i < reqs.size())
        {
            final int etage = reqs.get(i).getEtage();
            while(i < reqs.size() && !reqs.get(i).isEntree() && reqs.get(i).getEtage() == etage)
            {
                if(c.remove(reqs.get(i).concernee))
                {
                    i++;
                }
                else
                {
                    reqs.remove(i);
                }
            }
            if(c.stream().anyMatch(p -> p.destination == etage))
            {
                for(final AlgoPersonne p : new ArrayList<>(c))
                {
                    if(p.destination == etage)
                    {
                        reqs.add(i,new SortiePers(p));
                        i ++;
                        c.remove(p);
                    }
                }
            }
            while(i < reqs.size() && reqs.get(i).isEntree() && reqs.get(i).getEtage() == etage)
            {
                c.add(reqs.get(i).concernee);
                if(c.size() >= maxCapa)
                {
                    return false;
                }
                i ++;
            }
        }
        if(c.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void passerEnDerniereSortie(final int[] etatSortie)
    {
        final int etage = reqs.get(etatSortie[0]).getEtage();
        for(int i = etatSortie[0] + 1; i < reqs.size(); i ++)
        {
            final AlgoRequete req = reqs.get(i);
            if(req.getEtage() != etage || req.isEntree())
            {
                swap(etatSortie[0],i-1,reqs);
                etatSortie[0] = i-1;
                return;
            }
            else
            {
                etatSortie[1] ++;
            }
        }
    }

    private int passerEnPremiereSortie(final int sortieIndex)
    {
        final int etage = reqs.get(sortieIndex).getEtage();
        for(int i = sortieIndex - 1; i >= 0; i --)
        {
            final AlgoRequete req = reqs.get(i);
            if(req.getEtage() != etage)
            {
                swap(sortieIndex,i+1,reqs);
                return i+1;
            }
        }
        return sortieIndex;
    }

    private int passerEnDerniereSortie(final int indexSortie)
    {
        final int etage = reqs.get(indexSortie).getEtage();
        for(int i = indexSortie + 1; i < reqs.size(); i ++)
        {
            final AlgoRequete req = reqs.get(i);
            if(req.getEtage() != etage || req.isEntree())
            {
                swap(indexSortie,i-1,reqs);
                return i-1;
            }
        }
        return indexSortie;
    }

    private static <T> void swap(final int i, final int j, final List<T> list)
    {
        final T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private int getSortie(final int indexEntree)
    {
        final AlgoPersonne p = reqs.get(indexEntree).concernee;
        for(int i = indexEntree + 1; i < reqs.size(); i ++)
        {
            if(reqs.get(i).concernee == p)
            {
                return i;
            }
        }
        return -1;
    }

    protected boolean forEachSortieInsert(final SortiePers sortie, final int[] current,final int nbPersSimulatnesMax, final Supplier<Boolean> tacheUntilTrue)
    {
        int[] current2 = {current[0] + 1, current[1] + 1};

        while(current2[0] <= reqs.size() && reqs.get(current2[0] - 1).getEtage() != sortie.getEtage() && current2[1] <= nbPersSimulatnesMax)
        {

            reqs.add(current2[0], sortie);
            final int[] originalState2 = current2.clone();

            //System.out.println(id + " vient de add en " + current2[0] + " la chose suivante " + sortie);
            //System.out.println(id + " avantArrangement " + reqs);
            final List<AlgoRequete> original2 = arrangerSorties(current2);
            //System.out.println(id + " resultReqs " + reqs);
            if(tacheUntilTrue.get())
            {
                return true;
            }
            if(original2 != null)
            {
                reqs = original2;
                current2 = originalState2;
            }
            reqs.remove(current2[0]);

            continuerJusqueInsertionPossible(current2);
        }
        return false;
    }

    protected boolean atteignable2(final EntreePers entree, final SortiePers sortie)
    {
        return atteignable(entree.getEtage()) && atteignable(sortie.getEtage());
    }

    /**
     *
     * @param entree
     * @param sortie
     * @param nbPersSimulatnesMax
     * @param tache, si true alors on s'arrête
     */
    public boolean forEachFullInsertUntil(final EntreePers entree, final SortiePers sortie, final int nbPersSimulatnesMax, final Supplier<Boolean> tacheUntilTrue)
    {
        //System.out.println("reqsLongueur " + reqs.size());
        if(!atteignable2(entree,sortie))
        {
            return false;
        }
        int[] current = current0();
        //System.out.println("initial remplissage " + current[1]);
        while(current[0] <= reqs.size())
        {

            //System.out.println("entree a " + current[0] + " reqs " + reqs);
            if(current[1] < nbPersSimulatnesMax)
            {
                reqs.add(current[0],entree);
                final int[] originalState = current.clone();
                final List<AlgoRequete> original = arrangerSorties(current);

                //System.out.println(id + " nbpers " + current[1] + " avant l'exec " + reqs);
                if(forEachSortieInsert(sortie,current,nbPersSimulatnesMax,tacheUntilTrue))
                {
                    //System.out.println("attention un return " + reqs.size());
                    return true;
                }
                //System.out.println("doit etre revenu " + reqs.size());
                if(original != null)
                {
                    reqs = original;
                    current = originalState;
                }
                reqs.remove(current[0]);
                //System.out.println("un abandon retour a " + reqs.size());
            }
            continuerJusqueInsertionPossible(current);
        }
        return false;
    }

    /**
     *
     * @param minIndex
     * @param indexMax
     */
    private int placeSortie(final int minIndex, final int maxIndex, final int etage)
    {
        for(int i = minIndex ; i < maxIndex ; i ++)
        {
            if(reqs.get(i).getEtage() == etage)
            {
                return i;
            }
        }
        return -1;
    }

    public List<AlgoRequete> cloneState()
    {
        return new ArrayList<>(reqs);
    }

    public void applyState(final List<AlgoRequete> state)
    {
        reqs = state;
    }

    /**
     * déplace les sorties si nécessaire
     * @param indexInsertion
     * @param etage
     * @return null si reqs n'a pas été modifié, l'original sinon
     */
    private List<AlgoRequete> arrangerSorties(final int[] indexEtRemplissage)
    {
        final Set<Integer> recupereApres = new HashSet<>();

        final int etage = reqs.get(indexEtRemplissage[0]).getEtage();
        List<AlgoRequete> retour = null;
        AlgoRequete req;
        for(int i = indexEtRemplissage[0] + 1; i < reqs.size(); i ++)
        {
            req = reqs.get(i);
            if(req.concernee.destination == etage)
            {
                if(req.isEntree())
                {
                    recupereApres.add(req.concernee.id);
                }
                else
                {
                    if(!recupereApres.contains(req.concernee.id))
                    {
                        if(retour == null)
                        {
                            retour = new ArrayList<>(reqs);
                        }
                        reqs.remove(i);
                        reqs.add(indexEtRemplissage[0],req);
                        indexEtRemplissage[0] ++;
                        indexEtRemplissage[1] --;
                    }
                }
            }
        }
        return retour;
    }

    private void continuerJusqueInsertionPossible(final int[] indexEtRemplissage)
    {
        if(indexEtRemplissage[0] == reqs.size())
        {
            indexEtRemplissage[0] ++; // on arrete
            return;
        }

        if(reqs.get(indexEtRemplissage[0]).isEntree())
        {
            indexEtRemplissage[0] ++;
            indexEtRemplissage[1] ++;
        }
        else
        {
            final int etage = reqs.get(indexEtRemplissage[0]).getEtage();
            indexEtRemplissage[0] ++;
            indexEtRemplissage[1] --;
            while(indexEtRemplissage[0] < reqs.size() && !reqs.get(indexEtRemplissage[0]).isEntree() && reqs.get(indexEtRemplissage[0]).getEtage() == etage)
            {
                indexEtRemplissage[0] ++;
                indexEtRemplissage[1] --;
            }
        }
    }

    public List<Integer> listeInvitesDestructif(final int etage)
    {
        final List<Integer> invites = new ArrayList<>();
        while(!reqs.isEmpty() && !reqs.get(0).isEntree() && reqs.get(0).getEtage() == etage)
        {
            contenu.remove(reqs.get(0).concernee);
            reqs.remove(0);

        }
        while(!reqs.isEmpty() && reqs.get(0).getEtage() == etage)
        {
            contenu.add(reqs.get(0).concernee);
            invites.add(reqs.get(0).concernee.id);
            reqs.remove(0);
        }
        //System.out.println("destruct liste " + invites);
        return invites;
    }

    public Integer prochainObjectif()
    {
        //System.out.println("demandeProchainObj");
        if(!reqs.isEmpty())
        {
            return reqs.get(0).getEtage();
        }
        return null;
    }


    public void shadowMode()
    {
        super.saveState();
        readIndex = 0;
    }

    @Override
    public void rallBack()
    {
        super.rallBack();
        readIndex = 0;
    }

    public void realMode()
    {
        super.rallBack();
        readIndex = -1;
    }

    public Integer shadowProchainObjectif()
    {
        if(readIndex < reqs.size())
        {
            return reqs.get(readIndex).getEtage();
        }
        return null;
    }

    public List<Integer> shadowListeInvite(final int etage)
    {

        final List<Integer> invites = new ArrayList<>();
        while(readIndex < reqs.size() && reqs.get(readIndex).getEtage() == etage && !reqs.get(readIndex).isEntree())
        {
            readIndex ++;
        }
        while(readIndex < reqs.size() && reqs.get(readIndex).getEtage() == etage)
        {
            invites.add(reqs.get(readIndex).concernee.id);
            readIndex ++;
        }
        //System.out.println("shadow invite etage " + etage + " invite " + invites);
        return invites;
    }



    @Override
    public List<Integer> getInvites(final int niveau, final int placesDispo)
    {
        //System.out.println(id + " a atteint " + niveau);
        if(readIndex < 0)
        {
            return listeInvitesDestructif(niveau);
        }
        else
        {
            return shadowListeInvite(niveau);
        }
    }



    @Override
    public Integer prochainArret(final Predicate<Integer> aFiltrer)
    {
        //System.out.println("prochainArret ");
        //phys().println("prochainArret ");
        Integer v;
        if(readIndex < 0)
        {
            //phys().println("par la version destructrice, reqs " + reqs);
            v = prochainObjectif();
        }
        else
        {
            v = shadowProchainObjectif();
        }
        if(v!=null && aFiltrer.test(v))
        {
            return v;
        }
        else
        {
            return null;
        }
    }
}
