package fr.fogux.lift_simulator.mind.planifiers;

import java.util.List;

public interface ListView<T>
{
    T get(List<T> liste, int index);
}
