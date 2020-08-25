package fr.fogux.lift_simulator.mind.plannifiers;

import java.util.List;

public class InsertView<T> implements ListView<T>
{
    protected final T element;
    protected final int insertIndex;

    public InsertView(final T element, final int index)
    {
        this.element = element;
        this.insertIndex = index;
    }



    @Override
    public T get(final List<T> liste, final int index)
    {
        if(index > insertIndex)
        {
            return liste.get(index - 1);
        }
        else if(index < insertIndex)
        {
            return liste.get(index);
        }
        else return element;
    }

}
