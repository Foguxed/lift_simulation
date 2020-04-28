package fr.fogux.lift_simulator;

public abstract class GestionnaireDeTaches
{
    protected Long innerTime;

    public GestionnaireDeTaches()
    {
        innerTime = 0l;
    }


    public long innerTime()
    {
        return innerTime;
    }

    public abstract void runExecuting();

    public abstract boolean marcheArriereEnCours();

    public abstract void update();
    /*
     * protected static void refillBuffer() { while(bufferPartition.size() < 10) {
     * addOnePartitionLine(); } }
     *
     * protected static void addOnePartitionLine() {
     * bufferPartition.add(Evenement.genererEvenement(GestionnaireDeFichiers.
     * getNextPartitionLine())); }
     */

    public abstract void dispose();

}
