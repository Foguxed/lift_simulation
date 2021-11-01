package fr.fogux.lift_simulator.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TempWriter extends Writer
{

	private boolean closed = false;
	public final List<char[]> listWrites;
	
	
	public TempWriter() 
	{
		listWrites = new ArrayList<char[]>();
	}
	
	public TempWriter(TempWriter shadowed) 
	{
		listWrites = new ArrayList<char[]>(shadowed.listWrites);
	}
	
	@Override
	public void close() throws IOException 
	{
		closed = true;
	}

	@Override
	public void flush() throws IOException 
	{
		
	}

	@Override
	public void write(char[] cbuf,final int off,final int len) throws IOException 
	{
		char[] newCharT = new char[len];
		for(int i = 0; i < len; i ++)
		{
			newCharT[i] = cbuf[off + i];
		}
		listWrites.add(newCharT);
		if(closed)
		{
			throw new IllegalStateException("TempWriter already closed");
		}
	}

}
