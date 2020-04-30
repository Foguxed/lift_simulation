package fr.fogux.lift_simulator.mind.RPsimpleAlgo;

import java.util.List;

import fr.fogux.lift_simulator.structure.AscId;

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

	public boolean arretDemandePlusLoin(int niveau) {
		boolean b = false;
		int destination = this.destinations.size();
		for (int i = 0; i < destination; i++) {
			if ( (destination > niveau && this.enMontee) 
					|| (destination < niveau && !this.enMontee) ) {
				b = true;
			}
		}
    	return b;
		
	}



}
