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
//  CECI NE SERT A RIEN :
//	public boolean enMontee() {
//		// TODO Auto-generated method stub
//		return this.enMontee;
//	}
    
// JE NE SAIS PAS CE QUE CA VEUT DIRE :
//		public boolean isEnMontee() {
//			return enMontee;
//		}
//
//		public void setEnMontee(boolean enMontee) {
//			this.enMontee = enMontee;
//		}

	}
