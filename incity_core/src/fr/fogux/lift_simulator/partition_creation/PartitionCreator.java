package fr.fogux.lift_simulator.partition_creation;

import java.util.ArrayList;
import java.util.List;

public class PartitionCreator
{

    public PartitionCreator()
    {
    }

    protected void generate()
    {
        // generatePaquebotNormal();
        generateImmeubleEntreprise(100, 62, 12, 28, 1000, 15 * 60 * 1000);
    }

    /**
     * tout les etages ont la meme probabilite d'etre utilises notre paquebot
     * https://www.msccroisieres.fr/fr-fr/Decouvrez-MSC/Navires-De-Croisiere/MSC-Opera.aspx
     * a les ponts 5 a 13, tous les etages ont la meme proba d etre selectionnes en
     * depart/arrivee 9 etages avec le 0 compris donc 0 a 8
     */
    protected void generatePaquebotNormal()
    {

        final DestinationProfile profileConstantAleatoire = new DestinationProfile(getDestinationsProbaEgales(0, 8, 1));
        final double[] grps =
        { 0.20d, 0.724d, 0.0055d, 0.0140 };
        final GroupProfile grpProfile = new GroupProfile(grps);
        final List<EtageDeDepart> depart = new ArrayList<>();
        for (int i = 0; i < 9; i++)
        {
            depart.add(new EtageDeDepart(i, 1 / 9d, profileConstantAleatoire, grpProfile));
        }
        new Immeuble(depart).genererPersonnesInput(99, 1000 * 60 * 15);

    }

    /**
     * retourne un profile de destination constant entre 0 et nbEtage-1
     */

    protected void generateImmeubleEntreprise(final int etageMax, final double probaIncome, final double probaOutCome,
        final double probaInterEtage, final int nbPersonnesDeplacees, final long duree)
    {

        final List<EtageDestination> versEtages = getDestinationsProbaEgales(1, etageMax, probaInterEtage);
        final List<EtageDestination> totale = new ArrayList<>(versEtages);
        System.out.println(" probaOutCome " + probaOutCome);
        totale.add(new EtageDestination(0, probaOutCome));
        final DestinationProfile profilEtages = new DestinationProfile(totale);
        final DestinationProfile profilEntree = new DestinationProfile(versEtages);

        final double[] grps =
        { 0.85d, 0.10d, 0.03d, 0.02d };
        final GroupProfile grpProfile = new GroupProfile(grps);

        final List<EtageDeDepart> depart = new ArrayList<>();
        depart.add(new EtageDeDepart(0, probaIncome, profilEntree, grpProfile));
        for (int i = 1; i < etageMax + 1; i++)
        {
            System.out.println(" proba etage " + ((probaOutCome + probaInterEtage) / etageMax));
            depart.add(
                new EtageDeDepart(i, (probaOutCome + probaInterEtage) / etageMax, profilEtages, grpProfile));
        }
        new Immeuble(depart).genererPersonnesInput(nbPersonnesDeplacees, duree);
    }

    protected List<EtageDestination> getDestinationsProbaEgales(final int etageMin, final int etageMax,
        final double probaTotaleDuGroupe)
    {
        final List<EtageDestination> destinations = new ArrayList<>();
        final double nbEtagesD = Math.abs((double) (etageMax - etageMin) + 1);
        for (int i = etageMin; i < etageMax + 1; i++)
        {
            destinations.add(new EtageDestination(i, probaTotaleDuGroupe / nbEtagesD));
        }
        return destinations;

    }
}
