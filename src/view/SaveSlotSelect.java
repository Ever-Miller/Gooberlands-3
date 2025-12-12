package view;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import game.TrainerSelectState;
import game.DifficultySelectState;
import game.GameManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * File: SaveSlotSelect.java
 *
 * Purpose:
 *     Displays the save slot selection screen for Story Mode.
 *
 *     This screen allows the player to:
 *       - Load an existing save file
 *       - Start a new game in an empty slot
 *       - Delete existing save data
 *       - Return to mode selection
 *
 *     All persistence logic is delegated to {@link GameManager}.
 */
public class SaveSlotSelect extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;
	
	/**
	 * Constructs the save slot selection screen.
	 *
	 * @param stage active JavaFX stage
	 */
	public SaveSlotSelect(Stage stage) {
        this.stage = stage;
        execute();
    }
	
	/**
	 * Builds and renders the save slot selection UI.
	 *
	 * This includes:
	 *   - Slot buttons for load or new game
	 *   - Optional delete buttons for existing saves
	 *   - Back navigation
	 *   - Settings access
	 */
	private void execute() {
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		Label titleLabel = new Label("SELECT SAVE SLOT");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setOffsetX(3);
        titleShadow.setOffsetY(3);
        titleLabel.setEffect(titleShadow);
        
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox slot1Container = createSlotContainer(1);
        VBox slot2Container = createSlotContainer(2);
        VBox slot3Container = createSlotContainer(3);
        Button btnBack = ViewStyles.createStyledButton("BACK");
        
        btnBack.setOnAction(e -> {
            ModeSelect next_page = new ModeSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new SaveSlotSelect(stage));
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        buttonBox.getChildren().addAll(titleLabel, slot1Container, slot2Container, slot3Container, btnBack);
        this.getChildren().addAll(background, buttonBox, settingsCog);
	}
	
	/**
	 * Creates a UI container representing a single save slot.
	 *
	 * Displays either a load or new-game option depending
	 * on the existence of save data.
	 *
	 * @param slotNum save slot number
	 * @return populated slot container
	 */
	private VBox createSlotContainer(int slotNum) {
		VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        
        Path savePath = Paths.get("save_" + slotNum + ".dat");
        boolean saveExists = Files.exists(savePath);
        
        String text = "SLOT " + slotNum + (saveExists ? " (LOAD)" : " (NEW GAME)");
        Button mainBtn = ViewStyles.createStyledButton(text);
        
        mainBtn.setOnAction(e -> {
            GameManager gm = GameManager.getInstance();
            if (saveExists) {
                AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/intro_sound.mp3");
                boolean loaded = gm.loadGame(slotNum);
                if (!loaded) {
                    System.out.println("Failed to load save " + slotNum);
                }
            } else {
                // New game flow begins with difficulty selection
                gm.setState(new DifficultySelectState(gm, slotNum));
            }
        });
        
        container.getChildren().add(mainBtn);
        
        if (saveExists) {
            Button deleteBtn = new Button("DELETE");
            
            deleteBtn.setStyle(
                "-fx-background-color: #c0392b; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );
            deleteBtn.setPrefWidth(mainBtn.getPrefWidth()); 
            
            deleteBtn.setOnAction(e -> {
                GameManager gm = GameManager.getInstance();
                boolean deleted = gm.deleteSave(slotNum);
                
                if (deleted) {
                    stage.getScene().setRoot(new SaveSlotSelect(stage));
                } else {
                    System.err.println("Failed to delete save slot " + slotNum);
                }
            });
            
            container.getChildren().add(deleteBtn);
        }
        
        return container;
    }
}
