package fr.fogux.dedale.function;


public class Utils
{
    /**
     * 
     * @param sortedArray le tableau ordonné par ordre croissant
     * @param key
     * @param low index du minorant le plus petit (-1 possible)
     * @param high index du majorant le plus grand
     * @return l'index i le plus grand tel que tableau[i] <= key high exclus
     */
    public static int rechercheDichotomique(final double[] sortedArray,final double key, int low, int high) 
    {
        while (low +1< high) 
        {
            int mid = (low + high) / 2;
            if (sortedArray[mid] > key) 
            {
                high = mid;
            } 
            else 
            {
                low = mid;
            }
        }
        return low;
    }
    
    /**
     * 
     * @param sortedArray le tableau ordonné par ordre croissant
     * @param key
     * @return l'index i le plus grand tel que tableau[i] <= key
     * -1 et tableau.length sont des valeurs possibles de i;
     */
    public static int rechercheDichotomique(final double[] sortedArray,final double key)
    {
        return rechercheDichotomique(sortedArray, key,-1,sortedArray.length+1);
    }
    
    /**
     * 
     * @param sortedArray le tableau ordonné par ordre croissant
     * @param key
     * @return l'index i le plus grand tel que tableau[i] <= key ou alors 
     * les index 0 et length-1 (si key n'est pas dans le tableau)
     */
    public static int rechercheDichotomiqueBornee(final double[] sortedArray,final double key)
    {
        return rechercheDichotomique(sortedArray, key,0,sortedArray.length);
    }
    
    /**
     * 
     * @param sortedArray le tableau ordonné par ordre croissant
     * @param key
     * @return l'index i le plus grand tel que tableau[i] <= key ou alors 
     * -1 si pour tout i, tableau[i] > key;
     */
    
    public static int rechercheDichotomiqueClassique(final double[] sortedArray, final double key)
    {
    	return rechercheDichotomique(sortedArray,key,-1,sortedArray.length);
    }
    
    /**
     * 
     * @param f function
     * @param scaleX inverted scale (if the function f is defined on [0;1] and scaleX = 2,
     *  the returned function will be defined on [0;0.5])
     * @return
     */
    public static FunctionDouble getInvertScaledXFunction(FunctionDouble f, double scaleX)
    {
        return new FctComposee(f,new FctPolynome(0,scaleX));
    }
    
    public static FunctionDouble getScaledXFunction(FunctionDouble f, double scaleX)
    {
        return new FctComposee(f,new FctPolynome(0,1/scaleX));
    }
}
