package fr.fogux.lift_simulator.structure;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.evenements.AscVirgule;
import fr.fogux.lift_simulator.fichiers.Compoundable;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.EtatAscenseur;

/**
 * Instantié seulement si necessaire
 * @author Florent
 *
 */
public class DepPlanificateur implements Compoundable
{
    protected final List<AscVirgule> virgules;
    protected final ConfigSimu c;
    public AscState EF;
    protected boolean accelerationPositive;
    public AscState EI;

    public DepPlanificateur(final ConfigSimu c, final AscState EI)
    {
        this.c = c;
        this.EI = EI;
        EF = EI;
        virgules = new ArrayList<>();
    }

    public DepPlanificateur(final DepPlanificateur shadowed)
    {
        c = shadowed.c;
        EI = shadowed.EI;
        EF = shadowed.EF;
        accelerationPositive = shadowed.accelerationPositive;
        virgules = new ArrayList<>(shadowed.virgules);//on ne peut pas modifier une virgule
    }

    public void acceptCompound(final DataTagCompound compound)
    {
        virgules.clear();
        final List<DataTagCompound> etapes = compound.getCompoundList(TagNames.listeEtapes);
        EI = new AscState(etapes.get(0));
        AscState precedent = EI;
        for(int i = 1; i < etapes.size(); i++)
        {
            final AscSoftDep asd = new AscSoftDep(etapes.get(i));
            virgules.add(new AscVirgule(c, asd, precedent));
            precedent = asd;
        }
        EF = precedent;
        if(!virgules.isEmpty())
        {
            accelerationPositive = virgules.get(0).getSoftDep().aPositif();
        }
    }

    @Override
    public void printFieldsIn(final DataTagCompound compound)
    {
        final List<DataTagCompound> etapes = new ArrayList<>();
        etapes.add(Compoundable.compound(EI));
        for(final AscVirgule v : virgules)
        {
            etapes.add(Compoundable.compound(v.getSoftDep()));
        }
        compound.setCompoundList(TagNames.listeEtapes, etapes);
    }
    /**
     * @param absTime
     * @return Ef.x ou EI.x si absTime n'est pas compri dans la range du mouvement
     */
    public float safeGetX(final long absTime)
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
    private float getX(final long absTime)
    {
        return getVirgule(absTime).getX(absTime);
    }

    public boolean isSimpleVirgule()
    {
        return virgules.size() <= 1;
    }

