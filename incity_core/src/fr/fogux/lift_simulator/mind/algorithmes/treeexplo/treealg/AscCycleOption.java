package fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.ConfigAlgoCycleOption;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.ConsumerContenu;
import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;
import fr.fogux.lift_simulator.mind.ascenseurs.VoisinAsc;
import fr.fogux.lift_simulator.mind.option.BChoix;
import fr.fogux.lift_simulator.mind.option.Choix;
import fr.fogux.lift_simulator.mind.planifiers.ContenuAsc;
import fr.fogux.lift_simulator.mind.planifiers.ContenuEtPrevision;
import fr.fogux.lift_simulator.mind.planifiers.TrueLivraison;
import fr.fogux.lift_simulator.mind.planifiers.ContenuEtPrevision.Cycle;
import fr.fogux.lift_simulator.mind.pool.ByEtageFewUpdatePool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.utils.CustomForEach;
import fr.fogux.lift_simulator.utils.Ref;
import fr.fogux.lift_simulator.utils.Utils;

public class AscCycleOption extends AscIndepIteratif<ByEtageFewUpdatePool>
{
    public ContenuAsc contenu;
    protected AlgoPersGroup prochainClient; //le prochain groupe de personne à prendre dans l'ascenseur si celui ci se situe avant la première sortie du conteu
    protected static final int limitNbOptions = 5;
    protected static final float marge = 0.9f;
    
    protected AscCycleOption ascInf;
    protected AscCycleOption ascSup;
    protected boolean enMontee = true;

    protected final Predicate<AlgoPersGroup> prochainClientVoisinFilter = (pg -> voisinPredicate.test(pg.getEtage()));

    protected Cycle tempCycle;

    protected ConfigAlgoCycleOption heuristiqueConfig;

    public static Consumer<AscCycleOption> debugConsumer()
    {
        final int v = Utils.newDebugCompteurId();
        return new Consumer<AscCycleOption>()
        {

            @Override
            public void accept(final AscCycleOption t)
            {
                t.outputProvider.interfacePhys.println("debugCompteur " + v);
            }

        };
    }

    public static Consumer<AscCycleOption> debugConsumer(final String text)
    {
        //System.out.println(text);
        return new Consumer<AscCycleOption>()
        {

            @Override
            public void accept(final AscCycleOption t)
            {
                t.outputProvider.interfacePhys.println(t.id + " exec " + text);
            }
        };
    }

    public  Consumer<AscCycleOption> debugMatchingIdConsumer()
    {
        final int debug = Utils.newDebugCompteurId();
        final long time = phys().simu.getTime();
        /*if(alg.doDebug())
        {
            System.out.println(" event sur " + id + " dans " + phys().simu.hashCode() + " debug " + debug);
        }*/
        return new Consumer<AscCycleOption>()
        {

            @Override
            public void accept(final AscCycleOption t)
            {
                if(!t.getId().equals(id) || t.outputProvider.interfacePhys.simu.getTime() != time)
                {
                    throw new SimulateurAcceptableException(" event envoye sur " + t.getId() + " or choix fait par " + id + " choisi par " + phys().simu.hashCode() + " debug " +  debug+ " castTime " + Utils.getTimeString(time) + " exectime " + Utils.getTimeString(t.outputProvider.interfacePhys.simu.getTime()));
                }
            }

        };
    }


    public Consumer<AscCycleOption> getConsumerSetMontee(final boolean monte)
    {
        return new Consumer<AscCycleOption>()
        {

            @Override
            public void accept(final AscCycleOption t)
            {
                t.enMontee = monte;
            }
        };
    }


    public static Consumer<AscCycleOption> getProchainClientConsumer(final AlgoPersGroup target)
    {
        final AlgoPersGroup copiedTarget = new AlgoPersGroup(target);
        return new Consumer<AscCycleOption>()
        {
            @Override
            public void accept(final AscCycleOption t)
            {
                t.verouillerProchainClient(new AlgoPersGroup(copiedTarget));
            }
        };
    }

    public static Consumer<AscCycleOption> getConsContenu(final ContenuAsc newContenuCopied)
    {


        return new ConsumerContenu(newContenuCopied);

        /*
        final ContenuAsc copied = new ContenuAsc(newContenuCopied);

        final int i = Utils.newDebugCompteurId();



        System.out.println("cree cons contenu " + copied + " id " + i);
        return new Consumer<AscCycleOption>()
        {
            @Override
            public void accept(final AscCycleOption t)
            {
                t.phys().println("contenu consumerupdated from " + t.contenu + " to " + copied + " id " + i);
                t.contenu = copied;
            }
        };*/
    }

