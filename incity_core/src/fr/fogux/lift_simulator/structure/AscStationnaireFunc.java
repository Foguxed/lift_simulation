package fr.fogux.lift_simulator.structure;

public class AscStationnaireFunc implements DeplacementFunc
{
    protected final float x;

    public AscStationnaireFunc(final float x)
    {
        this.x = x;
    }

    @Override
    public float getX(final long absTime)
    {
        return x;
    }

    @Override
    public float getV(final long absTime)
    {
        return 0f;
    }
}
