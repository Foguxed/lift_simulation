package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.EventRunPolicy;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementPersonnesInput extends PrintableEvenement implements Comparable<EvenementPersonnesInput>
{
    public final int nbPersonnes;
    protected final int destination;
    protected final int etage;

    public EvenementPersonnesInput(final long time, final int nbPersonnes, final int destination, final int etage)
    {
        super(time);
        this.nbPersonnes = nbPersonnes;
        this.destination = destination;
        this.etage = etage;
    }

    public EvenementPersonnesInput(final EvenementPersonnesInput aCloner, final int nouveauNbPersonnes, final long nouveauTime)
    {
        super(nouveauTime);
        nbPersonnes = nouveauNbPersonnes;
        destination = aCloner.destination;
        etage = aCloner.etage;
    }

    public EvenementPersonnesInput(final long time, final DataTagCompound data)
    {
        super(time);
        nbPersonnes = data.getInt(TagNames.nbPersonnes);
        destination = data.getInt(TagNames.destination);
        etage = data.getInt(TagNames.etage);
    }

    @Override
    public void simuRun(final Simulation simu)
    {
        simu.getImmeubleSimu().getEtage(etage).arriveeDe(nbPersonnes, destination);
        simu.getGestio().forecastNextPersInput();
    }

    @Override
    protected void printFieldsIn(final DataTagCompound compound, final long time)
    {
        compound.setInt(TagNames.nbPersonnes, nbPersonnes);
        compound.setInt(TagNames.destination, destination);
        compound.setInt(TagNames.etage, etage);
    }

    public String getPartitionEventString()
    {
        return getEventString(time);
    }

    @Override
    public void visuRun(final AnimationProcess animation)
    {
        if (animation.gestioTaches().marcheArriereEnCours())
        {
            PersonneVisu.removeLastPersonnes(nbPersonnes);
        } else
        {
            animation.getImmeubleVisu().getEtage(etage).arriveeDe(nbPersonnes, destination);
        }
    }

    @Override
    public int compareTo(final EvenementPersonnesInput o)
    {
        return (int)(time - o.time);
    }

    @Override
    public void reRun(final Simulation simulation)
    {
        simulation.reRunLastInputPersonne();
    }

    @Override
    public boolean shadowable(final long registeredTime,EventRunPolicy newPolicy)
    {
        return false;
    }

}
