package fr.fogux.lift_simulator;

public abstract class GestionnaireDeTaches
{
    protected Long innerTime = 0l;
    protected static GestionnaireDeTaches instance;

    protected GestionnaireDeTaches()
    {
        instance = this;
    }

    public static GestionnaireDeTaches getInstance()
    {
        return instance;
    }

    protected long innerTime()
    {
        return innerTime;
    }

    public static long getInnerTime()
    {
        return instance.innerTime();
    }

    protected abstract void runExecuting();

    protected abstract boolean marcheArriereEnCours();

    public static boolean marcheArriere()
    {
        return instance.marcheArriereEnCours();
    }

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
