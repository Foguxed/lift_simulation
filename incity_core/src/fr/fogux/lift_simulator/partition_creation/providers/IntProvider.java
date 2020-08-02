package fr.fogux.lift_simulator.partition_creation.providers;

import java.util.Random;

import fr.fogux.lift_simulator.fichiers.Compoundable;

public interface IntProvider extends Compoundable
{
	public int getRandomInt(Random r);
}
