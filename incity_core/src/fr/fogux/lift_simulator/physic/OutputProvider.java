package fr.fogux.lift_simulator.physic;

/**
 * Cette classe permet de modifier l'InterfacePhysique auquel a accès un algorithme
 * affin d'effectuer des simulations complémentaires nécessaires pour départager différentes
 * options (utilisé dans BestInsert et 2-opt)
 */
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
