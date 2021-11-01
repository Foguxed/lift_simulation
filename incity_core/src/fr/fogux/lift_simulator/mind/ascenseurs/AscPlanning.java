package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import fr.fogux.lift_simulator.mind.planifiers.AlgoRequete;
import fr.fogux.lift_simulator.mind.planifiers.EntreePers;
import fr.fogux.lift_simulator.mind.planifiers.SortiePers;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.Utils;

public class AscPlanning extends AlgoIndependentAsc
{
    public List<AlgoRequete> reqs;// une sortie ne peut pas être apres une entree au même etage
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

    /**
     *
     * @param current:  un indice de reqs (current[0]) et le nombre de personne dans l'ascenseur à cet indice là du plan (current[1]).
     * @param maxCapacite
     * @param concurrent
     * @param doUntilTrue
     * @return
     */

    public boolean forEachEchangeExterne(final int[] current, final int maxCapacite, final AscPlanning concurrent, final Supplier<Boolean> doUntilTrue)
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
                        concurrent.normaliserReqList(maxCapacite);

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

        //debugCheckState("fin echanges externes", maxCapacite);
        return false;
    }

    /**
     *
     * @param current doit être correct : il doit y a voir current[1] pers à l'étape current[0]
     * @param maxCapa
     * @param tacheUntilTrue
     * @return
     */
    public boolean forEachEchangeInterne(final int[] current, final int maxCapa, final Supplier<Boolean> tacheUntilTrue)
    {
        //debugCheckState("debut echanges internes", maxCapa);
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
                if(essayerSwap(current,current2,personnesEntreesPlusTard,maxContenu,maxCapa,sortie,tacheUntilTrue))
                {
                    //debugCheckState("un echange interne correct", maxCapa);
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
        //debugCheckState("fin echanges internes", maxCapa);
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
     * @return true si tacheUntilTrue a commandé l'arrêt des tentatives, false sinon
     */
    private boolean essayerSwap(int[] current, int[] current2, final Set<AlgoPersonne> entresPlusTard, final int maxContenu, final int maxCapa,final int eventuelleSortie, final Supplier<Boolean> tacheUntilTrue)
    {
        //debugCheckState("debut essayerSwap ",maxCapa);
        current = current.clone();
        current2 = current2.clone();
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
                        swap(current2[0],current[0],reqs);
                        if(normaliserReqList(maxCapa))
                        {
                            if(tacheUntilTrue.get())
                            {
                                return true;
                            }
                        }
                    }
                }
                else
                {
                    current2[0] = passerEnPremiereSortie(current2[0]);
                    swap(current2[0],current[0],reqs);
                    arrangerSorties(current2);
                    arrangerSorties(current);

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

    /**
     *
     * @param reqs la liste de requêtes à normaliser, vérifie que la capacité n'est pas dépassée
     * @param initialContenu le contenu au début de la liste de requête
     * @param maxCapa la capacité de l'ascenseur
     * @return false si le contenu est dépassé lors de l'execution de reqs, ou si toutes les personnes entrées ne sont pas sorties à un moment dans reqs, true sinon.
     */

    private boolean innerNormaliserReqList(final List<AlgoRequete> reqs, final Set<AlgoPersonne> initialContenu,final int maxCapa)
    {
        if(maxCapa != 5)
        {
            throw new IllegalStateException("debug mauvaise capa");
        }
        final Set<AlgoPersonne> c = new HashSet<>(initialContenu);
        int i = 0;
        while(i < reqs.size())
        {
            final int etage = reqs.get(i).getEtage();
            while(i < reqs.size() && !reqs.get(i).isEntree() && reqs.get(i).getEtage() == etage)// on enleve les sorties qui n'ont plus lieu d'être (personne déjà sortie avant)
            {
                if(c.remove(reqs.get(i).concernee)) // si la personne était bien dans le contenu, on laisse la sortie tel quel
                {
                    i++;
                }
                else
                {
                    reqs.remove(i);// on enleve la sortie car la personne est déjà sortie précédement (voir if ci dessous)
                }
            }
            if(c.stream().anyMatch(p -> p.destination == etage)) // si le contenu contenait des personnes qui peuvent sortir immédiatement, alors elles sortent
            {
                for(final AlgoPersonne p : new ArrayList<>(c))
                {
                    if(p.destination == etage)// retirer la sortie de p plus tard sera effectué par le while au dessus
                    {
                        reqs.add(i,new SortiePers(p));
                        i ++;
                        c.remove(p);
                    }
                }
            }
            while(i < reqs.size() && reqs.get(i).isEntree() && reqs.get(i).getEtage() == etage) // entrée d'une personne
            {
                c.add(reqs.get(i).concernee);
                if(c.size() > maxCapa)// dépassement de la capacité de l'ascenseur
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

    private boolean normaliserReqList(final int maxCapa)
    {
        return innerNormaliserReqList(reqs,contenu, maxCapa);
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

    /**
     *
     * @param indexEntree
     * @return l'indice de sortie de l'entrée en indice indexEntree dans reqs
     */
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

            final List<AlgoRequete> original2 = arrangerSorties(current2);

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
     * @param capaMax
     * @param tache, si tache.get() passe à true alors on s'arrête
     */
    public boolean forEachFullInsertUntil(final EntreePers entree, final SortiePers sortie, final int capaMax, final Supplier<Boolean> tacheUntilTrue)
    {
        if(!atteignable2(entree,sortie))
        {
            return false;
        }
        int[] current = current0();
        Utils.debug();// TODO enlever
        while(current[0] <= reqs.size())
        {
            if(current[1] < capaMax)
            {
                reqs.add(current[0],entree);
                final int[] originalState = current.clone();
                final List<AlgoRequete> original = arrangerSorties(current);
                if(forEachSortieInsert(sortie,current,capaMax,tacheUntilTrue))
                {
                    return true;
                }
                if(original != null)
                {
                    reqs = original;
                    current = originalState;
                }
                reqs.remove(current[0]);
            }
            continuerJusqueInsertionPossible(current); // on avance l'indice dans current jusqu'à ce que l'ascenseur ne soit plus pleins
        }
        //debugCheckState("fin full insert ",capaMax);
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

    public void debugCheckState(final String msg, final int maxCapa)
    {
        phys().println(msg + " id " + id + " " + cloneState());
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
        //System.out.println(id + " shadow invite etage " + etage + " invite " + invites + " nouveaureadindex " +readIndex +  " reqs " + reqs);
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

    @Override
    public Integer positionDattente()
    {
        return prochainArret(a -> true);
    }
}
