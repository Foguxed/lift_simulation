package fr.fogux.lift_simulator.structure;

import fr.fogux.lift_simulator.physic.ConfigSimu;

public abstract class DoublePredicate 
{
	private static final DoublePredicate v1CollisionIntov2APositif = new DoublePredicate() 
	{
		@Override
		public boolean apply(ConfigSimu c, float v1, float v2) {
			return v1 + c.getMargeInterAscenseur() > v2;
		}
	};
	private static final DoublePredicate v1CollisionIntov2ANegatif = new DoublePredicate() 
	{
		@Override
		public boolean apply(ConfigSimu c, float v1, float v2) {
			return v1 < v2 + c.getMargeInterAscenseur();
		}
	};
	
	public static final DoublePredicate getCollisionPredicate(boolean aPositif)
	{
		if(aPositif)
		{
			return v1CollisionIntov2APositif;
		}
		else
		{
			return v1CollisionIntov2ANegatif;
		}
	}
	public abstract boolean apply(ConfigSimu c, float v1, float v2);
}
