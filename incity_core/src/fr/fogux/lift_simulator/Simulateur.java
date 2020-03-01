package fr.fogux.lift_simulator;

import java.io.File;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fr.fogux.lift_simulator.animation.ImmeubleVisu;
import fr.fogux.lift_simulator.animation.PersonneVisu;
import fr.fogux.lift_simulator.evenements.Evenement;
import fr.fogux.lift_simulator.evenements.Evenements;
import fr.fogux.lift_simulator.evenements.animation.EvenementCreationImmeuble;
import fr.fogux.lift_simulator.evenements.animation.EvenementErreur;
import fr.fogux.lift_simulator.exceptions.SimulateurAcceptableException;
import fr.fogux.lift_simulator.fichiers.GestionnaireDeFichiers;
import fr.fogux.lift_simulator.mind.ProgrammeEntryListener;
import fr.fogux.lift_simulator.physic.ImmeubleSimu;
import fr.fogux.lift_simulator.population.PersonneSimu;
import fr.fogux.lift_simulator.screens.GameScreen;
import fr.fogux.lift_simulator.screens.MenuScreen;
import fr.fogux.lift_simulator.utils.AssetsManager;

public class Simulateur extends Game {
	SpriteBatch batch;
	Texture img;
	static ImmeubleSimu immeubleSimu;
	static ImmeubleVisu immeubleVisu;
	protected static Simulateur instance;
	public static boolean animationTime = false;
	protected GameScreen scr;
	protected static GestionnaireDeTachesSimu gestioSimu;

	@Override
	public void create ()
	{
		instance = this;
		batch = new SpriteBatch();
		Evenements.init();
		ProgrammeEntryListener.init();
		menuScreen();


	}

	protected void menuScreen()
	{
		AssetsManager.loadMainMenuAssets();
		final MenuScreen menu = new MenuScreen(this);
		setScreen(menu);
		menu.init();
	}

	public void startSimulation(final File dossier,final boolean playAnimation)
	{
		final String activePrgmName = ProgrammeEntryListener.getActiveProgramName();
		doSimulation(dossier,activePrgmName);
		if(playAnimation)
		{
			GestionnaireDeFichiers.loadVisualisationFile(dossier.getName(),activePrgmName);
			doVisualisation();
		}
		else
		{
			Gdx.app.exit();
		}

	}

	protected void doSimulation(final File dossier,final String activePrgmName)
	{
		PersonneSimu.initClass();
		GestionnaireDeFichiers.setSimulationPath(dossier.getName(),activePrgmName );
		gestioSimu = new GestionnaireDeTachesSimu();

		GestionnaireDeFichiers.loadSimulationFiles();
		immeubleSimu = new ImmeubleSimu(0,8,ProgrammeEntryListener.getActivePrgmNbAscenseurs());
		String erreurMsg = null;
		try
		{
			ProgrammeEntryListener.initPrgm();
			gestioSimu.runExecuting();
		}
		catch(final SimulateurAcceptableException e)
		{
			new EvenementErreur(e.getMessage()).print();
			e.printStackTrace();
			erreurMsg = e.getMessage();
		}
		GestionnaireDeFichiers.unloadSimulationFiles();
		GestionnaireDeFichiers.loadStatFiles();
		if(erreurMsg!=null)
		{
			GestionnaireDeFichiers.printStatAscenseur("ERREUR " + erreurMsg);
			GestionnaireDeFichiers.printStatPersonne("ERREUR " + erreurMsg);
		}
		else
		{
			immeubleSimu.printAscStats();
			PersonneSimu.printPersStats();
		}
		GestionnaireDeFichiers.unloadStatFiles();
		System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		System.out.println("SIMULATION TERMINEE");
		System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
	}
	public static GestionnaireDeTaches getGestionnaireDeTaches()
	{
		return GestionnaireDeTaches.getInstance();
	}

	public static GestionnaireDeTachesSimu getGestionnaireDeTachesSimu()
	{
		return gestioSimu;
	}

	@Override
	public void render ()
	{
		super.render();
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(img, 0, 0);

		batch.end();*/
	}

	@Override
	public void dispose ()
	{
		//batch.dispose();
		//img.dispose();
		//GestionnaireDeFichiers.unloadSimulationFiles();
	}

	public static ImmeubleSimu getImmeubleSimu()
	{
		return immeubleSimu;
	}

	public static ImmeubleVisu getImmeubleVisu()
	{
		return immeubleVisu;
	}

	public static void setImmeubleVisu(final ImmeubleVisu visu)
	{
		immeubleVisu = visu;
	}

	public SpriteBatch getBatch()
	{
		return batch;
	}

	public static Simulateur getSimulateur()
	{
		return instance;
	}

	public GameScreen getGameScreen()
	{
		return scr;
	}

	public void startVisualisation(final File file)
	{
		GestionnaireDeFichiers.loadVisualisationFile(file);
		doVisualisation();
	}

	protected void doVisualisation()
	{
		PersonneVisu.initClass();
		animationTime = true;
		AssetsManager.loadAnimationAssets();

		gestioSimu = null;
		final GestionnaireDeTachesVisu ges = new GestionnaireDeTachesVisu();
		scr = new GameScreen(this,ges);
		setScreen(scr);
		scr.init();
		System.out.println("fin init animation");
		((EvenementCreationImmeuble)Evenement.genererEvenement(GestionnaireDeFichiers.getNextJournalLine())).create();
	}

}
