package fr.fogux.lift_simulator.mind;

import fr.fogux.lift_simulator.mind.cyclic.ProgrammeCyclique;

public abstract class ProgrammeEntryListener
{
    protected static Programme prgmActif;
    
    public static void init()
    {
        prgmActif = new ProgrammeCyclique();
    }
    
    public static void initPrgm()
    {
        prgmActif.init();
    }
    
    public static String getActiveProgramName()
    {
        return "prgm_"+prgmActif.getName();
    }
    
    public static void appeler(int niveau, boolean versLeHaut)
    {
        prgmActif.appelExterieur(niveau, versLeHaut);
    }
    
    public static void finDeTransferDePersonnes(int niveau,int idAscenseur)
    {
        prgmActif.finDeTransferDePersonnes(niveau, idAscenseur);
    }
    
    public static void ascenseurFerme(int idAscenseur)
    {
        prgmActif.ascenseurFerme(idAscenseur);
    }
    
    public static void onAppuiSurNiveau(int niveau,int idAscenseur)
    {
        prgmActif.appelInterieur(niveau, idAscenseur);
    }
    
    public static void capteurDeNiveau(int idAscenseur,int niveau)
    {
        prgmActif.capteurDeNiveau(idAscenseur, niveau);
    }
    
    public static void ascenseurArrete(int idAscenseur)
    {
        prgmActif.ascArrete(idAscenseur);
    }
    
    public static int getActivePrgmNbAscenseurs()
    {
        return prgmActif.getNbAscenseurs();
    }
}
