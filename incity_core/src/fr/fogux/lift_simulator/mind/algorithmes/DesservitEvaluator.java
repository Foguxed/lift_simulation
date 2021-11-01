package fr.fogux.lift_simulator.mind.algorithmes;

import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.SimuEvaluator;
import fr.fogux.lift_simulator.population.PersonneSimu;

public class DesservitEvaluator implements SimuEvaluator<Float>
{

	static final int periode = 4*1000*60;
	@Override
	public Float evaluate(Simulation s, int time) 
	{
		return evaluerPersLivrees(s,time);
	}

	@Override
	public Float evaluateTerminated(Simulation s) 
	{
		return evaluerPersLivrees(s,(int)s.getTime());
	}

	@Override
	public Float evaluateAbsolute(Simulation s, int time) {
		return evaluerPersLivrees(s,time);
	}
	
	protected float evaluerPersLivrees(Simulation s, int timeMax)
	{
		int tMin = Math.max(timeMax - periode,1);
		int nbLivrees = 0;
		int nbDansAsc = 0;
		for(PersonneSimu p : s.getPersonneList())
		{
			if(p != null)
			{
				if(p.heureSortieAscenseur >= tMin && p.heureSortieAscenseur < timeMax)
				{
					nbLivrees ++;
				}
				else if(p.heureEntreeAscenseur >= tMin && p.heureEntreeAscenseur < timeMax)
				{
					nbDansAsc ++;
				}
			}
		}
		return -(2*nbLivrees+nbDansAsc);
	}

}