    private AscVirgule getVirgule(final long absTime)
    {
        for(final AscVirgule v : virgules)
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

    public AscState getFullState(final long t)
    {
        if(t >= EF.t)
        {
            return EF.copyStationnaire(t);
        }
        else if(t > EI.t)
        {
            final AscDeplacementFunc fun = getVirgule(t).getDepFunc();
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

    public void initiateMovement(final long t0, final float xf, final Ascenseur upper, final Ascenseur lower)
    {
        DepPlanificateur depUp = null;
        DepPlanificateur depLow = null;
        if(upper != null)
        {
            depUp = upper.planificateur;
        }
        if(lower != null)
        {
            depLow = lower.planificateur;
        }
        initiateMovement(t0, xf, depUp, depLow);
    }

    public void initiateMovement(final long t0, final float xf, final DepPlanificateur upper, final DepPlanificateur lower)
    {
        EI = getFullState(t0);
        virgules.clear();
        final AscSoftDep directDep = AscDeplacementFunc.getSoftDepStraightToObjective(c, EI.t, EI.v, EI.x, xf);
        final DepPlanificateur fixedDepp;
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

    private final AscState validateDirectDep(final AscSoftDep directDep)
    {
        virgules.add(new AscVirgule(c, directDep, EI));
        return directDep;
    }

    public final EtatAsc getFullMovingEtat(final long t)
    {
        final AscState state = getFullState(t);
        //System.out.println(" vv " + AscDeplacementFunc.getXArretMinimalMontee(c, state.x, state.v) + " eqmargin " + ConfigSimu.XEQUALITY_MARGIN);
        if(accelerationPositive)
        {
            return new EtatAsc(EtatAscenseur.MONTEE, state.x,  ((int) Math.floor((AscDeplacementFunc.getXArretMinimalMontee(c, state.x, state.v) - ConfigSimu.XEQUALITY_MARGIN))) + 1);
        }
        else
        {
            return new EtatAsc(EtatAscenseur.DESCENTE, state.x,((int) Math.floor((AscDeplacementFunc.getXArretMaximalDescente(c, state.x, state.v)+ ConfigSimu.XEQUALITY_MARGIN))) );
        }
    }

    public boolean notMoving(final long t)
    {
        return t >= EF.t; // anti collision marche pour t >= EF.t
    }

    /**
     *
     * @param fixedPlanificateur
     * @param xf should be correct
     * @return l'heure d'arrivee
     */
    private AscState initiateMovementPotentialBoundTo(final DepPlanificateur fixedPlanificateur, final float xf, final AscSoftDep directDep)
    {
        final boolean aPositif = directDep.aPositif();
        accelerationPositive = aPositif;
        if( fixedPlanificateur == null
            ||
            fixedPlanificateur.notMoving(EI.t)
            ||
            fixedPlanificateur.accelerationPositive != aPositif
            ||
            (fixedPlanificateur.isSimpleVirgule() && c.getAscenseurSpeed()*(fixedPlanificateur.getEtatInitial().t+c.getDeltaT() - EI.t) <= Math.abs(fixedPlanificateur.getEtatInitial().x - EI.x))
            )
        {
            return validateDirectDep(directDep);//pas de collision 1;
        }
        else
        {


            final float xOffset = getXMarginOffset(aPositif);
            final DoublePredicate v1CollisionIntoV2 = DoublePredicate.getCollisionPredicate(aPositif);
            //long t = directF.getCriticalT();
            //if(v1CollisionIntoV2.apply(c, directF.getX(t), fixedPlannifier.safeGetX(t)))

            if(v1CollisionIntoV2.apply(c, AscDeplacementFunc.getMaxXPossible(fixedPlanificateur.EF.t, c, EI, aPositif), fixedPlanificateur.EF.x))
            {
                final AscDeplacementFunc directF = AscDeplacementFunc.getDeplacementFunc(c, EI.t, EI.x, EI.v, directDep);
                for(int i = 0;i < fixedPlanificateur.virgules.size()-1; i++)// on s'occupe du dernier à part
                {
                    final AscVirgule av = fixedPlanificateur.virgules.get(i);
                    if(v1CollisionIntoV2.apply(c, directF.getX(av.getSoftDep().t), av.getSoftDep().x))
                    {
                        final AscVirgule lien = getPotentielLien(av,xOffset);
                        if(lien != null)
                        {
                            return remplirVirgulesLien(lien,fixedPlanificateur,i,xf,v1CollisionIntoV2,xOffset);
                        }
                    }
                }
                // si rien n'a marché alors c'est peut être la dernière (en fait le test dans le if ne doit pas être appliqué à la dernière)
                final AscVirgule lienPotentiel = getPotentielLien(fixedPlanificateur.virgules.get(fixedPlanificateur.virgules.size() - 1),xOffset);
                if(lienPotentiel != null)
                {
                    return remplirVirgulesLien(lienPotentiel,fixedPlanificateur,fixedPlanificateur.virgules.size() - 1,xf,v1CollisionIntoV2,xOffset);
                }
            }
            return validateDirectDep(directDep);
        }
    }

    private AscVirgule getPotentielLien(final AscVirgule av, final float xOffset)
    {
        if(EI.t > av.getDepFunc().getEndOfConnectablePolynome())
        {
            return null;
        }
        final AscSoftDep lien = getLienSur(av,xOffset);
        if(lien.t + ConfigSimu.TEQUALITY_MARGIN < EI.t || lien.t > av.getDepFunc().getEndOfConnectablePolynome())
        {
            return null;
        }
        else if(lien.is3Phases())
        {
            final AscDeplacementFunc lienFun = AscDeplacementFunc.getDeplacementFunc(c, EI.t, EI.x, EI.v, lien);
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

    private AscState remplirVirgulesLien(final AscVirgule lien, final DepPlanificateur ascBounded, int index, final float xf, final DoublePredicate collisionV1intoV2, final float xOffset)
    {
        virgules.add(lien);
        while(collisionV1intoV2.apply(c,xf,ascBounded.virgules.get(index).getSoftDep().x))
        {
            virgules.add(ascBounded.virgules.get(index).copyTranslated(xOffset));
            index ++;
        }// index forcément < ascBounded.virgules.size() à la fin
        final AscState eiDerniereEtape = virgules.get(virgules.size()-1).getSoftDep();
        final AscSoftDep lastDep = AscDeplacementFunc.getSoftDepStraightToObjective(c,eiDerniereEtape , xf);
        virgules.add(new AscVirgule(c,lastDep , eiDerniereEtape));
        return lastDep;
    }

    private float getXMarginOffset(final boolean aPositif)
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

    private AscSoftDep getLienSur(final AscVirgule av, final float xOffset)
    {
        return AscDeplacementFunc.getDeplacementTangeant(c, EI.t, EI.x, EI.v, av.getDepFunc().getConnectablePolynome(xOffset));
    }

    @Override
    public String toString()
    {
        return "DepPlannifier:{ EI = " + EI + " EF = " + EF + " virgules = " + virgules + " accelerationPositive = " + accelerationPositive +"}";
    }

}
