package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.algorithmes.treeexplo.treealg.AscCycleOption;
import fr.fogux.lift_simulator.mind.planifiers.ContenuAsc;

public class ConsumerContenu implements Consumer<AscCycleOption>
{
    public final ContenuAsc copied;
    //private final int debugVal;
    //private final String copiedInitialString;

    public ConsumerContenu(final ContenuAsc toCopy)
    {
        copied = new ContenuAsc(toCopy);
        /*
        debugVal = Utils.newDebugCompteurId();
        copiedInitialString = copied.toString();
        System.out.println("cree cons contenu " + copied + " id " + debugVal);*/
    }

    @Override
    public void accept(final AscCycleOption t)
    {
        //t.phys().println("contenu consumerupdated from " + t.contenu + " to " + copied + " id " + debugVal + " initialStr " + copiedInitialString);
        t.contenu = new ContenuAsc(copied);
    }

}
