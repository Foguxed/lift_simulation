package fr.fogux.lift_simulator.evenements;

import java.util.HashMap;
import java.util.Map;

import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonAscenseur;
import fr.fogux.lift_simulator.evenements.animation.EvenementBoutonTriangle;
import fr.fogux.lift_simulator.evenements.animation.EvenementChangementPlanifier;
import fr.fogux.lift_simulator.evenements.animation.EvenementConsoleLine;
import fr.fogux.lift_simulator.evenements.animation.EvenementErreur;

public class Evenements
{
    private final static Map<String, Class<? extends Evenement>> map
    = new HashMap<>();
    private final static Map<Class<? extends Evenement>, String> mapB
    = new HashMap<>();

    public static Class<? extends Evenement> getEvenement(final String type)
    {
        return map.get(type);
    }

    public static String getType(final Class<? extends Evenement> evenement)
    {
        return mapB.get(evenement);
    }

    public static void init()
    {
        put("persInput", EvenementPersonnesInput.class);
        put("persEntree", EvenementEntreePersonne.class);
        put("persSortie", EvenementSortiePersonne.class);
        put("mvmtPortes", EvenementMouvementPortes.class);
        put("ascChgmtPlannifier", EvenementChangementPlanifier.class);
        put("boutonTriangle", EvenementBoutonTriangle.class);
        put("ascBouton", EvenementBoutonAscenseur.class);
        put("Erreur", EvenementErreur.class);
        put("ligneConsole", EvenementConsoleLine.class);
    }

    private static void put(final String type, final Class<? extends Evenement> evenement)
    {
        map.put(type, evenement);
        mapB.put(evenement, type);
    }
}
