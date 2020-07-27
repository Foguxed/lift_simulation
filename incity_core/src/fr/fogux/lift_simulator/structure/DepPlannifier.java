package fr.fogux.lift_simulator.structure;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Predicate;

import fr.fogux.lift_simulator.evenements.AscVirgule;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;
import sun.security.krb5.internal.APOptions;

/**
 * Instantié seulement si necessaire
 * @author Florent
 *
 */
public class DepPlannifier implements Compoundable
{
	protected final List<AscVirgule> virgules = new ArrayList<>();
	protected final ConfigSimu c;
	public AscState EF;
	protected boolean accelerationPositive;
	public AscState EI;
	
	public DepPlannifier(ConfigSimu c, AscState EI)
	{
		this.c = c;
		this.EI = EI;
		this.EF = EI;
	}
	
	public void acceptCompound(DataTagCompound compound)
	{
		virgules.clear();
		List<DataTagCompound> etapes = compound.getCompoundList(TagNames.listeEtapes);
		EI = new AscState(etapes.get(0));
		AscState precedent = EI;
		for(int i = 1; i < etapes.size(); i++)
		{
			AscSoftDep asd = new AscSoftDep(etapes.get(i));
			virgules.add(new AscVirgule(c, asd, precedent));
			precedent = asd;
		}
		EF = precedent;
		if(!virgules.isEmpty())
		{
			accelerationPositive = virgules.get(0).getSoftDep().aPositif();
		}
	}
	
	public void printFieldsIn(DataTagCompound compound)
	{
		List<DataTagCompound> etapes = new ArrayList<DataTagCompound>();
		etapes.add(Compoundable.compound(EI));
		for(AscVirgule v : virgules)
		{
			etapes.add(Compoundable.compound(v.getSoftDep()));
		}
		compound.setCompoundList(TagNames.listeEtapes, etapes);
	}
	/**
	 * @param absTime
	 * @return Ef.x ou EI.x si absTime n'est pas compri dans la range du mouvement
	 */
	public float safeGetX(long absTime)
	{
		
		if(absTime >= EF.t)
		{
			return EF.x;
		}
		else if(absTime > EI.t)
		{
			return getX(absTime);
		}
		else if(absTime == EI.t)
		{
			return EI.x;
		}
		else
		{
			throw new IllegalArgumentException("absTime doit être postéreur à EI.t t = " + absTime + " EI = " + EI);
		}
	}
	
	/**
	 * 
	 * @param absTime doit être compris entre le début et la fin du mouvement
	 * @return
	 */
	private float getX(long absTime)
	{
		return getVirgule(absTime).getX(absTime);
	}
	
	public boolean isSimpleVirgule()
	{
		return virgules.size() <= 1;
	}
	
	private AscVirgule getVirgule(long absTime)
	{
		for(AscVirgule v : virgules)
		{
			if(v.getSoftDep().t >= absTime)
			{
				return v;
			}
		}
		throw new IllegalArgumentException("Incorrect absTime " + absTime + " I am " + this);
	}
	
	public AscState getEtatInitial()
	{
		return EI;
	}
	
	public AscState getFullState(long t)
	{
		if(t >= EF.t)
		{
			return EF.copyStationnaire(t);
		}
		else if(t > EI.t)
		{
			AscDeplacementFunc fun = getVirgule(t).getDepFunc();
			return new AscState(t,fun.getX(t),fun.getV(t));
		}
		else if(t == EI.t)
		{
			return EI;
		}
		else
		{
			throw new IllegalArgumentException("t doit être postéreur à EI.t t = " + t + " EI = " + EI);
		}
	}
	
	public void initiateMovement(long t0, float xf, Ascenseur upper, Ascenseur lower)
	{
		DepPlannifier depUp = null;
		DepPlannifier depLow = null;
		if(upper != null)
		{
			depUp = upper.plannifier;
		}
		if(lower != null)
		{
			depLow = lower.plannifier;
		}
		initiateMovement(t0, xf, depUp, depLow);
	}
	
	public void initiateMovement(long t0, float xf, DepPlannifier upper, DepPlannifier lower)
	{
		EI = getFullState(t0);
		virgules.clear();
		final AscSoftDep directDep = AscDeplacementFunc.getSoftDepStraightToObjective(c, EI.t, EI.v, EI.x, xf);
		final DepPlannifier fixedDepp;
		if(directDep.aPositif())
		{
			fixedDepp = upper;
		}
		else
		{
			fixedDepp = lower;
		}
		EF = initiateMovementPotentialBoundTo(fixedDepp,xf,directDep);
	}
	
	private final AscState validateDirectDep(AscSoftDep directDep)
	{
		virgules.add(new AscVirgule(c, directDep, EI));
		return directDep;
	}
	
	public final EtatAsc getFullMovingEtat(long t)
	{
		AscState state = getFullState(t);
		if(accelerationPositive)
		{
			return new EtatAsc(EtatAscenseur.MONTEE, state.x,  ((int) Math.floor((AscDeplacementFunc.getXArretMinimalMontee(c, state.x, state.v) - ConfigSimu.XEQUALITY_MARGIN))) + 1);
		}
		else
		{
			return new EtatAsc(EtatAscenseur.DESCENTE, state.x,((int) Math.floor((AscDeplacementFunc.getXArretMaximalDescente(c, state.x, state.v)+ ConfigSimu.XEQUALITY_MARGIN))) );
		}
	}
	
