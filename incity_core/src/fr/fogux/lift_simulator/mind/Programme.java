package fr.fogux.lift_simulator.mind;

public interface Programme
{
    //appele au tout debut
    public void init();
    
    /**
     * appelé lorsqu'un bouton dans les étages est utilisé
     * 
     * @param niveau niveau auquel un ascenseur a été appelé
     * @param versLeHaut true si c'est le bouton fleché vers le haut qui a été utilisé
     */
    public void appelExterieur(int niveau, boolean versLeHaut);
    
    /**
     * appelé lorsque les personnes sont toutes entrees dans un ascenseur ouvert
     * 
     * @param niveau auquel l'actio a lieue
     * @param idAscenseur de l'ascenseur ouvert
     */
    public void finDeTransfertDePersonnes(int niveau,int idAscenseur);
    
    /**
     * appelé lorsque les portes d'un ascenseur se sont fermées
     * 
     * @param idAscenseur id de l'ascenseur concerné
     */
    
    public void ascenseurFerme(int idAscenseur);
    
    /**
     * appelé lorsqu'un bouton appartenant au panneau à l'interieur de l'ascenseur est utilisé
     * 
     * @param niveau correspondant au bouton
     * @param idAscenseur de l'ascenseur concerné
     */
    public void appelInterieur(int niveau,int idAscenseur);
    
    
    /**
     * appelé lorsqu'un ascenseur passe au palier d'un niveau
     * 
     * @param idAscenseur de l'ascenseur concerné
     * @param niveau auquel se trouve l'ascenseur
     */
    public void capteurDeNiveau(int idAscenseur,int niveau);
    
    /**
     * permet d'identifier le programme dans les fichiers
     * @return le nom du programme
     */
    
    public void ascArrete(int idAscenseur);
    public String getName();
    
    public int getNbAscenseurs();
}
