package view;
import game.GameManager;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;

/**
 * File: MainPage.java
 *
 * Purpose:
 *     Displays the main title screen of the game.
 *
 *     This screen:
 *       - Presents the game title and subtitle
 *       - Provides entry into the game flow
 *       - Allows access to settings
 *       - Plays background music and entry animation
 *
 *     Window dragging and visual transitions are handled
 *     entirely within this view.
 */
public class MainPage extends StackPane {

	/** JavaFX stage used for scene navigation and window movement. */
	private Stage stage;

	/** Indicates if the view is being returned to from another screen. */
	private boolean returning;

	/** Mouse X offset used for window dragging. */
	private double xOffset = 0;

	/** Mouse Y offset used for window dragging. */
    private double yOffset = 0;
	
	/**
	 * Constructs the main menu screen.
	 *
	 * @param stage active JavaFX stage
	 */
	public MainPage(Stage stage){	
		this(stage, false);
	}
	
	/**
	 * Constructs the main menu screen with return-state control.
	 *
	 * @param stage active JavaFX stage
	 * @param returning true if returning from another screen
	 */
	public MainPage(Stage stage, boolean returning) {	
		this.stage = stage;
		this.returning = returning;
		basicView();	
	}
	
	/**
	 * Builds and renders the main menu UI.
	 *
	 * This includes:
	 *   - Background image
	 *   - Title and subtitle labels
	 *   - Entry button to game mode selection
	 *   - Settings access
	 *   - Optional fade-in animation
	 */
	private void basicView() {
		Image backImg = new Image("view/assets/images/Backdrop/lobby_and_main/title_screen.png"); 
        ImageView background = new ImageView(backImg);
        background.setFitWidth(1140);
        background.setFitHeight(640);
        background.setPreserveRatio(true);
        
        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(20);
        
        Label titleLabel = new Label("Gooberlands III");
        titleLabel.setTextFill(Color.RED);
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 65));
        titleLabel.setRotate(5);
        titleLabel.setStyle("-fx-stroke: white; -fx-stroke-width: 2px;");
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0.0); 
        dropShadow.setOffsetX(5.0); 
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.BLACK); 
        titleLabel.setEffect(dropShadow);
        
        Text subTitle = new Text("(We apologize for creating this)");
        subTitle.setFill(Color.RED);
        subTitle.setStroke(Color.BLACK);
        subTitle.setStrokeWidth(1.0);
        subTitle.setFont(Font.font("Arial", FontPosture.ITALIC, 30));
        subTitle.setStyle("-fx-padding: 5px; -fx-background-radius: 5;");
        
        Button playBtn = ViewStyles.createStyledButton("ENTER THE GOOBERVERSE");
        
        playBtn.setOnAction(e -> {
            ModeSelect next_page = new ModeSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new MainPage(stage, true));
        });
        
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        menuBox.getChildren().addAll(titleLabel, subTitle, playBtn, settingsCog);
        
        this.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        this.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        
        AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/main_page.mp3");
        
        if (!returning) {
        	Rectangle fadeOverlay = new Rectangle(1140, 640, Color.BLACK);
            fadeOverlay.setMouseTransparent(true);
            
            this.getChildren().addAll(background, menuBox, settingsCog, fadeOverlay);
            
	        FadeTransition fadeIn = new FadeTransition(Duration.seconds(5), fadeOverlay);
	        fadeIn.setFromValue(1.0);
	        fadeIn.setToValue(0.0);
	        fadeIn.setOnFinished(e -> this.getChildren().remove(fadeOverlay));
	        
	        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
	        pause.setOnFinished(event -> {
	        	fadeIn.play();
	        });
	        pause.play();
        } else {
        	this.getChildren().addAll(background, menuBox, settingsCog);
        }
	}
}
