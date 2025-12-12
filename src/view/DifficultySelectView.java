package view;

import ai.Difficulty;
import game.DifficultySelectState;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * File: DifficultySelectView.java
 *
 * Purpose:
 *     Displays the difficulty selection screen at game start.
 *
 *     Allows the player to:
 *       - Choose a difficulty level
 *       - Trigger the corresponding game state update
 *       - Navigate back to save-slot selection
 *
 *     This class is a pure VIEW component and delegates all
 *     state changes to {@link DifficultySelectState}.
 */
public class DifficultySelectView extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/** Game state handling difficulty selection logic. */
    private DifficultySelectState state;

    /**
     * Constructs the difficulty selection screen.
     *
     * @param stage active JavaFX stage
     * @param state difficulty selection game state
     */
    public DifficultySelectView(Stage stage, DifficultySelectState state) {
        this.stage = stage;
        this.state = state;
        execute();
    }
    
    /**
     * Builds and renders the difficulty selection UI.
     *
     * This includes:
     *   - Title header
     *   - Difficulty buttons with visual indicators
     *   - Back navigation button
     */
    private void execute() {
        Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);

        Label title = new Label("SELECT DIFFICULTY");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        title.setEffect(new DropShadow(3, Color.BLACK));

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(title);

        for (Difficulty d : Difficulty.values()) {
        	HBox diffBox = new HBox(20);
        	diffBox.setAlignment(Pos.CENTER);
        	
            Button btn = ViewStyles.createStyledButton(d.toString());
            
            ImageView diffSprite = ViewStyles.getDiffSprite(d);
    		if (diffSprite != null) {
    			diffSprite.setPreserveRatio(true);
    			diffSprite.setFitHeight(100);
    			diffSprite.setFitWidth(100);
    		}
    		
            
            btn.setOnAction(e -> {
            	state.selectDifficulty(d);
            	AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/character_select.mp3");
            });
            
            diffBox.getChildren().addAll(btn, diffSprite);
            content.getChildren().add(diffBox);
        }
        
        Button backBtn = ViewStyles.createStyledButton("BACK");
        backBtn.setOnAction(e -> {
            stage.getScene().setRoot(new SaveSlotSelect(stage));
        });
        content.getChildren().add(backBtn);

        this.getChildren().addAll(background, content);
    }
    
}
