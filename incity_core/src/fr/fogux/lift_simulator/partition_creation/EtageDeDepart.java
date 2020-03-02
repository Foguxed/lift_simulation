package fr.fogux.lift_simulator.partition_creation;

public class EtageDeDepart
{
    protected final int etage;
    protected final double proba;
    protected final DestinationRandomiser randomiser;
    protected GroupProfile grpProfile;

    public EtageDeDepart(int etage, double proba, DestinationProfile destiProfile, GroupProfile grpProfile)
    {
        this.etage = etage;
        this.proba = proba;
        this.randomiser = new DestinationRandomiser(destiProfile, etage);
        this.grpProfile = grpProfile;
    }

    public PersonneInput getRandomPersonInupt(long time)
    {
        return new PersonneInput(
            time, grpProfile.getRandomGrpsSize(), etage, randomiser.getRandomDestination().getEtage());
    }

    public double getProba()
    {
        return proba;
    }
}
