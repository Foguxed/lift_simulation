package fr.fogux.lift_simulator.mind.option;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;

public interface Choix<T,A extends AscIndepIteratif>
{
    T getObj();
    void apply(A asc);
}
