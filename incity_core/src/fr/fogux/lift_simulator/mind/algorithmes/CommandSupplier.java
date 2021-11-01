package fr.fogux.lift_simulator.mind.algorithmes;

import fr.fogux.lift_simulator.mind.ascenseurs.AlgoIndependentAsc;
import fr.fogux.lift_simulator.mind.option.Choix;

public interface CommandSupplier {
	public Choix<?,?> pollNextCommande(final AlgoIndependentAsc a);
}
