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
    //public Map<AscId, monAscenseur> mesAscenseurs = new HashMap<AscId, monAscenseur>();
    public monAscenseur[] [] ascenseursArray = new monAscenseur[4] [2];
    public Integer nTest = 0; // to remove

    public RPsimpleAlgo(final InterfacePhysique output, final ConfigSimu config)
    {
        super(output, config);
        // A VIRER :
        final List<DestinationSimple> destinationsSup = new ArrayList<>();
        destinationsSup.add(new DestinationSimple(0,4));
        final List<DestinationSimple> destinationsInf = new ArrayList<>();
        destinationsInf.add(new DestinationSimple(12));
        
    }

	@Override
    public void init()
    {
		for (int j = 0; j < 2; j++) {	// 1 à changer en 0 ! ! !
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n 
				AscId id = new AscId(i, j);
				List<Integer> destinations = new ArrayList<Integer> ();
				//int d = 2 + 3*i + 4*j;
				//destinations.add(d);
				ascenseursArray[i] [j] = new monAscenseur(id, destinations, true);
				//output.changerDestination(id, d, true);
				//System.out.systemPrintLn(" SALUT ! ");
				//System.out.systemPrintLn("   ------    " + ascenseursArray);
				//System.out.systemPrintLn("  ");
			}
		}
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
    	demandesEnCours.add(new TripletPnd(idPersonne, niveau, destination));
//    	int stack;
//    	if (niveau < 10) {
//    		stack = 0;			
//		} else {
//			stack = 1;
//		}
//    	output.changerDestination(new AscId(idPersonne % 4, stack), niveau, true);
    }

    @Override
    public Collection<Integer> listeInvites(final AscId idAsc, final int places_disponibles)
    {    	
    	int niveau = Math.round(output.getEtat(idAsc).positionActuelle);
		monAscenseur cetAscenseur = ascenseursArray[idAsc.monteeId] [idAsc.stackId];
    	List<Integer> listInvites = new ArrayList<Integer>();
		int i = 0;
		int n = 0;
		

		while (i < demandesEnCours.size() && n < places_disponibles) {
			
			TripletPnd myPerson = demandesEnCours.get(i);
			boolean veutMonter = myPerson.destination - myPerson.niveau > 0;
			output().systemPrintLn(" " + niveau + " vs. " + myPerson.niveau + "  " + " ");

			if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee)
					|| (!veutMonter && !(cetAscenseur.enMontee)) )) {
				demandesEnCours.remove(i);
				n++;
				listInvites.add(myPerson.id);
				output().systemPrintLn(" " + demandesEnCours.size() + " " + places_disponibles + "  " + "IF");
			}
			i++;
		}
    	return listInvites;
        
    }

	@Override
    public void arretSansOuverture(final AscId idAscenseur)
    {
		output().systemPrintLn(" arretSansOuverture ! ! ! ");

    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur)
    {
        
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
		for (int j = 1; j < 2; j++) {	// 1 à changer en 0 ! ! !
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n
				monAscenseur cetAscenseur = ascenseursArray[i] [j];
				AscId id = new AscId(i, j);
				
				int niveau = cetAscenseur.niveau(output);
				
				boolean someoneWaitsFurther = false;
				boolean someoneWaitsHere = false;
						
				for (int k = 0; k < demandesEnCours.size(); k++) {
					
					TripletPnd myPerson = demandesEnCours.get(k);
					boolean veutMonter = myPerson.destination - myPerson.niveau > 0;
					//output().systemPrintLn(" " + niveau + " vs. " + myPerson.niveau + "  " + " ");

					int maxLevel = cetAscenseur.maxLevel(ascenseursArray, output);
					int minLevel = cetAscenseur.minLevel(ascenseursArray, output);
					if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee) || (!veutMonter && !(cetAscenseur.enMontee)) )) {
						someoneWaitsHere = true;
						
					} else if ( (maxLevel > myPerson.niveau && myPerson.niveau > niveau  && cetAscenseur.enMontee)
							|| (minLevel < myPerson.niveau && myPerson.niveau < niveau  && !cetAscenseur.enMontee) ) {
						someoneWaitsFurther = true;
					}
				}
				output().systemPrintLn("  " + output().getNbPersonnes(id) + "   ------    " + config.nbPersMaxAscenseur());
				if (cetAscenseur.arretDemande(niveau) || (someoneWaitsHere && output().getNbPersonnes(id) < config.nbPersMaxAscenseur())) {
					output.changerDestination(id, niveau, true);	// STOP
					//output().systemPrintLn("  " + niveau + " STOP  ");
				} else if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, output) || someoneWaitsFurther) {
					output.changerDestination(id, (cetAscenseur.enMontee ? niveau + 1 : niveau - 1), true);	// CONTINUE
					//output().systemPrintLn("  " + niveau + " CONTINUE  ");
				} else {
					cetAscenseur.enMontee = !cetAscenseur.enMontee;
					output.changerDestination(id, (cetAscenseur.enMontee ? niveau + 1 : niveau - 1), true); // CHANGE DE DIRECTION
					//output().systemPrintLn("  " + niveau + " CHANGE DE DIRECTION  ");
				} 				
			}
		}
	}
}
