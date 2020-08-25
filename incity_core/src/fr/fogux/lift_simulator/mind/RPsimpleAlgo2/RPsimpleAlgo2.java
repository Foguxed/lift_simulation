package fr.fogux.lift_simulator.mind.RPsimpleAlgo2;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.RPsimpleAlgo.TripletPnd;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class RPsimpleAlgo2 extends Algorithme
{
    public int[] waitingUp = new int[config.getNiveauMax() - config.getNiveauMin() + 1];
    public int[] waitingDown = new int[config.getNiveauMax() - config.getNiveauMin() + 1];
    public int ground = config.getNiveauMin();
    public List<TripletPnd> demandesEnCours = new ArrayList<>();


    public monAscenseur2[] [] ascenseursArray = new monAscenseur2[4] [2];

    public RPsimpleAlgo2(final OutputProvider output, final ConfigSimu config)
    {
        super(output, config);
    }

    @Override
    public long init()
    {
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n
                final AscId id = new AscId(i, j);
                final boolean[] stops = new boolean[config.getNiveauMax() - config.getNiveauMin() + 1];
                out().changerDestination(id, -1 + 2*i + 3*j, true);
                ascenseursArray[i] [j] = new monAscenseur2(id, stops, true);
            }
        }
        out().systemPrintLn(" INIT --------------------------------------    ");
        return -1;	// Dur�e entre deux Ping()
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
        ping();
    }

    @Override
    public List<Integer> listeInvites(final AscId idAsc, final int places_disponibles, final int niveau)
    {
        final monAscenseur2 cetAscenseur = ascenseursArray[idAsc.monteeId] [idAsc.stackId];
        final List<Integer> listInvites = new ArrayList<>();
        cetAscenseur.stops[niveau - ground] = false;

        int i = 0;
        int n = 0;

        while (i < demandesEnCours.size() && n < places_disponibles) {

            final TripletPnd myPerson = demandesEnCours.get(i);
            final boolean veutMonter = myPerson.destination - myPerson.niveau > 0;

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
        ping();
        return listInvites;

    }

    @Override
    public void arretSansOuverture(final AscId idAscenseur)
    {

    }

    @Override
    public void finDeTransfertDePersonnes(final AscId idAscenseur, final int t)
    {
        ping();
    }

    @Override
    public void appelInterieur(final int niveau, final AscId idAscenseur)
    {

    }

    @Override
    public void ping() {

        out().println(" Ping ------------------");
        for (int j = 0; j < 2; j++) {	// 2 � changer en m

            for (int i = 0; i < 4; i++) { // CHANGER LE 4 EN n
                final monAscenseur2 cetAscenseur = ascenseursArray[i] [j];
                final AscId id = new AscId(i, j);

                final int niveau = cetAscenseur.niveau(out());

                boolean someoneWaitsHere;
                if (cetAscenseur.enMontee) {
                    someoneWaitsHere = waitingUp[niveau - ground] > 0;
                } else {
                    someoneWaitsHere = waitingDown[niveau - ground] > 0;
                }

                final int maxLevel = cetAscenseur.maxLevel(ascenseursArray, out(), config);
                final int minLevel = cetAscenseur.minLevel(ascenseursArray, out(), config);

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
                    out().println(niveau);
                    intArrayPrinter(waitingUp);
                    intArrayPrinter(waitingDown);
                    boolArrayPrinter(cetAscenseur.stops);
                    if (someoneWaitsHere) {
                        out().println("wh ");
                    }
                    if (someoneWaitsFurther) {
                        out().println("wf ");
                    }
                    if (cetAscenseur.arretDemande(niveau)) {
                        out().println("sh ");
                    }
                    if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, out(), config)) {
                        out().println("sf ");
                    }
                }


                if (cetAscenseur.arretDemande(niveau) || (someoneWaitsHere && out().getNbPersonnes(id) < config.nbPersMaxAscenseur())) {
                    out().changerDestination(id, niveau, true);	// STOP

                } else if (cetAscenseur.arretDemandePlusLoin(niveau, ascenseursArray, out(), config) || someoneWaitsFurther) {
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
                        out().println(id + "FSB");	// BLOQUE
                    } else {
                        out().changerDestination(id, nextStop, true);	// CONTINUE
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
                        out().println(id + "N OR FSB");	// BLOQUE
                    } else {
                        out().changerDestination(id, nextStop, true);	// CONTINUE
                    }				}
            }
        }
    }

    private void intArrayPrinter(final int[] bools) {
        int p = 0;
        int a = 1;
        for (int i = 0; i < bools.length - 3; i++) {
            p += bools[i]*a;
            a *= 10;
        }
        p += a;
        out().println(p);
    }

    private void boolArrayPrinter(final boolean[] stops)
    {
        int p = 0;
        int a = 1;
        for (int i = 0; i < stops.length - 3; i++) {
            p += (stops[i] ? 7 : 0)*a;
            a *= 10;
        }
        p += a;
        out().println(p);
    }
}
