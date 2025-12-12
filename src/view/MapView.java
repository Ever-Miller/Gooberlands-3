package view;
import controller.WorldController;
import game.GameManager;
import game.Level;
import game.ShopState;
import game.TeamState;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Goober;
import models.Trainer;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * File: MapView.java
 *
 * Purpose:
 *     Displays the world map and level selection screen.
 *
 *     This screen allows the player to:
 *       - View available and locked levels
 *       - Inspect level details and opponent teams
 *       - Enter battles
 *       - Access shop and team management
 *
 *     All progression logic is delegated to {@link WorldController}.
 */
public class MapView extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/** Controller managing world progression and level access. */
    private WorldController controller;

    /**
     * Constructs the map view.
     *
     * @param stage active JavaFX stage
     * @param controller world controller handling progression
     */
    public MapView(Stage stage, WorldController controller) { 
        this.stage = stage;
        this.controller = controller;
        execute();
    }

    /**
     * Builds and renders the world map UI.
     *
     * This includes:
     *   - Map background
     *   - Level selection buttons
     *   - Currency display
     *   - Shop and team navigation
     *   - Settings access
     */
	public void execute() {
		Image img = new Image("/view/assets/images/Backdrop/Level_select/Level_select.png");
		ImageView imgView = new ImageView(img);
		imgView.setFitWidth(1140);
		imgView.setFitHeight(640);
		imgView.setPreserveRatio(true);
		
		HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPickOnBounds(false);
        topBar.setPadding(new javafx.geometry.Insets(20));
        
        Label coinLbl = new Label("💰 " + controller.getPlayerCoins());
        coinLbl.setTextFill(Color.GOLD);
        coinLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        coinLbl.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5 15; -fx-background-radius: 20;");
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(this);
        });
        
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        topBar.getChildren().addAll(coinLbl);
        
        VBox menuButtons = new VBox(10);
        menuButtons.setAlignment(Pos.BOTTOM_RIGHT);
        menuButtons.setPadding(new javafx.geometry.Insets(20));
        menuButtons.setPickOnBounds(false);
        
        Button shopBtn = createMenuButton("ITEM SHOP");
        shopBtn.setOnAction(e -> GameManager.getInstance().setState(new ShopState(GameManager.getInstance())));
        
        Button teamBtn = createMenuButton("MY TEAM");
        teamBtn.setOnAction(e -> GameManager.getInstance().setState(new TeamState(GameManager.getInstance())));
        
        menuButtons.getChildren().addAll(teamBtn, shopBtn);
		
		// Buttons overall
		Pane levelLayer = new Pane();
		// Actual Buttons
		Button level1 = makeButton(40, 40, 830, 76);
		Button level2 = makeButton(40, 40, 385, 100);
		Button level3 = makeButton(40, 40, 772, 242);
		Button level4 = makeButton(40, 40, 367, 285);
		Button level5 = makeButton(40, 40, 739, 412);
		Button level6 = makeButton(40, 40, 345, 421);
		Button level7 = makeButton(40, 40, 557, 555);
		
		level1.setOnAction(event -> handleLevelSelect(1));
		level2.setOnAction(event -> handleLevelSelect(2));
		level3.setOnAction(event -> handleLevelSelect(3));
		level4.setOnAction(event -> handleLevelSelect(4));
		level5.setOnAction(event -> handleLevelSelect(5));
		level6.setOnAction(event -> handleLevelSelect(6));
		level7.setOnAction(event -> handleLevelSelect(7));
		
		levelLayer.getChildren().addAll(level1, level2, level3, level4, level5, level6, level7);
		
		this.getChildren().addAll(imgView, levelLayer, menuButtons, topBar, settingsCog);
        
        playFadeIn();
	}
	
	/**
	 * Creates a styled menu navigation button.
	 *
	 * @param text button label
	 * @return styled button instance
	 */
	private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-cursor: hand;");
        btn.setPrefWidth(150);
        return btn;
    }
	
	/**
	 * Plays a fade-in transition when the map is shown.
	 */
	private void playFadeIn() {
		Rectangle fadeOverlay = new Rectangle(1140, 640, Color.BLACK);
		fadeOverlay.setMouseTransparent(true);
		
		this.getChildren().add(fadeOverlay);

		FadeTransition ft = new FadeTransition(Duration.seconds(2.0), fadeOverlay);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setOnFinished(e -> this.getChildren().remove(fadeOverlay));
		ft.play();
	}

	/**
	 * Handles selection of a level button.
	 *
	 * Locked levels display a warning dialog.
	 *
	 * @param levelNum selected level number
	 */
	private void handleLevelSelect(int levelNum) {
		if (controller.isLevelLocked(levelNum)) {
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Not So Fast Bucko");
            alert.setContentText("Clear the previous stages before doing this one jit.");
            alert.showAndWait();
            return;
		}
		
		Level levelData = controller.getLevelData(levelNum);
		showLevelPopup(levelData);
	}
	
	/**
	 * Displays a modal popup containing level details and
	 * battle entry options.
	 *
	 * @param level selected level data
	 */
	private void showLevelPopup(Level level) {
		StackPane popupContainer = new StackPane();
		popupContainer.setAlignment(Pos.CENTER);
		
		Rectangle overlay = new Rectangle(1140, 640, Color.rgb(0, 0, 0, 0.7));
		
		VBox box = new VBox(15);
		box.setMaxSize(500, 450);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(new Insets(30));
		box.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 20; -fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 20;");
		box.setEffect(new DropShadow(10, Color.BLACK));
		
		Label title = new Label("STAGE " + level.getLevelNum());
		title.setTextFill(Color.WHITE);
		title.setFont(Font.font("Impact", 40));
		
		HBox starsBox = new HBox(5);
		starsBox.setAlignment(Pos.CENTER);
		int score = level.getCompletionLevel();
		for (int i = 0; i < 3; i++) {
			Label star = new Label("★");
			star.setFont(Font.font("Arial", 40));
			if (i < score) {
				star.setTextFill(Color.GOLD);
				star.setEffect(new DropShadow(5, Color.ORANGE));
			} else {
				star.setTextFill(Color.GRAY);
			}
			starsBox.getChildren().add(star);
		}
		
		Trainer enemy = level.getOpponent();
		ImageView enemySprite = ViewStyles.loadSprite(enemy.getName(), true);
		if (enemySprite != null) {
			enemySprite.setFitHeight(150);
			enemySprite.setPreserveRatio(true);
		}
		
		Label enemyName = new Label("OPPONENT: " + enemy.getName().toUpperCase());
		enemyName.setTextFill(Color.WHITE);
		enemyName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		
		HBox teamBox = new HBox(10);
		teamBox.setAlignment(Pos.CENTER);
		teamBox.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-padding: 10; -fx-background-radius: 10;");
		
		int levelSum = 0;
		for (Goober g : enemy.getTeam()) {
			ImageView gSprite = ViewStyles.loadSprite(g.getName(), true);
			if (gSprite != null) {
				gSprite.setFitHeight(50);
				gSprite.setPreserveRatio(true);
				teamBox.getChildren().add(gSprite);
			}
			levelSum += g.getLevel();
		}
		
		Label avgLevel = new Label("AVG ENEMY LVL: " + (levelSum / enemy.getTeam().size()));
		avgLevel.setTextFill(Color.LIGHTGRAY);
		avgLevel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		
		HBox btnBox = new HBox(20);
		btnBox.setAlignment(Pos.CENTER);
		
		Button fightBtn = new Button("FIGHT");
		fightBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 10;");
		fightBtn.setPrefWidth(150);
		
		fightBtn.setOnAction(e -> {
	        // 1. Create a black rectangle covering the entire screen
	        Rectangle fadeToBlack = new Rectangle(1140, 640, Color.BLACK);
	        fadeToBlack.setOpacity(0);
	        fadeToBlack.setMouseTransparent(false);
	        
	        // 2. Add it to the root StackPane (this class)
	        this.getChildren().add(fadeToBlack);
	        
	        // 3. Animate opacity from 0 to 1
	        FadeTransition ft = new FadeTransition(Duration.seconds(1.0), fadeToBlack);
	        ft.setFromValue(0.0);
	        ft.setToValue(1.0);
	        
	        // 4. Switch level ONLY when animation finishes
	        ft.setOnFinished(event -> {
	            controller.startLevel(level.getLevelNum());
	        });
	        
	        ft.play();
	    });
		
		Button closeBtn = new Button("CANCEL");
		closeBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-cursor: hand; -fx-background-radius: 10;");
		closeBtn.setPrefWidth(150);
		closeBtn.setOnAction(e -> {
			this.getChildren().remove(popupContainer);
		});
		
		btnBox.getChildren().addAll(fightBtn, closeBtn);
		
		box.getChildren().addAll(title, starsBox, enemySprite, enemyName, avgLevel, teamBox, btnBox);
		popupContainer.getChildren().addAll(overlay, box);
		
		this.getChildren().add(popupContainer);
	}

	/**
	 * Creates an invisible level interaction button positioned
	 * over the map for selection.
	 *
	 * @param width button width
	 * @param height button height
	 * @param x X-position on map
	 * @param y Y-position on map
	 * @return level selection button
	 */
	private Button makeButton(int width, int height, double x, double y) {
		Button b = new Button("");
		b.setBackground(null);
		b.setPrefSize(width, height);
		b.setLayoutX(x);
		b.setLayoutY(y);
		return b;
	}
}
