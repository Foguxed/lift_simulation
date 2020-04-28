package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.AnimationProcess;
import fr.fogux.lift_simulator.Simulation;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;

public class EvenementPersonnesInput extends PrintableEvenement
{
    protected int nbPersonnes;
    protected int destination;
    protected int etage;

    public EvenementPersonnesInput(final long time, final int nbPersonnes, final int destination, final int etage)
    {
        super(time);
        this.nbPersonnes = nbPersonnes;
        this.destination = destination;
        this.etage = etage;
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

}