    /*
    public static Consumer<AscCycleOption> CONSUMER_CHGMT_SENS = new Consumer<AscCycleOption>()
    {

        @Override
        public void accept(final AscCycleOption t)
        {
            t.monte = !t.monte;
        }
    };
     */

    /**
     * Gourmand: essaye de remplir l'ascenseur à chaque arrêt, note chaque prise de groupe
     *
     * @param id
     * @param config
     * @param outputProvider
     * @param ascPrecedent
     */



    public AscCycleOption(final AscId id, final ConfigSimu config, final OutputProvider outputProvider, final VoisinAsc ascPrecedent)
    {
        super(id, config, outputProvider, ascPrecedent);
        if(config.getAlgoData() == null)
        {

        }
        heuristiqueConfig = (ConfigAlgoCycleOption) config.getAlgoData();
        contenu = new ContenuAsc(config.nbPersMaxAscenseur());
        if(ascPrecedent instanceof AscCycleOption)
        {
            ascInf = (AscCycleOption) ascPrecedent;
        }
    }

    public AscCycleOption(final AscCycleOption toShadow, final ConfigSimu config, final OutputProvider phys,
        final VoisinAsc ascPrecedent)
    {
        super(toShadow,config,phys,ascPrecedent);
        heuristiqueConfig = (ConfigAlgoCycleOption) config.getAlgoData();
        contenu = new ContenuAsc(toShadow.contenu);
        if(ascPrecedent instanceof AscCycleOption)
        {
            ascInf = (AscCycleOption) ascPrecedent;
        }
        if(toShadow.prochainClient != null)
        {
            prochainClient = new AlgoPersGroup(toShadow.prochainClient);
        }
        enMontee = toShadow.enMontee;
    }

    public void update(final AscCycleUpdate updt)
    {
        contenu = updt.contenu;
        prochainClient = updt.prochainCl;
    }


    @Override
    public void setAscenseurSuperieur(final VoisinAsc asc)
    {
        super.setAscenseurSuperieur(asc);
        if(asc instanceof AscCycleOption)
        {
            ascSup = (AscCycleOption)asc;
        }
    }

    /*
    public int etageMaxContenu()
    {
        return contenu.livraisons.stream().max(AlgoPersonne.destinationComparator).get().destination;
    }*/

    public static final Consumer<AscCycleOption> getConsumerEntreePers(final List<AlgoPersonne> choix)
    {
        return new Consumer<AscCycleOption>()
        {

            @Override
            public void accept(final AscCycleOption t)
            {
                t.aFaitEntrer(choix);
            }
        };
    }

    protected void updateVoisinsCycles()
    {
        if(ascSup != null)
        {
            ascSup.updateCycle();
        }
        if(ascInf != null)
        {
            ascInf.updateCycle();
        }
    }

