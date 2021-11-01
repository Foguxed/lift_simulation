package fr.fogux.lift_simulator.mind.algorithmes.treeexplo;

import fr.fogux.lift_simulator.mind.option.NoeudChoix;
import fr.fogux.lift_simulator.mind.option.OptionSimu;
import fr.fogux.lift_simulator.utils.Arbre;

public interface OptionSimuStruct
{
    void add(OptionSimu opt);
    void removeSimu(int nb);
    void keepOnlyMatchingFirstChoix(Arbre<NoeudChoix<?, ?>> opt);
    OptionSimu getBestSimu(int time);
    boolean aMeilleurQueB(OptionSimu a, OptionSimu b, int time);
    Arbre<NoeudChoix<?, ?>> getBestFirstChoix();
    long minSimuTime();
}
