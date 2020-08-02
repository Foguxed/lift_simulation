package fr.fogux.dedale.function;

public interface Integrable<T extends FunctionDouble> extends FunctionDouble
{
    /**
     * @param refPointY ordonee au point x
     * @return l'intégrale (l'aire sous la courbe depuis l'origine vers x (peut être <0))
     * 
     */
    public Integrable<T> getIntegrale(double xRefPoint, double refPointY);
}