    @Override
    public Collection<Choix<List<AlgoPersonne>, ?>> getPossibilitesInvites(final int niveau, final int placesDispo)
    {
        updateVoisinsCycles();
        final List<Choix<List<AlgoPersonne>, ?>> listeChoix = new ArrayList<>();
        noticeOuverture(niveau);
        final List<AlgoPersonne> basePrises = new ArrayList<>();
        Consumer<AscCycleOption> cons = Utils.emptyConsumer();// les consumer permettre de transmettre le changement d'état aux simulations filles
        
        if(prochainClient != null)// prends le prochain client
        {
            if(prochainClient.getEtage() != niveau)
            {
                listeChoix.add(new BChoix<>(new ArrayList<>()));
                return listeChoix;
            }
            prochainClient.dump(basePrises, prochainClient.size());
            contenu.add(prochainClient,prochainClient.size());
            prochainClient = null;
            cons = cons.andThen(asc -> asc.prochainClient = null);
        }
        prendrePersAvecMemeSortie(basePrises, niveau , contenu.placesDispo());// prend automatiquement les personnes qui ont la même sorties que d'autres du contenu
        
        
        
        final ContenuAsc contenuInit = new ContenuAsc(contenu);
        if(contenu.estPleins())
        {
            listeChoix.add(new BChoix<>(basePrises,cons.andThen(getConsContenu(contenuInit)).andThen(getConsumerEntreePers(basePrises))));
        }
        else
        {
        	//essaye différentes prises de personnes
            final List<FloatComparable<ContenuEtPrevision>> resultat = new ArrayList<>();
            final List<FloatComparable<ContenuAsc>> completables = new ArrayList<>();
            final ContenuEtPrevision prevInitiale = new ContenuEtPrevision(null,contenuInit);
            final float evalInitiale =  evaluer(prevInitiale);
            final FloatComparable<ContenuAsc> fcontenuInitial = new FloatComparable<>(contenuInit,evalInitiale);
            resultat.add(new FloatComparable<>(prevInitiale,evalInitiale));
            if(!contenuInit.estPleins())
            {
                completables.add(fcontenuInitial);
            }
            while(!completables.isEmpty())// remplis l'ascenseur de plusieurs manières possibles 
            {
                final List<FloatComparable<ContenuAsc>> nextCompletable = new ArrayList<>();
                for(final FloatComparable<ContenuAsc> fCompletableContenu : completables)
                {
                    final ContenuAsc currentContenu = fCompletableContenu.v;
                    if(!currentContenu.contientPersDansSens(enMontee, niveau)) // on peut essayer un demi-tour
                    {
                        final Optional<AlgoPersGroup> optSensInverse = getMeilleurProchainClientAbsolu(currentContenu, niveau, !enMontee,true);
                        if(optSensInverse.isPresent())
                        {
                            final ContenuEtPrevision avecClientSensInverse = new ContenuEtPrevision(optSensInverse.get(), currentContenu);
                            resultat.add(new FloatComparable<>(avecClientSensInverse,evaluer(avecClientSensInverse)));
                        }
                    }
                    final Optional<AlgoPersGroup> opt = getMeilleurProchainClientDepuisPosActuelle(currentContenu, niveau, enMontee);
                    if(opt.isPresent())
                    {
                        final ContenuEtPrevision avecClientExt = new ContenuEtPrevision(opt.get(), currentContenu);
                        final float x2 = evaluer(avecClientExt);
                        resultat.add(new FloatComparable<>(avecClientExt,x2));
                    }
                    //premierFiltrageGroupes évalue différents paramètres de manière heuristique tels que la taille du groupe, son étage de destination, la compatibilité avec le contenu currentContenu
                    premierFiltrageGroupes(currentContenu, pools.get(0).getPersGroups(niveau).filter(p -> !currentContenu.destinationCompatible(p)),50) //dans filter on enleve les groupes deja consideres
                    .sorted().limit(5).forEach(
                        cpg ->
                        {
                        	// on ajoute cette possibilité dans résultats
                            final ContenuAsc c = new ContenuAsc(currentContenu);
                            c.addMax(cpg.v);
                            final ContenuEtPrevision sansClientExt = new ContenuEtPrevision(null, c);
                            final float x1 = evaluer(sansClientExt);// évalue deplus la compatibilité des déplacements prochains de l'ascenseur avec ses voisins
                            resultat.add(new FloatComparable<>(sansClientExt, x1));
                            if(!c.estPleins())
                            {
                                nextCompletable.add(new FloatComparable<>(c, x1));
                            }
                        }
                        );
                }
                completables.clear();
                nextCompletable.stream().sorted().limit(10).forEach(fc -> completables.add(fc));

            }
            final float meilleurResultat = resultat.stream().min(getFloatComparator()).get().evaluation;
            final float evMmin = meilleurResultat*(1-marge);

            final Consumer<AscCycleOption> consumerBase = cons;
            resultat.stream().filter(p -> evMmin <= p.evaluation).sorted().limit(limitNbOptions).forEach(
            		
                fcp ->
                {
                	// on ajoute l'option dans l'arbre de choix
                    final List<AlgoPersonne> invites = getTousInvites(basePrises, contenuInit, fcp.v.contenu);
                    Consumer<AscCycleOption> c = consumerBase.andThen(getConsumerEntreePers(invites)); // peut enlever une deuxieme fois des personnes mais ce n'est pas un problème
                    c = c.andThen(getConsContenu(fcp.v.contenu));
                    if(fcp.v.prochainClient != null)
                    {
                        c = c.andThen(getProchainClientConsumer(fcp.v.prochainClient));
                    }
                    listeChoix.add(new BChoix<>(invites, c));
                }
                );

        }
        return listeChoix;
    }

