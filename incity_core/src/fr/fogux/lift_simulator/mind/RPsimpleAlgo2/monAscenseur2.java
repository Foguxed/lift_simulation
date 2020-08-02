package fr.fogux.lift_simulator.mind.RPsimpleAlgo2;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class monAscenseur2 {
	public final AscId id;
	public boolean[] stops;
	public boolean enMontee;
	public int ground = -1; // -1 � changer absolument si on change la config
    
    public monAscenseur2(final AscId id, boolean[] stops, boolean enMontee)
    {
        this.id = id;
        this.stops = stops;
		this.enMontee = enMontee;
    }

    /**
     * Vrai si une personne dans l'ascenseur veut descendre au niveau
     * @param niveau
     * @return
     */
    public boolean arretDemande(int niveau) {
    	return this.stops[niveau - ground];	
	}

	public boolean arretDemandePlusLoin(int niveau, monAscenseur2[] [] ascenseursArray, final InterfacePhysique output, final ConfigSimu config) {
		boolean b = false;
		int maxLevel = this.maxLevel(ascenseursArray,  output, config);
		int minLevel = this.minLevel(ascenseursArray,  output, config);
		if (this.enMontee) {
			for (int i = niveau - ground + 1; i <= maxLevel - ground; i++) {
				b = b || this.stops[i];
			}
		} else {
			for (int i = minLevel - ground; i < niveau - ground ; i++) {
				b = b || this.stops[i];
			}
		} 
    	return b;
		
	}

	public int maxLevel(monAscenseur2[] [] ascenseursArray, InterfacePhysique output, final ConfigSimu config) {	// COMMENT AVOIR DES VARIABLES GLOBALES PLT�T ?
		int maxLevel = config.getNiveauMax();	// A MODIFIER
		if (this.id.stackId < 1) {	// A MODIFIER AUSSI
			maxLevel = ascenseursArray[this.id.monteeId] [this.id.stackId + 1].niveau(output);
		}
		return maxLevel;
	}

	public int minLevel(monAscenseur2[] [] ascenseursArray, InterfacePhysique output, final ConfigSimu config) {
		int minLevel = config.getNiveauMin();	// A MODIFIER
		if (this.id.stackId > 0) {	// A MODIFIER AUSSI
			minLevel = ascenseursArray[this.id.monteeId] [this.id.stackId - 1].niveau(output);
		}
		return minLevel;
	}

	public int niveau(final InterfacePhysique output) {
		int niveau;
		EtatAsc etatAsc = output.getEtat(id);
		
		if (etatAsc.premierEtageAtteignable == Integer.MIN_VALUE) {
			if (Math.abs(etatAsc.positionActuelle - Math.round(etatAsc.positionActuelle)) > 0.001) {
				output.println(id + " PB : position actuelle != int");
			}
			niveau = Math.round(etatAsc.positionActuelle);	// SI ARR�TE
		} else {
			niveau = etatAsc.premierEtageAtteignable;	// SI EN MOUVEMENT
		}
		//output.systemPrintLn(niveau + " �tat : " + output.getEtat(id).etat + etatAsc.premierEtageAtteignable);
		if (niveau > 10) {
			niveau = 10;		//PB. : ERREUR premierEtageAtteignable > maxLevel
		}
		if (niveau < -1) {
			niveau = -1;		//PB. : ERREUR premierEtageAtteignable > maxLevel
		}
		return niveau;
	}



}
