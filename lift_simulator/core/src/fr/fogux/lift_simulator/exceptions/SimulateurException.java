package fr.fogux.lift_simulator.exceptions;

public class SimulateurException extends RuntimeException
{
    private static final long serialVersionUID = 2667347647368736247L;
    
    public SimulateurException(String description)
    {
        super(description);
    }
}