    public int etageActuel()
    {
        return phys().getEtat(id).etageAtteignablePlusProche();
    }





    protected float evaluer(final ContenuEtPrevision contenuEtPrevision)
    {

        final int etageActuel = phys().getEtat(id).etageAtteignablePlusProche();

        final Cycle c = contenuEtPrevision.getSuiteEtages(enMontee, etageActuel);

        final float resultat = evaluerCompatibilite(contenuEtPrevision)*evaluerEfficacite(contenuEtPrevision, c,etageActuel)*evaluerSens(c,etageActuel);
        //System.out.println("testing " + contenuEtPrevision + " total "+ resultat);
        //System.out.println("evaluerCompatibilite " + evaluerCompatibilite(contenuEtPrevision) + " evaluerEfficacite " + evaluerEfficacite(contenuEtPrevision, c,etageActuel) + " evalsens " + evaluerSens(c,etageActuel));
        return resultat;
    }

    protected float evaluerCompatibilite(final ContenuEtPrevision contenuEtPrevision)
    {
        return noteCycle(contenuEtPrevision.getSuiteEtages(enMontee, phys().getEtat(id).etageAtteignablePlusProche()));
    }

    protected float evaluerSens(final Cycle contenuEtPrevision, final int etageActuel)
    {

        if(!contenuEtPrevision.apresDemiTour.isEmpty())
        {
            return notePrisePersSens(!enMontee);
        }
        else
        {
            return 1f;
        }
    }

    protected float evaluerEfficacite(final ContenuEtPrevision contenuEtPrevision, final Cycle c, final int etageActuel)
    {
        float x = contenuEtPrevision.getTotalScoreDeplacement(etageActuel,heuristiqueConfig);
        if(x == 0f)
        {
            return x;
        }
        //System.out.println("evalEfficaciteProcess x " + x + " totalParcourt " + c.getTotalParcourt(etageActuel));
        x = x/(c.getTotalParcourt(etageActuel) + contenuEtPrevision.nbArrets()*heuristiqueConfig.equivalentDistanceUnArret);
        //System.out.println("refactored x " + x);

        float bonusActivite = 0f;

        if(contenuEtPrevision.prochainClient != null)
        {
            bonusActivite += noterEtageDeDepart(contenuEtPrevision.contenu, contenuEtPrevision.prochainClient, c.cycleMontee) -1f;
        }
        for(final Integer arretAv : c.avantDemiTour)
        {
            bonusActivite+=noterGrossierementActiviteEtage(arretAv, c.cycleMontee);
        }
        for(final Integer arretAv : c.apresDemiTour)
        {
            bonusActivite+=noterGrossierementActiviteEtage(arretAv, !c.cycleMontee);
        }
        //System.out.println("x " + x + " nbArrets " + contenuEtPrevision.nbArrets());
        if(contenuEtPrevision.nbArrets() > 0)
        {
            bonusActivite = bonusActivite/(contenuEtPrevision.nbArrets());// moyenne
        }
        return x * (1f + bonusActivite)*Utils.pow(heuristiqueConfig.malusPlacesNonUtilisees, contenuEtPrevision.nbPlacesNonUtilises());
    }

    protected void prendrePersAvecMemeSortie(final List<AlgoPersonne> collecteur,final int niveau, final int placesDispo)
    {
        final List<TrueLivraison> livraisons = new ArrayList<>();
        final Ref<Integer> places = new Ref<>(placesDispo);
        CustomForEach.forEach(pools.get(0).getPersGroups(niveau).filter(p -> contenu.destinationCompatible(p)).sorted(AlgoPersGroup.sizeComparator), // on prend les groupes les plus petits en priorités ici
            (p,breaker) ->
        {
            int n = p.size();
            if(n >= places.get())
            {
                n = places.get();
                breaker.stop();
            }
            places.set(places.get()-n);
            livraisons.add(new TrueLivraison(p, n));
        }
            );
        contenu.mergeLivraisons(livraisons);
        final List<AlgoPersonne> aRetirerPool = new ArrayList<>();
        livraisons.stream().forEach(l ->
        {
            l.dump(aRetirerPool);
            l.dump(collecteur);
        }
            );
        aFaitEntrer(aRetirerPool);
    }

