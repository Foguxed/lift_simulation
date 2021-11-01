package fr.fogux.lift_simulator.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class TempJournalOutput extends BufferedWriter
{
    protected final BufferedWriter trueJournOutput;
    protected final TempWriter tempWriter;
    public TempJournalOutput(final BufferedWriter trueJournOutput,final TempWriter tempWriter)
    {
        super(tempWriter);
        this.tempWriter = tempWriter;
        this.trueJournOutput = trueJournOutput;
    }

    public TempJournalOutput(final TempJournalOutput shadowed)
    {
        this(shadowed.trueJournOutput,new TempWriter(shadowed.tempWriter));
    }

    public void printInJournOutput() throws IOException
    {
        flush();
        final List<char[]> writes = tempWriter.listWrites;
        for(final char[] c :writes)
        {
            trueJournOutput.write(c);
        }
    }


}
