package view;

import game.EndGameState;
import game.GameManager;
import game.MenuState;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * File: View.java
 *
 * Purpose:
 *     Serves as the JavaFX application entry point for Gooberlands III.
 *
 * Responsibilities:
 *     - Initialize the primary JavaFX {@link Stage}
 *     - Configure global window settings (title, icon, style)
 *     - Bootstrap the {@link GameManager}
 *     - Load the initial {@link Scene} and menu state
 *
 * Design Notes:
 *     This class does not contain any game logic.
 *     It exists purely to bridge JavaFX lifecycle management with the
 *     game's state-driven architecture managed by {@link GameManager}.
 *
 *     All scene transitions and gameplay flow are delegated to
 *     the GameManager and GameState system.
 */
public class View extends Application {

    /**
     * JavaFX lifecycle entry method.
     *
     * Initializes:
     *   - Window styling and title
     *   - Application icon
     *   - Game manager and initial game state
     *   - Main menu scene
     *
     * @param stage primary JavaFX stage provided by the runtime
     * @throws Exception if JavaFX initialization fails
     */
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Gooberlands 3");
		stage.initStyle(StageStyle.UNDECORATED);
		
		GameManager gm = new GameManager(stage);
		Image appIcon = new Image("/view/assets/images/other/GL3_logo.png"); 
		
		stage.getIcons().add(appIcon);
		
		Scene scene = new Scene(new MainPage(stage), 1140, 640);
		
		gm.setState(new MenuState(gm));
	    stage.setScene(scene);
	    stage.show();
	}

    /**
     * Standard JVM entry point.
     *
     * Delegates control to the JavaFX runtime, which then
     * invokes {@link #start(Stage)}.
     *
     * @param args command-line arguments
     */
	public static void main(String[] args) {
		launch(args);
	}

}
