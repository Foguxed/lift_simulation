package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.List;

public interface ListView<T>
{
    T get(List<T> liste, int index);
}
