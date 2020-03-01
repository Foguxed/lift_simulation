package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.utils.Utils;

public class EvenementAscenseurCapteurNiveau extends PrintableEvenement
{
    protected final int ascenseurId;
    protected final int etage;
    
    
    public EvenementAscenseurCapteurNiveau(long time, DataTagCompound data)
    {
        super(time,data);
        this.ascenseurId = data.getInt(TagNames.ascenseurId);
        this.etage = data.getInt(TagNames.etage);
    }
    
    public EvenementAscenseurCapteurNiveau(long time,int ascenseurId,int etage)
    {
        super(time,true);
        this.ascenseurId = ascenseurId;
        this.etage = etage;
    }
    
    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        Utils.msg(this, " printFIEDS time " + GestionnaireDeTaches.getInnerTime() + " mon time " + time + " etage " + etage);
        compound.setInt(TagNames.ascenseurId,ascenseurId);
        compound.setInt(TagNames.etage, etage);
    }

    @Override
    public void simuRun()
    {
        super.simuRun();
        Simulateur.getImmeubleSimu().getAscenseur(ascenseurId).niveauTriger(etage);
    }

    @Override
    public void visuRun()
    {
        
    }

}