    protected float noterGroupeVsPlaces(final AlgoPersGroup p, final int placesDispos)
    {
        if(p.size() == placesDispos)
        {
            return heuristiqueConfig.bonusGroupeBonneTaille;
        }
        else if(p.size() < placesDispos)
        {
            return Utils.pow(heuristiqueConfig.malusPlacesLibres, placesDispos - p.size());
        }
        else return 1f;
    }

    protected float noterRentabiliteCourseContenant(final int nbPers)
    {
        return 0.3f*((float)nbPers/(float)config.nbPersMaxAscenseur());
    }

    protected float noterEnchainement(final ContenuEtPrevision contenuAsc,final AlgoPersGroup p)
    {
        final Ref<Float> v = new Ref<>(1f);

        pools.get(0).getPersGroups(p.getDestination()).forEach(pg ->
        {
            float x = pg.size();
            if(pg.monte() != p.monte())
            {
                x*=noterChangementDeSens(p.getDestination(), p.monte());
            }
            if(contenuAsc.destinationCompatible(pg))
            {
                x*=heuristiqueConfig.bonusMemeDestination;
            }
            v.set(v.get() + x);
        }
            );
        final float referenceVal = pools.get(0).refNoteEtage();
        return v.get()/referenceVal;
    }

    protected float noterGrossierementActiviteEtage(final int etage, final boolean montee)
    {
        final Ref<Float> v = new Ref<>(1f);

        pools.get(0).getPersGroups(etage).forEach(pg ->
        {
            float x = pg.size();
            if(pg.monte() != montee)
            {
                x*=noterChangementDeSens(etage, montee);
            }
            v.set(v.get() + x);
        }
            );
        final float referenceVal = pools.get(0).refNoteEtage();
        return v.get()/referenceVal;
    }

    protected float noterEtageDeDepart(final ContenuAsc contenu, final AlgoPersGroup potentielClient, final boolean monte)
    {
        float v = 1;
        final int placesDispo = contenu.placesDispo()-potentielClient.size();

        if(placesDispo > 0)
        {
            final Ref<Integer> compatibles = new Ref<>(0);
            final Ref<Integer> autres = new Ref<>(0);
            CustomForEach.forEach(pools.get(0).getPersGroups(potentielClient.getEtage()), (pg,b) ->
            {
                if(pg.getDestination() != potentielClient.getDestination())
                {
                    if(contenu.destinationCompatible(pg))
                    {
                        compatibles.set(compatibles.get() + pg.size());
                        if(compatibles.get() >= placesDispo)
                        {
                            b.stop();
                        }
                    }
                    else
                    {
                        autres.set(autres.get() + pg.size());
                    }
                }
            });
            final float x = Math.min(autres.get(), placesDispo - compatibles.get());
            v = v + (compatibles.get()*heuristiqueConfig.bonusMemeDestination+ x)*heuristiqueConfig.bonusPresenceEtageDepart ;
        }
        return v;
    }

    public void aFaitEntrer(final List<AlgoPersonne> plist)
    {
        for(final AlgoPersonne p : plist)
        {
            alg.prisEnCharge(p);
        }
    }


    protected Optional<TrueLivraison> getProchainArretDuContenu(final int etageActuel)
    {
        Optional<TrueLivraison> opt = contenu.getProchainArret(etageActuel,enMontee);
        if(!opt.isPresent())
        {
            opt = contenu.getProchainArret(etageActuel,!enMontee);
        }
        return opt;
    }

    @Override
    public Collection<Choix<Integer, ?>> getPossibilitesProchainArrets(final Predicate<Integer> aFiltrer)
    {
        final List<Choix<Integer, ?>> s = new ArrayList<>(1);
        final int etageActuel = phys().getEtat(id).etageAtteignablePlusProche();
        Integer choix = null;
        Consumer<AscCycleOption> c = Utils.emptyConsumer();
        if(prochainClient == null)
        {
            final Optional<TrueLivraison> opt = getProchainArretDuContenu(etageActuel);
            if(opt.isPresent())
            {
                choix = opt.get().destination;
            }
        }
        else
        {
            choix = prochainClient.getEtage();
        }
        if(choix == null)
        {
            updateVoisinsCycles();
            Optional<AlgoPersGroup> persGroupA = getMeilleurProchainClientAbsolu(contenu, etageActuel, enMontee,false);
            if(!persGroupA.isPresent())
            {
                persGroupA = getMeilleurProchainClientAbsolu(contenu, etageActuel, !enMontee,false);
            }
            if(persGroupA.isPresent())
            {
                c = getProchainClientConsumer(persGroupA.get());
                choix = persGroupA.get().getEtage();
            }
        }
        if(choix != null)
        {
            if(choix > etageActuel)
            {
                enMontee = true;
            }
            else
            {
                enMontee = false;
            }
            
            AscCycleOption concurrent = null;
            if(enMontee && ascSup != null && !ascSup.enMontee)
            {
                concurrent = ascSup;

            }
            if(!enMontee && ascInf != null && ascInf.enMontee)
            {
                concurrent = ascInf;
            }
            
            if(concurrent != null && !concurrent.inactif())
            {
                concurrent.updateCycle();
                if(concurrent.evalRetournementAscAbsolu() > evalRetournementAscAbsolu())
                {
                    choix = null; // on evite la collision en se mettant en attente mais pas inactif
                }
            }
            c = c.andThen(getConsumerSetMontee(enMontee));
        }
        if(choix != null && !aFiltrer.test(choix))
        {
            choix = null;
        }
        s.add(new BChoix<>(choix,c));
        return s;
    }

