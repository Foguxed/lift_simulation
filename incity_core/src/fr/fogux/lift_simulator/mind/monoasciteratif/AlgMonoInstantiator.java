package fr.fogux.lift_simulator.mind.monoasciteratif;

import fr.fogux.lift_simulator.mind.AlgoInstantiator;
import fr.fogux.lift_simulator.mind.Algorithme;
import fr.fogux.lift_simulator.mind.independant.OutputProvider;
import fr.fogux.lift_simulator.mind.trajets.AlgoPersonne;
import fr.fogux.lift_simulator.mind.trajets.Escale;
import fr.fogux.lift_simulator.mind.trajets.EtatMonoAsc;
import fr.fogux.lift_simulator.physic.ConfigSimu;
import fr.fogux.lift_simulator.utils.OcamlList;

public class AlgMonoInstantiator implements AlgoInstantiator
{
    protected final EtatMonoAsc etat;
    protected final MonoMemoiser memoiser;
    protected final AlgoPersonne p;
    protected final boolean estRecup;
    protected final OcamlList<Escale> trajet;


    public AlgMonoInstantiator(final EtatMonoAsc etat, final MonoMemoiser memoiser, final AlgoPersonne p, final boolean estRecuperation, final OcamlList<Escale> trajet)
    {
        this.etat = etat;
        this.memoiser = memoiser;
        this.p = p;
        this.trajet = trajet;
        estRecup = estRecuperation;
    }

    @Override
    public Algorithme getPrgm(final OutputProvider output, final ConfigSimu c)
    {
        return new AlgMonoAscIteratif(output, c, memoiser, etat, p,estRecup,trajet);
    }

    @Override
    public String getName()
    {
        return "algMonoIteratifInstantiator";
    }

}
