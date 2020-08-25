package fr.fogux.lift_simulator.mind.independant;

public interface VoisinAsc
{
    /**
     *
     * @return la limite donnée à l'ascenseur supérieur
     */
    int getLimitSup();
    int getLimitInf();
    int getAtteignableSup();
    int getAtteignableInf();
    void updateLimitVoisin(boolean isSup);
    void setAscenseurSuperieur(VoisinAsc asc);
}
