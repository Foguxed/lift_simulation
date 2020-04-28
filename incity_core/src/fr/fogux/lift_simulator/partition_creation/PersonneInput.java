package fr.fogux.lift_simulator.partition_creation;

import fr.fogux.lift_simulator.evenements.EvenementPersonnesInput;
import fr.fogux.lift_simulator.evenements.Evenements;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public class PersonneInput implements Comparable<PersonneInput>
{
    protected final long heureInput;
    protected final int nbPersonnes;
    protected final int etageInput;
    protected final int destination;
    DataTagCompound data;

    public PersonneInput(final long heureInput, final int nbPersonnes, final int etageInput, final int destination)
    {
        this.heureInput = heureInput;
        this.nbPersonnes = nbPersonnes;
        this.etageInput = etageInput;
        this.destination = destination;
        data = new DataTagCompound();
        data.setInt(TagNames.nbPersonnes, nbPersonnes);
        data.setInt(TagNames.etage, etageInput);
        data.setInt(TagNames.destination, destination);
        data.setString(TagNames.type, Evenements.getType(EvenementPersonnesInput.class));
    }

    public String getStringVal()
    {
        return "[" + Utils.getTimeString(heureInput) + "]" + data.getValueAsString();
    }

    public long heureInput()
    {
        return heureInput;
    }

    @Override
    public int compareTo(final PersonneInput o)
    {
        return (int) (heureInput - o.heureInput());
    }

    public int getNbPersonnes()
    {
        return nbPersonnes;
    }
}