    public boolean inactif()
    {
        return contenu.isEmpty() && prochainClient == null;
    }


    protected Optional<AlgoPersGroup> getMeilleurProchainClientDepuisPosActuelle(final ContenuAsc contenuAsc,final int etageAscenseurActuelExclu, final boolean monte)
    {

        final int bound = boundRechercheProchainClient(contenuAsc,etageAscenseurActuelExclu,monte);
        int fromKey;
        int toKey;
        if(monte)
        {
            fromKey = etageAscenseurActuelExclu+1;
            toKey = bound +1;
        }
        else
        {
            fromKey = etageAscenseurActuelExclu-1;
            toKey = bound - 1; // car descendingmap (les indices sont parcouru à l'envers)
        }
        final Stream<FloatComparable<AlgoPersGroup>> stream = premierFiltrageGroupes(contenuAsc,pools.get(0).getGroupStream(fromKey, toKey, monte).filter(prochainClientVoisinFilter),50).sorted().limit(15);

        return ajouteNotesProchainClient(contenuAsc, stream, monte).sorted().findFirst().map(fc -> fc.v);
    }

    protected Optional<AlgoPersGroup> getMeilleurProchainClientAbsolu(final ContenuAsc contenuAsc,final int etageAscActuel, final boolean monte, final boolean excludeEtageActuel)
    {
        final int bound = boundRechercheProchainClient(contenuAsc,etageAscActuel,monte);
        int fromKey;
        int toKey;
        if(monte)
        {
            fromKey = Integer.MIN_VALUE;
            toKey = bound +1;
        }
        else
        {
            fromKey = Integer.MAX_VALUE;
            toKey = bound - 1;
        }
        Stream<AlgoPersGroup> stream = pools.get(0).getGroupStream(fromKey, toKey, monte).filter(prochainClientVoisinFilter);
        if(excludeEtageActuel)
        {
            stream = stream.filter(p -> p.getEtage() != etageAscActuel);
        }
        return ajouteNotesProchainClient(contenuAsc, premierFiltrageGroupes(contenuAsc,stream,50).sorted(), monte).sorted().findFirst().map(fc -> fc.v);
    }

    protected int boundRechercheProchainClient(final ContenuAsc contenuAsc, final int etageActuel,final boolean monte)
    {
        final Optional<TrueLivraison> opt = contenuAsc.getProchainArret(etageActuel, monte);
        if(opt.isPresent())
        {
            return opt.get().destination;
        }
        else
        {
            if(monte)
            {
                return Integer.MAX_VALUE -10; // pour éviter les problèmes avec MaxValue + 1;
            }
            else
            {
                return Integer.MIN_VALUE +10;
            }
        }
    }


    protected Stream<FloatComparable<AlgoPersGroup>> ajouteNotesProchainClient(final ContenuAsc contenuAsc,final Stream<FloatComparable<AlgoPersGroup>> stream, final boolean monte)
    {
        final int etageActuel = phys().getEtat(id).etageAtteignablePlusProche();
        final Stream<FloatComparable<AlgoPersGroup>> s = stream.map(cpg ->
        {
            final Cycle c = new ContenuEtPrevision(cpg.v, contenuAsc).getSuiteEtages(monte, etageActuel);
            return new FloatComparable<>(cpg.v,cpg.evaluation*noteCycle(c)*noterEtageDeDepart(contenuAsc, cpg.v, monte));
        });
        return s;
    }



