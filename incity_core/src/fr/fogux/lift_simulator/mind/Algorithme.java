package fr.fogux.lift_simulator.mind;

import java.util.Collection;

import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.physic.InterfacePhysique;
import fr.fogux.lift_simulator.structure.AscId;

public abstract class Algorithme
{

    protected InterfacePhysique output;
    protected ConfigSimu config;

    public Algorithme(final InterfacePhysique output, final ConfigSimu config)
    {
        this.output = output;
        this.config = config;
    }

    public InterfacePhysique output()
    {
        return output;
    }

    public ConfigSimu config()
    {
        return config;
    }

    /**
     * appelle à t = 0
     */
    public abstract void init();

    /**
     * appelé lorsque quelqu'un arrive dans l'immeuble
     * @param niveau
     * @param destination
     */
    public abstract void appelExterieur(int idPersonne, int niveau, int destination);

    public abstract Collection<Integer> listeInvites(AscId idASc, int places_disponibles);

    public abstract void arretSansOuverture(AscId idAscenseur);

    /**
     * appelé lorsque les portes de l'ascenseur se sont fermees
     *
     * @param niveau      auquel l'actio a lieue
     * @param idAscenseur de l'ascenseur ouvert
     */
    public abstract void finDeTransfertDePersonnes(AscId idAscenseur);

    @Deprecated
    /**
     * appelé lorsqu'un bouton appartenant au panneau à l'interieur de l'ascenseur
     * est utilisé
     *
     * @param niveau      correspondant au bouton
     * @param idAscenseur de l'ascenseur concerné
     */
    public abstract void appelInterieur(int niveau, AscId idAscenseur);
}
