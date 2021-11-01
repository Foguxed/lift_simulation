package fr.fogux.lift_simulator.mind.option;

import java.util.function.Consumer;

import fr.fogux.lift_simulator.mind.ascenseurs.AscIndepIteratif;

public class BChoix<T extends Object,A extends AscIndepIteratif> implements Choix<T,A>
{
    protected T obj;
    protected Consumer<A> c;

    protected static final Consumer nothing = new Consumer() {

        @Override
        public void accept(final Object t)
        {

        }
    };

    public BChoix(final T obj)
    {
        this(obj,nothing);
    }

    public BChoix(final T obj,final Consumer<A> c)
    {
        this.obj = obj;
        this.c = c;
    }

    @Override
    public T getObj()
    {
        return obj;
    }

    @Override
    public void apply(final A asc)
    {
        c.accept(asc);
    }

    @Override
    public String toString()
    {
        return " obj " + obj + " consumer " + c;
    }
}