    protected float evalRetournementAscAbsolu()
    {
        final int position = phys().getEtat(id).etageAtteignablePlusProche();
        int distRetournement;
        if(enMontee)
        {
            distRetournement = pools.get(0).etageMax() - position;
        }
        else
        {
            distRetournement = position - pools.get(0).etageMin();
        }
        return 1f - (float)distRetournement/((float)pools.get(0).distanceTotale());
    }

    protected float noterChangementDeSens(final int etage, final boolean monteeVersDescente)
    {
        int distRetournement;
        if(monteeVersDescente)
        {
            distRetournement = pools.get(0).borneSupClients - etage;
        }
        else
        {
            distRetournement = etage - pools.get(0).borneInfClients;
        }

        return 1f - (float)distRetournement/((float)pools.get(0).distanceTotale());
    }

    protected float notePrisePersSens(final boolean sens)
    {
        if(sens)
        {
            if(ascSup != null)
            {
                return ascSup.notePrisePersSens(sens);
            }
            else
            {
                if(sens == enMontee)
                {
                    return 1f;
                }
                else
                {
                    return evalRetournementAscAbsolu();
                }
            }
        }
        else
        {
            if(ascInf != null)
            {
                return ascInf.notePrisePersSens(sens);
            }
            else
            {
                if(sens == enMontee)
                {
                    return 1f;
                }
                else
                {
                    return evalRetournementAscAbsolu();
                }
            }
        }
    }

    protected Stream<FloatComparable<AlgoPersGroup>> premierFiltrageGroupes(final ContenuAsc contenuAsc, final Stream<AlgoPersGroup> str, final int innerLimit)
    {
        final ContenuEtPrevision cp = new ContenuEtPrevision(null, contenuAsc);
        return str.
            map(pg ->
            {
                final int tailleCourse = Math.min(pg.size(), contenuAsc.placesDispo());
                final float x = noterGroupeVsPlaces(pg, contenuAsc.placesDispo())*noterRentabiliteCourseContenant(tailleCourse)*notePrisePersSens(pg.monte());
                return new FloatComparable<>(pg,x);
            }).sorted().limit(innerLimit).map(cpg -> new FloatComparable<>(cpg.v, cpg.evaluation*noterEnchainement(cp, cpg.v)));
    }

    protected float noteRelationCycles(final Cycle cinf, final Cycle csup)
    {
        float x = 1f;
        if(cinf.cycleMontee)
        {

            List<Integer> arretsAscSupEnDescenteAComparer;
            if(csup.cycleMontee)
            {
                x*=noteArretsEnMontee(cinf.avantDemiTour, csup.avantDemiTour);
                arretsAscSupEnDescenteAComparer = csup.apresDemiTour;
            }
            else
            {
                x*=Utils.pow(heuristiqueConfig.malusCollision, cinf.avantDemiTour.size());
                arretsAscSupEnDescenteAComparer = csup.avantDemiTour;
            }
            x *= noteArretsEnDescente(cinf.apresDemiTour,arretsAscSupEnDescenteAComparer);
        }
        else
        {
            List<Integer> arretsAscSupEnmonteeAComparer;
            if(csup.cycleMontee)
            {
                x *= Utils.pow(heuristiqueConfig.malusCollision, cinf.avantDemiTour.size());
                arretsAscSupEnmonteeAComparer = csup.avantDemiTour;
            }
            else
            {
                x *= noteArretsEnDescente(cinf.apresDemiTour, csup.avantDemiTour);
                arretsAscSupEnmonteeAComparer = csup.apresDemiTour;
            }
            x *= noteArretsEnMontee(cinf.apresDemiTour, arretsAscSupEnmonteeAComparer);
        }
        return x;
    }

    protected float noteCycle(final Cycle c)
    {
        float x = 1f;
        float y = 1f;
        if(ascSup!=null)
        {
            x = noteRelationCycles(c,ascSup.getCycleActuel());
        }
        if(ascInf != null)
        {
            y = noteRelationCycles(ascInf.getCycleActuel(), c);
        }
        return x*y;
    }

    public Cycle getCycleActuel()
    {
        return tempCycle;
    }

    public void verouillerProchainClient(final AlgoPersGroup prochainClient)
    {
        final int n = Math.min(contenu.placesDispo(), prochainClient.size());

        final AlgoPersGroup pc = prochainClient.subGroup(n);
        for(final AlgoPersonne p : pc)
        {

            alg.prisEnCharge(p);
        }
        this.prochainClient = pc;
    }


