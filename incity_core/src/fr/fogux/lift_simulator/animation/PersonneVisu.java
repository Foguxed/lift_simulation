package fr.fogux.lift_simulator.animation;

import java.util.ArrayList;
import java.util.List;

import fr.fogux.lift_simulator.structure.Personne;

public class PersonneVisu extends Personne
{
    protected PersonneGroup persGroup;
    protected static List<PersonneVisu> personnesList;

    public PersonneVisu(int destination/* , EtageVisu etageActuel */)
    {
        super(destination, personnesList.size());
        personnesList.add(this);
    }

    public static void initClass()
    {
        personnesList = new ArrayList<PersonneVisu>();
    }

    public static PersonneVisu getPersonne(int id)
    {
        return personnesList.get(id);
    }

    public static void removeLastPersonnes(int nbPersonne)
    {
        System.out.println("removed " + nbPersonne);
        final int sizeDepart = personnesList.size();
        for (int i = 0; i < nbPersonne; i++)
        {
            System.out.println("person id " + String.valueOf(i) + " size " + personnesList.size());
            personnesList.remove(sizeDepart - i - 1).dispose();
        }
    }

    public void animationSortie(long timeDebut, long duree)
    {
        persGroup.animationSortie(timeDebut, duree);
    }

    public PersonneGroup getPersonneGroup()
    {
        return persGroup;
    }

    public void enterPersonneGroup(PersonneGroup group)
    {
        persGroup = group;
        group.add(this);
    }

    public void dispose()
    {
        persGroup.remove(this);
    }
}
