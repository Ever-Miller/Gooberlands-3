package view;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * File: FriendBattleSelect.java
 *
 * Purpose:
 *     Displays the friend-battle selection screen.
 *
 *     Allows the player to:
 *       - Search for an existing friend battle room
 *       - Create a new friend battle room
 *       - Return to the mode selection screen
 *
 *     This class is a VIEW-only component responsible
 *     for navigation between battle setup screens.
 */
public class FriendBattleSelect extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/**
	 * Constructs the friend battle selection screen.
	 *
	 * @param stage active JavaFX stage
	 */
	public FriendBattleSelect(Stage stage){
		this.stage = stage;
		execute();
	}

	/**
	 * Builds and renders the friend battle selection UI.
	 *
	 * This includes:
	 *   - Search room button
	 *   - Create room button
	 *   - Back navigation button
	 *   - Settings access button
	 */
	public void execute() {
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		BorderPane screen = new BorderPane();
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(45);
		
		Button btn1 = ViewStyles.createStyledButton("Search");
		btn1.setOnAction(e->{
			Search next_page = new Search(stage);
			Scene scene = new Scene(next_page, 1140, 640);
			stage.setScene(scene);
		});
				
		Button btn2 = ViewStyles.createStyledButton("Make");
		btn2.setOnAction(e->{
			RoomMake next_page = new RoomMake(stage);
			Scene scene = new Scene(next_page, 1140, 640);
			stage.setScene(scene);
		});
		
		Button btnBack = ViewStyles.createStyledButton("BACK");
		btnBack.setOnAction(e -> {
			ModeSelect next_page = new ModeSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });
		
		Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new FriendBattleSelect(stage));
        });
		StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
		
		vbox.getChildren().addAll(btn1, btn2, btnBack);
		screen.setCenter(vbox);
		
		this.getChildren().addAll(background, screen, settingsCog);
	}
}