    public List<AlgoPersonne> getTousInvites(final List<AlgoPersonne> base, final ContenuAsc contenuBase, final ContenuAsc contenuFinal)
    {
        final List<AlgoPersonne> retour = new ArrayList<>(base);
        contenuFinal.dumpDifference(contenuBase, retour);
        return retour;
    }


    public void updateCycle()
    {
        tempCycle = new ContenuEtPrevision(prochainClient, contenu).getSuiteEtages(enMontee, phys().getEtat(id).etageAtteignablePlusProche());
    }

    protected float noteArretsEnMontee(final List<Integer> arretsAscInf,final List<Integer> arretsAscSup)
    {
        return noteArrets(arretsAscInf,arretsAscSup,(x,y) -> x <=y);
    }

    protected float noteArretsEnDescente(final List<Integer> arretsAscInf,final List<Integer> arretsAscSup)
    {
        return noteArrets(arretsAscSup,arretsAscInf,(x,y) -> x >= y);
    }

    protected float noteArrets(final List<Integer> arretsAscPoursuivant, final List<Integer> arretsAscPoursuivi, final BiFunction<Integer, Integer, Boolean> comparaison)
    {
        int j = 0;
        final int n = arretsAscPoursuivi.size();
        int decalage = 0;
        int totalMalusPositif = 0;
        int totalMalusNegatif = 0;
        for(final Integer i : arretsAscPoursuivant)
        {
            while(j < n && comparaison.apply(arretsAscPoursuivi.get(j),i))
            {
                j ++;
                decalage ++;
            }
            if(decalage > 0)
            {
                totalMalusPositif ++;
            }
            else if(decalage < 0)
            {
                totalMalusNegatif ++;
            }
            decalage --;
        }
        final float v = Utils.pow(heuristiqueConfig.malusCollision, totalMalusPositif)* Utils.pow(heuristiqueConfig.malusEloignement, totalMalusNegatif);
        return v;
    }

    @Override
    public String toString()
    {
        return "contenu [" + contenu + "] prochainClient " + prochainClient;
    }


    @Override
    public Collection<Choix<Integer, ?>> getPossibilitesPositionAttente()
    {
    	final int etageActuel = phys().getEtat(id).etageAtteignablePlusProche();
        int v = 0;
        Optional<TrueLivraison> opt = contenu.getProchainArret(etageActuel, this.enMontee);
        if(opt.isPresent())
        {
            v = opt.get().destination;
        }
        //final Choix<Integer, ?> c = new BChoix<>(v,debugConsumer(" choix attente par " + id));
        final List<Choix<Integer, ?>> s = new ArrayList<>(1);
        s.add(new BChoix<>(v));
        return s;
        
    }

    @Override
    public void noticeOuverture(final int niveau)
    {
        contenu.noticeArriveEtage(niveau);
    }

    public static <T> Comparator<FloatComparable<T>> getFloatComparator()
    {
        return new Comparator<AscCycleOption.FloatComparable<T>>()
        {

            @Override
            public int compare(final FloatComparable<T> o1, final FloatComparable<T> o2)
            {
                return o1.compareTo(o2);
            }

        };
    }

    final class FloatComparable<T> implements Comparable<FloatComparable<T>>
    {
        final T v;
        final float evaluation;



        public FloatComparable(final T v, final float evaluation)
        {
            this.v = v;
            this.evaluation = evaluation;
        }

        @Override
        public int compareTo(final FloatComparable<T> o)
        {
            return Float.compare(o.evaluation,evaluation);// inversé car on aura toujours besoin de l'ordre décroissant
        }

        @Override
        public String toString()
        {
            return "FC eval " + evaluation + " v " + v;
        }
    }

    @Override
    public void printDebug(final InterfacePhysique debugOutput)
    {
        /*
        if(contenu.livraisons.values().stream().anyMatch(livraison -> livraison.contenu.stream().anyMatch(p -> !debugOutput.contient(id, p.id))))
        {
            throw new SimulateurAcceptableException( id + " contenu incorrect " + contenu);
        }// marche mal */
        //debugOutput.println(" contenu " + contenu + " prochainClient " + prochainClient);
        //System.out.println(" contenu " + contenu + " prochainClient " + prochainClient);
    }
}

