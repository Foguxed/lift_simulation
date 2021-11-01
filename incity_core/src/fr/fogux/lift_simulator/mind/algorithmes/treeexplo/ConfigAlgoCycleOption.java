package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import java.util.ArrayList;
import java.util.List;

public class ConfigAlgoCycleOption
{
	/* pour asc twin : 83 0.7,0.9975,0.9600001,1.2749999,2.0,0.25,0.76,6.65,6.9000006, val 243733672
    avec     protected static final float marge = 0.1f;
    protected static final int limitNbOptions = 3;
    public static final int structCapacity = 1000;
    public static final long maxForecastTime = 1000*60*3;
    
    et en revenant à 0 sauf si prochain arret du contenu vide
    
    
    614 0.658,0.969,0.5760004,1.1100004,2.2500002,0.27499998,0.7040002,6.7200003,5.88, val 285897333
    1720 0.658,0.969,0.5600004,1.0500004,1.8500006,0.27999997,0.7040002,6.7200003,5.4000006, val 285417654
    */public final float malusCollision;//0.2
    public final float malusEloignement ; //0.9

    public final float malusPlacesLibres;

    public final float bonusGroupeBonneTaille;
    public final float bonusMemeDestination;


    public final float bonusPresenceEtageDepart;

    public final float malusPlacesNonUtilisees;//0.93

    public final float equivalentDistanceUnArret;

    public final float flatBonusLivraisonPersonne;

    public ConfigAlgoCycleOption()
    {
        malusCollision = 0.7f;
        malusEloignement = 0.95f;
        malusPlacesLibres = 0.8f;
        bonusGroupeBonneTaille = 1.5f;
        bonusMemeDestination = 2.5f;
        bonusPresenceEtageDepart = 0.25f;
        malusPlacesNonUtilisees = 0.8f;
        equivalentDistanceUnArret = 7f;
        flatBonusLivraisonPersonne = 6f;
    }
    
    

    public ConfigAlgoCycleOption(double malusCollision, double malusEloignement, double malusPlacesLibres,
    		double bonusGroupeBonneTaille, double bonusMemeDestination, double bonusPresenceEtageDepart,
			double malusPlacesNonUtilisees, double equivalentDistanceUnArret, double flatBonusLivraisonPersonne) {
		super();
		this.malusCollision = (float)malusCollision;
		this.malusEloignement = (float)malusEloignement;
		this.malusPlacesLibres = (float)malusPlacesLibres;
		this.bonusGroupeBonneTaille = (float)bonusGroupeBonneTaille;
		this.bonusMemeDestination = (float)bonusMemeDestination;
		this.bonusPresenceEtageDepart = (float)bonusPresenceEtageDepart;
		this.malusPlacesNonUtilisees = (float)malusPlacesNonUtilisees;
		this.equivalentDistanceUnArret = (float)equivalentDistanceUnArret;
		this.flatBonusLivraisonPersonne = (float)flatBonusLivraisonPersonne;
	}

    public static ConfigAlgoCycleOption optimiseePourAscSuperposesSeul()
    {
    	return new ConfigAlgoCycleOption(0.658f,0.969f,0.5760004f,1.1100004f,2.2500002f,0.27499998f,0.7040002f,6.7200003f,5.88f);
    }
    
    public static ConfigAlgoCycleOption optimiseePourAscSuperposesTreeAlg()
    {
    	return new ConfigAlgoCycleOption(0.7,0.9975,0.9600001,1.2749999,2.0,0.25,0.76,6.65,6.9000006);
    }
    
    public static ConfigAlgoCycleOption pourIncity()
    {
    	return new ConfigAlgoCycleOption(0.714,0.95760006,0.9600001,1.2749999,2.0,0.25,0.7752,6.783,6.9000006);
    }

	public ConfigAlgoCycleOption(final List<FloatGradParam> params)
    {
        malusCollision = params.get(0).value;//0.2
        malusEloignement = params.get(1).value; //0.9

        malusPlacesLibres = params.get(2).value;

        bonusGroupeBonneTaille = params.get(3).value;
        bonusMemeDestination = params.get(4).value;


        bonusPresenceEtageDepart = params.get(5).value;

        malusPlacesNonUtilisees = params.get(6).value;//0.93

        equivalentDistanceUnArret = params.get(7).value;

        flatBonusLivraisonPersonne = params.get(8).value;
    }

    public List<FloatGradParam> toGradParamList()
    {
        final List<FloatGradParam> retour = new ArrayList<>();
        retour.add(new FloatGradParam(malusCollision));
        retour.add(new FloatGradParam(malusEloignement));
        retour.add(new FloatGradParam(malusPlacesLibres));
        retour.add(new FloatGradParam(bonusGroupeBonneTaille));
        retour.add(new FloatGradParam(bonusMemeDestination));
        retour.add(new FloatGradParam(bonusPresenceEtageDepart));
        retour.add(new FloatGradParam(malusPlacesNonUtilisees));
        retour.add(new FloatGradParam(equivalentDistanceUnArret));
        retour.add(new FloatGradParam(flatBonusLivraisonPersonne));
        return retour;
    }
}
