package fr.fogux.lift_simulator.mind.RPsimpleAlgo2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.RPsimpleAlgo.TripletPnd;
import fr.fogux.lift_simulator.mind.basic.AscenseurDevin;
import fr.fogux.lift_simulator.mind.basic.DestinationSimple;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class RPsimpleAlgo2 extends Algorithme
{
    public int[] waitingUp = new int[config.getNiveauMax() - config.getNiveauMin() + 1];
    public int[] waitingDown = new int[config.getNiveauMax() - config.getNiveauMin() + 1];
    public int ground = config.getNiveauMin();
    public List<TripletPnd> demandesEnCours = new ArrayList<TripletPnd>();

    
    public monAscenseur2[] [] ascenseursArray = new monAscenseur2[4] [2];

    public RPsimpleAlgo2(final InterfacePhysique output, final ConfigSimu config)
    {
        super(output, config);
    }

	@Override
    public long init()
    {
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n 
				AscId id = new AscId(i, j);
				boolean[] stops = new boolean[config.getNiveauMax() - config.getNiveauMin() + 1];
				output.changerDestination(id, -1 + 2*i + 3*j, true);
				ascenseursArray[i] [j] = new monAscenseur2(id, stops, true);
			}
		}
		output().systemPrintLn(" INIT --------------------------------------    ");
		return -1;	// Durée entre deux Ping()
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
    	demandesEnCours.add(new TripletPnd(idPersonne, niveau, destination));
    	if (destination - niveau > 0) {
			waitingUp[niveau - ground] += 1;
		} else {
			waitingDown[niveau - ground] += 1;
		}
    	this.ping();
    }

    @Override
    public Collection<Integer> listeInvites(final AscId idAsc, final int places_disponibles, final int niveau)
    {    	 	
		monAscenseur2 cetAscenseur = ascenseursArray[idAsc.monteeId] [idAsc.stackId];
    	List<Integer> listInvites = new ArrayList<Integer>();
    	cetAscenseur.stops[niveau - ground] = false;
    	
		int i = 0;
		int n = 0;

		while (i < demandesEnCours.size() && n < places_disponibles) {
			
			TripletPnd myPerson = demandesEnCours.get(i);
			boolean veutMonter = myPerson.destination - myPerson.niveau > 0;

			if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee) || (!veutMonter && !cetAscenseur.enMontee) )) {
				demandesEnCours.remove(i);
				n++;
				listInvites.add(myPerson.id);
				cetAscenseur.stops[myPerson.destination - ground] = true;
				if (veutMonter) {
					waitingUp[niveau - ground] -= 1;
				} else {
					waitingDown[niveau - ground] -= 1;
				}
			}
			i++;
		}
		this.ping();
    	return listInvites;
        
    }

	@Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
		
    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, int t)
    {
    	this.ping();
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

	@Override
	public void ping() {
		
		output.println(" Ping ------------------");
		for (int j = 0; j < 2; j++) {	// 2 à changer en m
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n
				monAscenseur2 cetAscenseur = ascenseursArray[i] [j];
				AscId id = new AscId(i, j);
				
				int niveau = cetAscenseur.niveau(output);
				
				boolean someoneWaitsHere;
				if (cetAscenseur.enMontee) {
					someoneWaitsHere = waitingUp[niveau - ground] > 0;
				} else {
					someoneWaitsHere = waitingDown[niveau - ground] > 0;
				}
				
				int maxLevel = cetAscenseur.maxLevel(ascenseursArray, output, config);
				int minLevel = cetAscenseur.minLevel(ascenseursArray, output, config);
				
				boolean someoneWaitsFurther = false;
				if (cetAscenseur.enMontee) {
					for (int a = niveau - ground + 1; a <= maxLevel - ground; a++) {
						someoneWaitsFurther = someoneWaitsFurther || waitingUp[a] > 0;
					}
				} else {
					for (int a = minLevel - ground; a < niveau - ground ; a++) {
						someoneWaitsFurther = someoneWaitsFurther || waitingDown[a] > 0;
					}
				}
				if (i == 3 && j == 1) {
					output.println(niveau);
					this.intArrayPrinter(waitingUp);
					this.intArrayPrinter(waitingDown);
					this.boolArrayPrinter(cetAscenseur.stops);
					if (someoneWaitsHere) {
						output.println("wh ");
					}
					if (someoneWaitsFurther) {
						output.println("wf ");
					}
					if (cetAscenseur.arretDemande(niveau)) {
						output.println("sh ");
					}
					if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, output, config)) {
						output.println("sf ");
					}
				}
				
				
				if (cetAscenseur.arretDemande(niveau) || (someoneWaitsHere && output().getNbPersonnes(id) < config.nbPersMaxAscenseur())) {
					output.changerDestination(id, niveau, true);	// STOP
					
				} else if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, output, config) || someoneWaitsFurther) {
					int nextStop = Integer.MIN_VALUE;
					if (cetAscenseur.enMontee) {
						for (int n = maxLevel; n > niveau; n--) {
							if (cetAscenseur.stops[n - ground]) {
								nextStop = n;
							}
							if (waitingUp[n - ground] > 0) {
								nextStop = n;
							}
						}
					} else {
						for (int n = minLevel; n < niveau ; n++) {
							if (cetAscenseur.stops[n - ground]) {
								nextStop = n;
							}
							if (waitingDown[n - ground] > 0) {
								nextStop = n;
							}						}
					}
					if (nextStop == Integer.MIN_VALUE) {
						output.println(id + "FSB");	// BLOQUE
					} else {
						output.changerDestination(id, nextStop, true);	// CONTINUE
					}
				} else {						// VERIFIER QUE ascenseur NON BLOQUE entre Plafond et autre Ascenseur
					cetAscenseur.enMontee = !cetAscenseur.enMontee;
					
					int nextStop = Integer.MIN_VALUE;
					if (cetAscenseur.enMontee) {
						for (int n = maxLevel; n > niveau; n--) {
							if (cetAscenseur.stops[n - ground]) {
								nextStop = n;
							}
							if (waitingUp[n - ground] > 0) {
								nextStop = n;
							}
						}
					} else {
						for (int n = minLevel; n < niveau ; n++) {
							if (cetAscenseur.stops[n - ground]) {
								nextStop = n;
							}
							if (waitingDown[n - ground] > 0) {
								nextStop = n;
							}						}
					}
					if (nextStop == Integer.MIN_VALUE) {
						output.println(id + "N OR FSB");	// BLOQUE
					} else {
						output.changerDestination(id, nextStop, true);	// CONTINUE
					}				} 				
			}
		}
	}

	private void intArrayPrinter(int[] bools) {
		int p = 0;
		int a = 1;
		for (int i = 0; i < bools.length - 3; i++) {
			p += bools[i]*a;
			a *= 10;
		}
		p += a;
		output.println(p);
	}
	
	private void boolArrayPrinter(boolean[] stops) {
		int p = 0;
		int a = 1;
		for (int i = 0; i < stops.length - 3; i++) {
			p += (stops[i] ? 7 : 0)*a;
			a *= 10;
		}
		p += a;
		output.println(p);
	}
}
