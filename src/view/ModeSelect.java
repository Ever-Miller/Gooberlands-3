package view;
import game.TrainerSelectState;
import game.GameManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * File: ModeSelect.java
 *
 * Purpose:
 *     Displays the game mode selection screen.
 *
 *     Allows the player to:
 *       - Enter Story Mode
 *       - Start a Friend Battle
 *       - Return to the main menu
 *
 *     This class functions purely as a VIEW and is responsible
 *     only for navigation between high-level game modes.
 */
public class ModeSelect extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;
	
	/**
	 * Constructs the mode selection screen.
	 *
	 * @param stage active JavaFX stage
	 */
	public ModeSelect(Stage stage){
		this.stage = stage;
		execute();
	}

	/**
	 * Builds and renders the mode selection UI.
	 *
	 * This includes:
	 *   - Mode selection buttons
	 *   - Title header
	 *   - Back navigation
	 *   - Settings access
	 */
	public void execute() {
		
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		Label titleLabel = new Label("CHOOSE YOUR PATH");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.BLACK);
        titleShadow.setOffsetX(3);
        titleShadow.setOffsetY(3);
        titleLabel.setEffect(titleShadow);
		
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(30);

        Button btnStory = ViewStyles.createStyledButton("STORY MODE");
        Button btnFriend = ViewStyles.createStyledButton("FRIEND BATTLE");
        Button btnBack = ViewStyles.createStyledButton("BACK");
        
        btnStory.setOnAction(e -> {
        	SaveSlotSelect next_page = new SaveSlotSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene); 
        });

        btnFriend.setOnAction(e -> {
            FriendBattleSelect next_page = new FriendBattleSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });

        btnBack.setOnAction(e -> {
            MainPage next_page = new MainPage(stage, true);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new ModeSelect(stage));
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
		
        buttonBox.getChildren().addAll(titleLabel, btnStory, btnFriend, btnBack);
        this.getChildren().addAll(background, buttonBox, settingsCog);
	}
}
