package fr.fogux.lift_simulator.mind.RPsimpleAlgo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.basic.AscenseurDevin;
import fr.fogux.lift_simulator.mind.basic.DestinationSimple;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public class RPsimpleAlgo extends Algorithme
{
    public List<TripletPnd> demandesEnCours = new ArrayList<TripletPnd>();
    //public Map<AscId, monAscenseur> mesAscenseurs = new HashMap<AscId, monAscenseur>();
    public monAscenseur[] [] ascenseursArray = new monAscenseur[4] [2];

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
		for (int j = 0; j < 2; j++) {
			
			for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n 
				AscId id = new AscId(i, j);
				List<Integer> destinations = new ArrayList<Integer> ();
				int d = 2 + 3*i + 4*j;
				destinations.add(d);
				ascenseursArray[i] [j] = new monAscenseur(id, destinations, true);
				output.changerDestination(id, d, true);
				System.out.println(" SALUT ! ");
				System.out.println("   ------    " + ascenseursArray);
				System.out.println("  ");
			}
		}
    }

    @Override
    public void appelExterieur(final int idPersonne, final int niveau, final int destination)
    {
    	demandesEnCours.add(new TripletPnd(idPersonne, niveau, destination));
    	int stack;
    	if (niveau < 10) {
    		stack = 0;			
		} else {
			stack = 1;
		}
    	output.changerDestination(new AscId(idPersonne % 4, stack), niveau, true);
    }

    @Override
    public Collection<Integer> listeInvites(final AscId idAsc, final int places_disponibles)
    {    	
		int niveau = output().getEtat(idAsc).premierEtageAtteignable;
		monAscenseur cetAscenseur = ascenseursArray[idAsc.monteeId] [idAsc.stackId];
    	List<Integer> listInvites = new ArrayList<Integer>();
		int i = 0;
		int n = 0;
		System.out.println("      ---------------       ");
		System.out.println(demandesEnCours.size() + places_disponibles);
		System.out.println("      ---------------       ");

		output().println(" " + demandesEnCours.size() + " " + places_disponibles);

		while (i < demandesEnCours.size() && n < places_disponibles) {
			
			TripletPnd myPerson = demandesEnCours.get(i);
			boolean veutMonter = myPerson.destination - myPerson.niveau > 0;
			
			if (niveau == myPerson.niveau && ( (veutMonter && cetAscenseur.enMontee)
					|| (!veutMonter && !(cetAscenseur.enMontee)) )) {
				demandesEnCours.remove(i);
				n++;
				listInvites.add(myPerson.id);
			}
			i++;
		}
    	return listInvites;
        
    }

	@Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

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
		// TODO Auto-generated method stub
		
	}

}
