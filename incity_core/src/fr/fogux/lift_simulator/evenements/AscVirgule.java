package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscDeplacementFunc;
import fr.fogux.lift_simulator.structure.AscSoftDep;
import fr.fogux.lift_simulator.structure.AscState;

public class AscVirgule
{
	private final static Runnable RUN_NOTHING = new Runnable() {
		@Override
		public void run() {
		}
	};
	private final AscSoftDep softDep;
	protected final AscState precedent;
	protected Runnable instantiator = new Runnable() 
	{
		
		@Override
		public void run() {
			instantiateSoftDep();
			instantiator = RUN_NOTHING;
		}
	};
	private final ConfigSimu c;
	
	private AscDeplacementFunc fun;
	
	public AscVirgule(ConfigSimu c,AscSoftDep softDep,AscState precedent)
	{
		this.softDep = softDep;
		this.precedent = precedent;
		this.c = c;
	}
	
	public AscVirgule(ConfigSimu c, AscSoftDep softDep, AscState precedent, AscDeplacementFunc instantiatedFun)
	{
		this(c,softDep,precedent);
		fun = instantiatedFun;
		instantiator = RUN_NOTHING;
	}
	
	public AscSoftDep getSoftDep()
	{
		return softDep;
	}
	
	public AscVirgule copyTranslated(float xOffset)
	{
		final AscSoftDep newSoftDep = softDep.copyTranslatedAndBounded(xOffset);
		final AscState newPrecedent = precedent.copyTranslated(xOffset);
		return new AscVirgule(c,newSoftDep,newPrecedent);
		//optimisable, on utilise pas la fun ici.
	}
	/**
	 * 
	 * @param etatPrecedent
	 * @param deplacementActuel
	 * @param approcheur
	 * @return null si aucune connection n'est possible, la virgule se connectant à cette virgule sinon
	 */
	private AscVirgule getVirguleConnection(ConfigSimu c,AscState approcheur)
	{
		if(c.getAscenseurSpeed()*((float)(precedent.t+c.getDeltaT() - approcheur.t)) > Math.abs(precedent.x - approcheur.x))
		{
			instantiator.run();
			if(approcheur.t < fun.getEndOfConnectablePolynome())
			{
				
			}
		}
		return null;
	}
	
	public AscDeplacementFunc getDepFunc()
	{
		instantiator.run();
		return fun;
	}
	
	public void instantiateSoftDep()
	{
		fun = AscDeplacementFunc.getDeplacementFunc(c, precedent.t, precedent.x, precedent.v, softDep);
	}
	
	public String toString()
	{
		return "virgule: {SoftDep = " + softDep + " deplacementFunc " + fun + "}";
	}
	
	public float getX(long absTime)
	{
		instantiator.run();
		return fun.getX(absTime);
	}
}
