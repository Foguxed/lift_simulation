package fr.fogux.lift_simulator.utils;

public class Maillon<obj extends Object>
{
    protected final obj value;
    protected Maillon<obj> suivant;
    protected Maillon<obj> precedent;

    protected Maillon(final Maillon<obj> precedent, final obj object, final Maillon<obj> suivant)
    {
        this.precedent = precedent;
        this.value = object;
        this.suivant = suivant;
    }

    protected Maillon(final Maillon<obj> precedent, final obj object)
    {
        this(precedent, object, null);
    }

    protected Maillon(final obj object, final Maillon<obj> suivant)
    {
        this(null, object, suivant);
    }

    protected Maillon(final obj object)
    {
        this(null, object, null);
    }

    public obj getValue()
    {
        return value;
    }

    public Maillon<obj> getSuivant()
    {
        return suivant;
    }

    public Maillon<obj> getPrecedent()
    {
        return precedent;
    }
}
