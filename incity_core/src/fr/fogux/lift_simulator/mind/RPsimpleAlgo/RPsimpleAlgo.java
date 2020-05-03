package fr.fogux.lift_simulator.mind.RPsimpleAlgo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.basic.AscenseurDevin;
import fr.fogux.lift_simulator.mind.basic.DestinationSimple;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;
import fr.fogux.lift_simulator.structure.EtatAsc;

public class RPsimpleAlgo extends Algorithme
{
    public List<TripletPnd> demandesEnCours = new ArrayList<TripletPnd>();
    public monAscenseur[] [] ascenseursArray = new monAscenseur[4] [2];

    public RPsimpleAlgo(final InterfacePhysique output, final ConfigSimu config)
    {
        super(output, config);
    }

	@Override
    public long init()
    {
		for (int j = 0; j < 2; j++) {	// 1 à changer en 0 ! ! !
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n 
				AscId id = new AscId(i, j);
				List<Integer> destinations = new ArrayList<Integer> ();
				int d = -1 + 2*i + 3*j;
				destinations.add(d);
				output.changerDestination(id, d, true);
				ascenseursArray[i] [j] = new monAscenseur(id, destinations, true);
				
				//System.out.systemPrintLn(" SALUT ! ");
				//System.out.systemPrintLn("   ------    " + ascenseursArray);
				//System.out.systemPrintLn("  ");
			}
		}
		output().systemPrintLn(" INIT --------------------------------------    ");
		
		return 1000;	// Durée entre deux Ping()
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
    	output().systemPrintLn(" New Person --------------------------------------    ");
    	demandesEnCours.add(new TripletPnd(idPersonne, niveau, destination));
//    	int stack;
//    	if (niveau < 10) {
//    		stack = 0;			
//		} else {
//			stack = 1;
//		}
//    	monAscenseur cetAscenseur = ascenseursArray[idPersonne % 4] [stack];
//    	List<Integer> l = cetAscenseur.destinations;
//    	l.add(niveau);
//    	if (l.size() == 1) {
//    		output.changerDestination(new AscId(idPersonne % 4, stack), l.remove(0), true);
//		}
    }

    @Override
    public Collection<Integer> listeInvites(final AscId idAsc, final int places_disponibles, final int niveau)
    {    	
    	//int niveau = Math.round(output.getEtat(idAsc).positionActuelle);
    	output().systemPrintLn(" New Invites --------------------------------------    ");
    	
		monAscenseur cetAscenseur = ascenseursArray[idAsc.monteeId] [idAsc.stackId];
    	List<Integer> listInvites = new ArrayList<Integer>();
    	for (int i = 0; i < cetAscenseur.destinations.size(); i++) {
			if (cetAscenseur.destinations.get(i) == niveau) {
				cetAscenseur.destinations.remove(i);
			}
		}
    	
		int i = 0;
		int n = 0;

		while (i < demandesEnCours.size() && n < places_disponibles) {
			
			TripletPnd myPerson = demandesEnCours.get(i);
			boolean veutMonter = myPerson.destination - myPerson.niveau > 0;
			//output().systemPrintLn(" " + niveau + " vs. " + myPerson.niveau + "  " + " ");

			if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee) || (!veutMonter && !cetAscenseur.enMontee) )) {
				demandesEnCours.remove(i);
				n++;
				listInvites.add(myPerson.id);
				cetAscenseur.destinations.add(myPerson.destination);
				//output().systemPrintLn(" " + demandesEnCours.size() + " " + places_disponibles + "  " + "IF");
			}
			i++;
		}
    	return listInvites;
        
    }

	@Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
		output().systemPrintLn(" arretSansOuverture ! ! ! BIG Pb. ... J       V       U          V           H  H J V B J I BH I B I  UB I U  B H I IY V U  V ");

    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, int t)
    {
//    	monAscenseur cetAscenseur = ascenseursArray[idAscenseur.monteeId] [idAscenseur.stackId];
//    	if (cetAscenseur.destinations.size() > 0) {
//    		output.changerDestination(idAscenseur, cetAscenseur.destinations.remove(0), true);
//    	}
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

	@Override
	public void ping() {
//		int kTest = 3;
//		if (nTest % kTest == 0) {
//			output.changerDestination(new AscId((nTest / kTest) % 4, (nTest / kTest) % 2), (nTest / kTest) % 20, true);
//		}
//		nTest++;
		output.println(" PING --------------------------------------    ");
		for (int j = 0; j < 2; j++) {	// 1 à changer en 0 ! ! !
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n
				monAscenseur cetAscenseur = ascenseursArray[i] [j];
				AscId id = new AscId(i, j);
				
				int niveau = cetAscenseur.niveau(output);
				
				boolean someoneWaitsFurther = false;
				boolean someoneWaitsHere = false;
				int maxLevel = cetAscenseur.maxLevel(ascenseursArray, output, config);
				int minLevel = cetAscenseur.minLevel(ascenseursArray, output, config);
				
				for (int k = 0; k < demandesEnCours.size(); k++) {
					
					TripletPnd myPerson = demandesEnCours.get(k);
					boolean veutMonter = myPerson.destination - myPerson.niveau > 0;
					//output().systemPrintLn(" " + niveau + " vs. " + myPerson.niveau + "  " + " ");

					
					if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee) || (!veutMonter && !(cetAscenseur.enMontee)) )) {
						someoneWaitsHere = true;
						
					} else if ( (maxLevel > myPerson.niveau && myPerson.niveau > niveau  && cetAscenseur.enMontee)
							|| (minLevel < myPerson.niveau && myPerson.niveau < niveau  && !cetAscenseur.enMontee) ) {
						someoneWaitsFurther = true;
					}
				}
				output().systemPrintLn("  " + output().getNbPersonnes(id) + "   ----------   " + config.nbPersMaxAscenseur());
				if (cetAscenseur.arretDemande(niveau) || (someoneWaitsHere && output().getNbPersonnes(id) < config.nbPersMaxAscenseur())) {
					output.changerDestination(id, niveau, true);	// STOP
					output.println(" STOP     " + cetAscenseur.destinations);
				} else if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, output, config) || someoneWaitsFurther) {
					output.changerDestination(id, (cetAscenseur.enMontee ? Math.min(maxLevel, niveau + 1) : Math.max(minLevel, niveau - 1)), true);	// CONTINUE
					output.println(" CONTINUE " + cetAscenseur.destinations);
				} else {						// VERIFIER QUE ascenseur NON BLOQUE entre Plafond et autre Ascenseur
					cetAscenseur.enMontee = !cetAscenseur.enMontee;
					output.changerDestination(id, (cetAscenseur.enMontee ? Math.min(maxLevel, niveau + 1) : Math.max(minLevel, niveau - 1)), true); // CHANGE DE DIRECTION
					output.println(" C d D    " + cetAscenseur.destinations);
				} 				
			}
		}
	}
}
