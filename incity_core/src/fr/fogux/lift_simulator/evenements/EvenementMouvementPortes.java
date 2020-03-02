package fr.fogux.lift_simulator.evenements;

import fr.fogux.lift_simulator.GestionnaireDeTaches;
import fr.fogux.lift_simulator.Simulateur;
import fr.fogux.lift_simulator.fichiers.DataTagCompound;
import fr.fogux.lift_simulator.fichiers.TagNames;
import fr.fogux.lift_simulator.physic.TimeConfig;
import fr.fogux.lift_simulator.utils.Utils;

public class EvenementMouvementPortes extends AnimatedEvent
{
    protected final int ascenseurId;
    protected final int etageId;
    protected final boolean isOuverture;

    public EvenementMouvementPortes(long time, DataTagCompound data)
    {
        super(time, data);
        ascenseurId = data.getInt(TagNames.ascenseurId);
        isOuverture = data.getBoolean(TagNames.isOuverture);
        etageId = data.getInt(TagNames.etage);
    }

    public EvenementMouvementPortes(int ascenseurId, int etageId, boolean isOuverture)
    {
        super(TimeConfig.getDureePortes() + GestionnaireDeTaches.getInnerTime(), true);
        this.ascenseurId = ascenseurId;
        this.etageId = etageId;
        this.isOuverture = isOuverture;
    }

    @Override
    public void simuRun()
    {
        super.simuRun();
        if (isOuverture)
        {
            System.out.println("ouverture des portes a " + time + " ascid " + ascenseurId);
            Simulateur.getImmeubleSimu().getAscenseur(ascenseurId).lorsqueOuvert();
        } else
        {
            Utils.msg(this, " simuRun lorsque ferme debut");
            Simulateur.getImmeubleSimu().getAscenseur(ascenseurId).lorsqueFerme();
            Utils.msg(this, " simuRun lorsque ferme fin" + GestionnaireDeTaches.getInnerTime());
        }
    }

    @Override
    protected void printFieldsIn(DataTagCompound compound)
    {
        super.printFieldsIn(compound);
        compound.setInt(TagNames.ascenseurId, ascenseurId);
        compound.setInt(TagNames.etage, etageId);
        compound.setBoolean(TagNames.isOuverture, isOuverture);
    }

    @Override
    protected void runAnimation(long timeDebut, long duree)
    {
        System.out.println("run anim " + timeDebut);
        Simulateur.getImmeubleVisu().getEtage(etageId).getPorte(ascenseurId).animation(timeDebut, duree, isOuverture);
    }

    @Override
    protected void sortieAnimation()
    {

    }

}
