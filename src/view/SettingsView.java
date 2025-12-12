package view;

import game.GameManager;
import game.MenuState;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * File: SettingsView.java
 *
 * Purpose:
 *     Displays the settings and options menu.
 *
 *     This screen allows the player to:
 *       - Adjust master audio volume
 *       - Return to the previous screen
 *       - Navigate to the main menu
 *       - Exit and save the game
 *
 *     All persistent changes are delegated to {@link GameManager}
 *     and {@link AudioManager}.
 */
public class SettingsView extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/** Action executed when returning to the previous view. */
	private Runnable onBackAction;
		
	/**
	 * Constructs the settings menu.
	 *
	 * @param stage active JavaFX stage
	 * @param onBackAction callback invoked when returning
	 */
	public SettingsView(Stage stage, Runnable onBackAction) {
        this.stage = stage;
        this.onBackAction = onBackAction;
        execute();
	}
	
	/**
	 * Builds and renders the settings UI.
	 *
	 * This includes:
	 *   - Master volume control
	 *   - Back and menu navigation
	 *   - Application exit option
	 */
	private void execute() {
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		Label titleLabel = new Label("SETTINGS");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        titleLabel.setEffect(new DropShadow(3, Color.BLACK));
        
        Label volumeLabel = new Label("Master Volume");
        volumeLabel.setTextFill(Color.LIGHTGRAY);
        volumeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        double currentVol = AudioManager.getInstance().getMasterVolume();
        Slider volumeSlider = new Slider(0, 100, currentVol * 100);
        volumeSlider.setMaxWidth(400);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setStyle("-fx-control-inner-background: #FF5500;");
        
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double newVolume = newVal.doubleValue() / 100.0;
            AudioManager.getInstance().setMasterVolume(newVolume);
        });
        
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(titleLabel, volumeLabel, volumeSlider);

        Button btnBack = ViewStyles.createStyledButton("BACK");
        btnBack.setOnAction(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });
        
        Button menuButton = ViewStyles.createStyledButton("GO TO MAIN MENU");
        menuButton.setOnAction(e -> {
            GameManager.getInstance().setState(new MenuState(GameManager.getInstance()));
        });
        
        Text exitText = new Text("exit");
        exitText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));
        exitText.setFill(Color.RED);
        exitText.setStroke(Color.WHITE); 
        exitText.setStrokeWidth(1.5);
        
        Button closeBtn = new Button();
        closeBtn.setGraphic(exitText);
        closeBtn.setStyle(
        	    "-fx-background-color: transparent;" + 
        	    "-fx-cursor: hand;"
        	);
        
        closeBtn.setOnAction(e -> {
        	GameManager.getInstance().saveOnClose();
            Platform.exit();
            System.exit(0);
        });
        
        closeBtn.setOnMouseEntered(e -> exitText.setText("exit (if you're a nerd)"));
        closeBtn.setOnMouseExited(e -> exitText.setText("exit"));
        
        StackPane.setAlignment(closeBtn, Pos.TOP_LEFT);
        StackPane.setMargin(closeBtn, new javafx.geometry.Insets(10));

        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        
        if (!onBackAction.toString().substring(0, 9).equals("view.Main")) {
        	layout.getChildren().addAll(contentBox, btnBack, menuButton);
        	GameManager.getInstance().saveOnClose();
        }
        else {
        	layout.getChildren().addAll(contentBox, btnBack);
        }
        
        this.getChildren().addAll(background, layout, closeBtn);
	}
}
