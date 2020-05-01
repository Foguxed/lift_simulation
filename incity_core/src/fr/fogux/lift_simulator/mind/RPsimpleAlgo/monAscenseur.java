package fr.fogux.lift_simulator.mind.RPsimpleAlgo;

import java.util.List;

import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class monAscenseur {
	public final AscId id;
    public final List<Integer> destinations;
    public boolean enMontee;
    
    public monAscenseur(
    		final AscId id,
    		final List<Integer> destinations,
    		boolean enMontee
    		)
    {
        this.destinations = destinations;
		this.enMontee = enMontee;
        this.id = id;
    }

    public List<Integer> getDestinations()
    {
        return null;
    }
    /**
     * Vrai si une personne dans l'ascenseur veut descendre
     * @param niveau
     * @return
     */
    public boolean arretDemande(int niveau) {
    	boolean b = false;
		for (int i = 0; i < this.destinations.size(); i++) {
			if (this.destinations.get(i) == niveau) {
				b = true;
			}
		}
    	return b;
	}

	public boolean arretDemandePlusLoin(int niveau, monAscenseur[] [] ascenseursArray, InterfacePhysique output) {
		boolean b = false;
		int destination = this.destinations.size();
		int maxLevel = this.maxLevel(ascenseursArray,  output);
		int minLevel = this.minLevel(ascenseursArray,  output);
		for (int i = 0; i < destination; i++) {
			if ( (maxLevel > destination && destination > niveau && this.enMontee) 
					|| (minLevel < destination && destination < niveau && !this.enMontee) ) {
				b = true;
			}
		}
    	return b;
		
	}

	public int maxLevel(monAscenseur[] [] ascenseursArray, InterfacePhysique output) {	// COMMENT AVOIR DES VARIABLES GLOBALES PLTÔT ?
		int maxLevel = 20;	// A MODIFIER
		if (this.id.stackId < 1) {	// A MODIFIER AUSSI
			maxLevel = ascenseursArray[this.id.monteeId] [this.id.stackId + 1].niveau(output);
		}
		return maxLevel;
	}

	public int minLevel(monAscenseur[] [] ascenseursArray, InterfacePhysique output) {
		int minLevel = -3;	// A MODIFIER
		if (this.id.stackId > 0) {	// A MODIFIER AUSSI
			minLevel = ascenseursArray[this.id.monteeId] [this.id.stackId - 1].niveau(output);
		}
		return minLevel;
	}

	public int niveau(final InterfacePhysique output) {
		int niveau;
		EtatAsc etatAsc= output.getEtat(id);
		
		if (etatAsc.premierEtageAtteignable == Integer.MIN_VALUE) {
			niveau = Math.round(etatAsc.positionActuelle);	// SI ARRÊTE
		} else {
			niveau = etatAsc.premierEtageAtteignable;	// SI EN MOUVEMENT
		}
		return niveau;
	}



}
