package fr.fogux.dedale.proba;

public class CurveTemplates
{
	
	public static final TemplateProbaCurve GAUSSIAN2S = new TemplateProbaCurve(new double[][]
	{
		{ -1, 0.05 },
		{ -0.5, 0.23 },
		{ 0, 0.4 },
		{ 0.5, 0.23 },
		{ 1, 0.05 } });
	public static final TemplateProbaCurve GAUSSIAN3S = new TemplateProbaCurve(new double[][]
	{
		{ -1, 0.01 },
		{ -2/3, 0.5 },
		{ -1/3, 0.23 },
		{ 0, 0.4 },
		{ 1 / 3, 0.23 },
		{ 2 / 3, 0.5 },
		{ 1, 0.01 } });
	public static final TemplateProbaCurve CHAPITEAU = new TemplateProbaCurve(new double[][]
	{
		{ -1, 0.05 },
		{ -0.75, 0.1 },
		{ -0.5, 0.1 },
		{ -0.25, 0.2 },
		{ 0, 0.4 },
		{ 0.25, 0.2 },
		{ 0.5, 0.1 },
		{ 0.75, 0.1 },
		{ 1, 0.05 } });

	public static final TemplateProbaCurve POSITIVE_CONST = new TemplateProbaCurve(new double[][]
		{
			{ 0, 1 },
			{ 1, 1 }});
	
	public static final TemplateProbaCurve POSITIVE_GAUSSIAN2S = new TemplateProbaCurve(new double[][]
	{
		{ 0, 0.4 },
		{ 0.5, 0.23 },
		{ 1, 0.5 } });
	public static final TemplateProbaCurve POSITIVE_GAUSSIAN3S = new TemplateProbaCurve(new double[][]
	{
		{ 0, 0.4 },
		{ 1 / 3d, 0.23 },
		{ 2 / 3d, 0.05 },
		{ 1, 0}});
	public static final TemplateProbaCurve POSITIVE_CHAPITEAU = new TemplateProbaCurve(new double[][]
	{
		{ 0, 0.4 },
		{ 0.25, 0.2 },
		{ 0.5, 0.1 },
		{ 0.75, 0.1 },
		{ 1, 0.05 } });
}
