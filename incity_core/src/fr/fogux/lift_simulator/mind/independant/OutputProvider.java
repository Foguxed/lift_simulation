package fr.fogux.lift_simulator.mind.independant;

import fr.fogux.lift_simulator.physic.InterfacePhysique;

public class OutputProvider
{
    public InterfacePhysique interfacePhys;
    public OutputProvider(final InterfacePhysique phys)
    {
        interfacePhys = phys;
    }

    public InterfacePhysique out()
    {
        return interfacePhys;
    }
}
