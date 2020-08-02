package fr.fogux.dedale.function;

public class TestFct
{
    public static void tester()
    {
    }
    
    public static void tester(FunctionDouble fct,final double xMin,final double xMax,final long nbPoints)
    {
        double ecart = (xMax-xMin)/(double)nbPoints;
        double v;
        for(double d = xMin; d <= xMax; d += ecart)
        {
            v = fct.getY(d);
            System.out.println(d + " \t " + (Double.isNaN(v)?"":v));
        }
    }
}