	public boolean notMoving(long t)
	{
		return t >= EF.t;
	}
	
	/**
	 * 
	 * @param fixedPlannifier
	 * @param xf should be correct
	 * @return l'heure d'arrivee
	 */
	private AscState initiateMovementPotentialBoundTo(DepPlannifier fixedPlannifier, float xf, AscSoftDep directDep)
	{
		//TODO si deja tangeant
		final boolean aPositif = directDep.aPositif();
		accelerationPositive = aPositif;
		if( fixedPlannifier == null 
			||
			fixedPlannifier.notMoving(EI.t)
			||
			fixedPlannifier.accelerationPositive != aPositif
			||
			(fixedPlannifier.isSimpleVirgule() && c.getAscenseurSpeed()*((float)(fixedPlannifier.getEtatInitial().t+c.getDeltaT() - EI.t)) <= Math.abs(fixedPlannifier.getEtatInitial().x - EI.x))
			)
		{
			return validateDirectDep(directDep);//pas de collision 1;
		}
		else
		{
			
			
			final float xOffset = getXMarginOffset(aPositif);
			DoublePredicate v1CollisionIntoV2 = DoublePredicate.getCollisionPredicate(aPositif);
			//long t = directF.getCriticalT();
			//if(v1CollisionIntoV2.apply(c, directF.getX(t), fixedPlannifier.safeGetX(t)))
			
			if(v1CollisionIntoV2.apply(c, AscDeplacementFunc.getMaxXPossible(fixedPlannifier.EF.t, c, EI, aPositif), fixedPlannifier.EF.x))
			{
				AscDeplacementFunc directF = AscDeplacementFunc.getDeplacementFunc(c, EI.t, EI.x, EI.v, directDep);
				for(int i = 0;i < fixedPlannifier.virgules.size()-1; i++)// on s'occupe du dernier à part
				{
					AscVirgule av = fixedPlannifier.virgules.get(i);
					if(v1CollisionIntoV2.apply(c, directF.getX(av.getSoftDep().t), av.getSoftDep().x))
					{
						AscVirgule lien = getPotentielLien(av,xOffset);
						if(lien != null)
						{
							return remplirVirgulesLien(lien,fixedPlannifier,i,xf,v1CollisionIntoV2,xOffset);
						}
					}
				}
				// si rien n'a marché alors c'est peut être la dernière (en fait le test dans le if ne doit pas être appliqué à la dernière)
				AscVirgule lienPotentiel = getPotentielLien(fixedPlannifier.virgules.get(fixedPlannifier.virgules.size() - 1),xOffset);
				if(lienPotentiel != null)
				{
					return remplirVirgulesLien(lienPotentiel,fixedPlannifier,fixedPlannifier.virgules.size() - 1,xf,v1CollisionIntoV2,xOffset);
				}
			}
			return validateDirectDep(directDep);
		}
	}
	
	private AscVirgule getPotentielLien(AscVirgule av, float xOffset)
	{
		if(EI.t > av.getDepFunc().getEndOfConnectablePolynome())
		{
			return null;
		}
		AscSoftDep lien = getLienSur(av,xOffset);
		if(lien.t + ConfigSimu.TEQUALITY_MARGIN < EI.t || lien.t > av.getDepFunc().getEndOfConnectablePolynome())
		{
			return null;
		}
		else if(lien.is3Phases())
		{
			AscDeplacementFunc lienFun = AscDeplacementFunc.getDeplacementFunc(c, EI.t, EI.x, EI.v, lien);
			if(lienFun.isPossible(lien.t))
			{
				return new AscVirgule(c, lien, EI, lienFun);
			}
		}
		else
		{
			return new AscVirgule(c, lien, EI);
		}
		return null;
	}
	
	private AscState remplirVirgulesLien(AscVirgule lien, DepPlannifier ascBounded, int index, float xf, DoublePredicate collisionV1intoV2, float xOffset)
	{
		virgules.add(lien);
		while(collisionV1intoV2.apply(c,xf,ascBounded.virgules.get(index).getSoftDep().x))
		{
			virgules.add(ascBounded.virgules.get(index).copyTranslated(xOffset));
			index ++;
		}// index forcément < ascBounded.virgules.size() à la fin
		final AscState eiDerniereEtape = virgules.get(virgules.size()-1).getSoftDep();
		AscSoftDep lastDep = AscDeplacementFunc.getSoftDepStraightToObjective(c,eiDerniereEtape , xf);
		virgules.add(new AscVirgule(c,lastDep , eiDerniereEtape));
		return lastDep;
	}
	
	private float getXMarginOffset(boolean aPositif)
	{
		if(aPositif)
		{
			return -c.getMargeInterAscenseur();
		}
		else
		{
			return c.getMargeInterAscenseur();
		}
	}
	
	private AscSoftDep getLienSur(AscVirgule av, float xOffset)
	{
		return AscDeplacementFunc.getDeplacementTangeant(c, EI.t, EI.x, EI.v, av.getDepFunc().getConnectablePolynome(xOffset));
	}
	
	public String toString()
	{
		return "DepPlannifier:{ EI = " + EI + " EF = " + EF + " virgules = " + virgules + " accelerationPositive = " + accelerationPositive +"}";
	}
	
}
