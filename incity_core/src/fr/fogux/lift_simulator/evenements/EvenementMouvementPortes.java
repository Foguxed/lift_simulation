package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.structure.AscId;

public class EvenementMouvementPortes extends AnimatedEvent
{
    protected final AscId ascenseurId;
    protected final int etageId;
    protected final boolean isOuverture;

    public EvenementMouvementPortes(final long time, final DataTagCompound data)
    {
        super(time, data);
        ascenseurId = AscId.fromCompound(data);
        isOuverture = data.getBoolean(TagNames.isOuverture);
        etageId = data.getInt(TagNames.etage);
    }

    public EvenementMouvementPortes(final long debutMouvement, final ConfigSimu c, final AscId ascenseurId, final int etageId, final boolean isOuverture)
    {
        super(c.getDureePortes() + debutMouvement, debutMouvement);
        this.ascenseurId = ascenseurId;
        this.etageId = etageId;
        this.isOuverture = isOuverture;
    }

    @Override
    public void simuRun(final Simulation simu)
    {
        if (isOuverture)
        {
            simu.getImmeubleSimu().getAscenseur(ascenseurId).finOuverturePortes(etageId);
        } else
        {
            simu.getImmeubleSimu().getAscenseur(ascenseurId).finFermeturePortes(etageId);
        }
    }

    @Override
    public void reRun(final Simulation simu)
    {
        if (isOuverture)
        {
            simu.getImmeubleSimu().getAscenseur(ascenseurId).reRunDemandeDeListe();
        }
        else
        {
            simu.getImmeubleSimu().getAscenseur(ascenseurId).reRunFermeturePortes(etageId);
        }

    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long time)
    {
        super.printFieldsIn(compound, time);
        ascenseurId.printIn(compound);
        compound.setInt(TagNames.etage, etageId);
        compound.setBoolean(TagNames.isOuverture, isOuverture);
    }

    @Override
    protected void runAnimation(final AnimationProcess p,final long timeDebut, final long duree)
    {
        p.getImmeubleVisu().getEtage(etageId).getPorte(ascenseurId.monteeId).animation(timeDebut, duree, isOuverture);
    }

    @Override
    protected void sortieAnimation(final AnimationProcess p)
    {

    }

    @Override
    public String toString()
    {
        return "EvenementMouvementPorte ouverture " + isOuverture +" AscId " + ascenseurId + " etage " + etageId + " runTime " + getTime();
    }


}
