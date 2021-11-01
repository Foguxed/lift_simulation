package fr.fogux.lift_simulator.mind.ascenseurs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import fr.fogux.lift_simulator.mind.algorithmes.SetPool;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.OutputProvider;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

/**
 * NearestNeighbor implémenté de manière a ce que l'algorithme choisisse le client le plus proche de l'ascenseur, mais
 * prend également en compte la distance entre les destinations du client et des personnes dans le contenu de l'ascenseur
 *
 */
public class NearestNeighborAsc extends AscPoolUser<SetPool>
{
	protected boolean sansTache = true;
	protected Set<AlgoPersonne> contenu;
	protected Set<AlgoPersonne> reservees;
	protected final int memeEtageBonus = - 4; // en etages
	protected final int etageAscenseurBonus = -6;
	protected final int multDistanceDepartAuContenu = 50;
	protected final int multDistanceArriveeAuContenu = 40;

	protected final int multDistanceDepartDepartReservation = 40;
	protected final int multDistanceArriveeArriveeReservation = 25;
	protected final int multDistanceArriveeDepartReservation = 15;
	
	protected final int multDistanceAsc = 100;
	
	public NearestNeighborAsc(AscId id, ConfigSimu config, OutputProvider outputProvider, VoisinAsc ascPrecedent) 
	{
		super(id, config, outputProvider, ascPrecedent);
		contenu = new HashSet<>();
		reservees = new HashSet<>();
	}

	@Override
	public List<Integer> getInvites(int niveau, int placesDispo) 
	{
		List<Integer> invites = new ArrayList<Integer>();
		contenu.removeIf(p -> p.destination == niveau);
		choisirReservations(phys().getEtat(id));
		reservees.stream().filter(p -> p.depart == niveau).limit(placesDispo).forEach(p->
			{
				invites.add(p.id);
				contenu.add(p);
			});
		
		reservees.removeIf(p -> invites.contains(p.id));
		return invites;
	}

	@Override
	public void poolHasBeenUpdated() 
	{
		if(sansTache)
		{
			sansTache = false;
			reflechir();
		}
	}
	
	protected int scoreTrajet(int depart, int destination, EtatAsc etat)
	{
		int score = 0;
		score += scoreDistanceAscenseur(depart,(int)Math.round(etat.positionActuelle));
		for(AlgoPersonne p : contenu)
		{
			score += scoreDistance(p.destination,depart,multDistanceDepartAuContenu);
			score += scoreDistance(p.destination,destination,multDistanceArriveeAuContenu);
		}
		for(AlgoPersonne p : reservees)
		{
			scoreDistance(p.depart,depart, multDistanceDepartDepartReservation);
			scoreDistance(p.destination,destination, multDistanceDepartDepartReservation);
			scoreDistance(p.destination,depart, multDistanceArriveeDepartReservation);
			scoreDistance(p.depart,destination, multDistanceArriveeDepartReservation);
		}
		return score;
	}
	
	protected int scoreDistance(int etage1, int etage2, int multiplicateur)
	{
		if(etage1 == etage2)
		{
			return multiplicateur*memeEtageBonus;
		}
		else return multiplicateur*Math.abs(etage1 - etage2);
	}
	
	protected int scoreDistanceAscenseur(int etage1, int etage2)
	{
		if(etage1 == etage2)
		{
			return multDistanceAsc*etageAscenseurBonus;
		}
		return multDistanceAsc*Math.abs(etage1 - etage2);
	}
	
	protected void choisirReservations(EtatAsc etatasc)
	{
		while(contenu.size() + reservees.size() < config.nbPersMaxAscenseur())
		{
			int minscore = Integer.MAX_VALUE;
			AlgoPersonne res = null;
			for(SetPool pool : pools) // une pool est un ensemble de personne que peut désservir cet ascenseur
			{
				for(AlgoPersonne p : pool.set)
				{
					int score = scoreTrajet(p.depart,p.destination,etatasc);
					if(score < minscore)
					{
						minscore = score;
						res = p;
					}
				}
			}
			if(res == null)
			{
				return;
			}
			else
			{
				this.mainAlgo.prisEnCharge(res);
				reservees.add(res);
			}
		}
		phys().println(id + " choisi " + reservees);
	}
	
	@Override
	public Integer prochainArret(Predicate<Integer> aFiltrer) 
	{
		EtatAsc etat = phys().getEtat(id);
		choisirReservations(etat);
		Integer reponse = null;
		int distMin = Integer.MAX_VALUE;
		int tempv;
		for(AlgoPersonne p : contenu)
		{
			if(aFiltrer.test(p.destination))
			{
				tempv = (int) (100*Math.abs(p.destination - etat.positionActuelle));
				if(tempv < distMin)
				{
					distMin = tempv;
					reponse = p.destination;
				}
			}
		}
		for(AlgoPersonne p : reservees)
		{
			if(aFiltrer.test(p.depart))
			{
				tempv = (int) (100*Math.abs(p.depart - etat.positionActuelle));
				if(tempv < distMin)
				{
					distMin = tempv;
					reponse = p.depart;
				}
			}
		}
		if(reponse == null && contenu.size() < config.nbPersMaxAscenseur())
		{
			sansTache = true;
		}
		return reponse;
	}

	@Override
	public Integer positionDattente() 
	{
		return prochainArret(a -> true);
	}
}
