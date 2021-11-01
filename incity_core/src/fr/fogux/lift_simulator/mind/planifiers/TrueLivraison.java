package fr.fogux.lift_simulator.mind.planifiers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.fogux.lift_simulator.mind.trajets.AlgoPersGroup;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;

public class TrueLivraison extends Livraison
{

    public final Set<AlgoPersonne> contenu;


    /**
     *  ne doivent pas être modifiables
     * @param destination
     * @param contenu
     */
    public TrueLivraison(final int destination, final Set<AlgoPersonne> contenu)
    {
        super(destination, contenu.size());
        this.contenu = contenu;
    }

    public TrueLivraison(final AlgoPersGroup fromGroup, final int nbPers)
    {
        this(fromGroup.getDestination(),fromGroup(fromGroup,nbPers));
    }

    public TrueLivraison merge(final TrueLivraison livraison)
    {
        final Set<AlgoPersonne> newContenu = new HashSet<>(contenu);
        newContenu.addAll(livraison.contenu);
        return new TrueLivraison(destination, newContenu);
    }

    public void dumpDifference(final TrueLivraison refIncluse, final Collection<AlgoPersonne> c)
    {
        for(final AlgoPersonne p : contenu)
        {
            if(!refIncluse.contenu.contains(p))
            {
                c.add(p);
            }
        }
    }

    private static final Set<AlgoPersonne> fromGroup(final AlgoPersGroup grp, final int nbPers)
    {
        final Set<AlgoPersonne> c = new HashSet<>(nbPers);
        grp.dump(c, nbPers);
        return c;
    }

    public void dump(final Collection<AlgoPersonne> c)
    {
        c.addAll(contenu);
    }

    @Override
    public String toString()
    {
        return super.toString() + " detail : *" + contenu + "* ";
    }
}
