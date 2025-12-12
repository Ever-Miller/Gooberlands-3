/**
 * File: BattlePage.java
 *
 * Purpose:
 *     Represents the primary battle UI screen.
 *
 * Responsibilities:
 *     - Render player and enemy trainers and Goobers
 *     - Display HP / XP bars and active status effects
 *     - Allow selection of moves, abilities, items, and Goober switches
 *     - Animate attacks and transitions
 *     - Present intro dialogue and post-battle results
 *
 * Design Notes:
 *     This class functions purely as the VIEW layer in the MVC architecture.
 *     All battle logic is delegated to {@link BattleController}.
 */


package view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.List;

import action.Action;
import action.ActionType;
import action.ItemAction;
import action.TrainerAction;
import battle.BattleTurnResult;
import controller.BattleController;
import effects.Effect;
import effects.EffectType;
import game.EndGameState;
import game.GameManager;
import game.MapState;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import items.Item;
import models.Goober;
import models.GooberMove;
import models.MoveDescriptions;
import models.Trainer;

import game.MultiplayerBattleState;


/**
 * Main battle screen UI.
 *
 * Handles user interaction and visual feedback for battles.
 */
public class BattlePage extends StackPane{

	/** JavaFX stage used to swap scenes and overlays. */
	private Stage stage;
	/** Controller managing battle logic. */
    private BattleController controller;
    /** Level index for determining background and rewards. */
    private int levelIdx;

    // --- UI Components: HUD ---
    private Label playerHpLabel, enemyHpLabel, playerXpLabel, enemyXpLabel;
    private Rectangle playerHpBar, enemyHpBar, playerXpBar, enemyXpBar;
    private Label playerNameLabel, playerLevelLabel;
    private Label enemyNameLabel, enemyLevelLabel;
    private HBox playerStatusIcons, enemyStatusIcons;
    
    // --- UI Components: Visuals ---
    private ImageView playerGooberView, enemyGooberView;
    private ImageView playerTrainerSprite, opponentTrainerSprite;
    private VBox spriteContainer;
    
    // --- Move Menu Components ---
    private VBox moveMenu;
    private Button[] moveButtons;
    private Button abilityButton;
    private Label moveDescriptionLabel;
    
    /** Battle log output area. */
    private TextArea battleLogArea;
    
    // --- Intro / Dialogue State ---
    private VBox introOverlay;
    private Label introText;
    private HBox introDialogueRow;
    private String opponentOpeningLine, playerOpeningLine;
    private int introStep = 0;
    
    /** Pending action selected by the player. */
    private Action pendingPlayerAction;

	
    /**
	 * Constructs a BattlePage.
	 *
	 * @param stage JavaFX stage
	 * @param controller battle controller
	 * @param levelIdx level index
	 */
	public BattlePage(Stage stage, BattleController controller, int levelIdx) {
		this.stage = stage;
        this.controller = controller;
        this.levelIdx = levelIdx;
        
        controller.resetAbilityUsed();
        
        execute();
        updateUIFromModel();
        showIntroDialogue();
	}
	
	/**
	 * Builds and lays out the battle UI.
	 */
	public void execute() {
		BorderPane mainLayout = new BorderPane();
		mainLayout.setPadding(new Insets(45, 0, 45, 0));
		
		ImageView bg = createBackgroundForLevel();
		bg.setFitWidth(1140);
        bg.setFitHeight(640);
		
		// Enemy Field (Top)
		HBox enemyField = new HBox(30);
        enemyField.setAlignment(Pos.CENTER);
        enemyField.setPadding(new Insets(10, 0, 0, 0));
        
        VBox enemyStatusBox = createStatusBox(false);
        enemyGooberView = new ImageView();
        enemyGooberView.setFitHeight(170);
        enemyGooberView.setPreserveRatio(true);
        
        opponentTrainerSprite = ViewStyles.loadSprite(controller.getOpponent().getName(), true);
        if (opponentTrainerSprite != null) {
            opponentTrainerSprite.setFitHeight(160); 
            opponentTrainerSprite.setPreserveRatio(true);
        } else {
            opponentTrainerSprite = new ImageView();
        }

        
        enemyField.getChildren().addAll(enemyStatusBox, opponentTrainerSprite, enemyGooberView);
        
        HBox playerField = new HBox(30);
        playerField.setAlignment(Pos.CENTER);
        
        playerGooberView = new ImageView();
        playerGooberView.setFitHeight(170);
        playerGooberView.setPreserveRatio(true);
        spriteContainer = new VBox(playerGooberView);
        
        playerTrainerSprite = ViewStyles.loadSprite(controller.getPlayer().getName(), false); // Back view
        if (playerTrainerSprite != null) {
            playerTrainerSprite.setFitHeight(160);
            playerTrainerSprite.setPreserveRatio(true);
        } else {
            playerTrainerSprite = new ImageView();
        }
        
        VBox playerStatusBox = createStatusBox(true);

        playerField.getChildren().addAll(playerTrainerSprite, spriteContainer, playerStatusBox);
        
        HBox bottomSection = new HBox(15);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(15, 0, 0, 0));
        bottomSection.setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        
        VBox controlsBox = new VBox(10);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(0, 0, 10, 0));
        
        Button fightBtn = ViewStyles.createStyledButton("SELECT MOVE");
        fightBtn.setPrefWidth(300);
        fightBtn.setOnAction(e -> showMoveMenu());

        HBox subMenu = new HBox(20);
        subMenu.setAlignment(Pos.CENTER);
        subMenu.setPadding(new Insets(0, 0, 10, 0));
        
        Button bagBtn = ViewStyles.createStyledButton("BAG");
        bagBtn.setPrefWidth(200);
        bagBtn.setOnAction(e -> {
            Bag bagView = new Bag(stage, this);
            stage.getScene().setRoot(bagView);
        });
        
        Button runBtn = ViewStyles.createStyledButton("RUN");
        runBtn.setPrefWidth(200);
        runBtn.setOnAction(e -> {
        	AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/intro_sound.mp3");
        	controller.onRunSelected();
        });
        
        runBtn.setOnMouseEntered(e -> {
        	runBtn.setStyle("-fx-background-color: #FF7700;" +
                    "-fx-border-color: #FFFFFF;" +     
                    "-fx-border-width: 4px;" +
                    "-fx-background-radius: 30;" +
                    "-fx-border-radius: 30;" +
                    "-fx-font-family: 'Comic Sans MS', Impact;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 22px;" +           
                    "-fx-text-fill: #FFFFFF;" +        
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 8, 0, 0, 5);");
        	runBtn.setText("COWARD");
        });
        
        runBtn.setOnMouseExited(e -> {
        	runBtn.setStyle("-fx-background-color: #FF5500;" +
                    "-fx-border-color: #FFFFFF;" +
                    "-fx-border-width: 4px;" +         
                    "-fx-background-radius: 30;" +     
                    "-fx-border-radius: 30;" +
                    "-fx-font-family: 'Comic Sans MS', Impact;" + 
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 22px;" +
                    "-fx-text-fill: #FFFFFF;" +        
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 3);");
        	runBtn.setText("RUN");
        });

        Button switchBtn = ViewStyles.createStyledButton("GOOBERS");
        switchBtn.setPrefWidth(200);
        switchBtn.setOnAction(e -> {
            SpriteSelect spriteView = new SpriteSelect(stage, this);
            stage.getScene().setRoot(spriteView);
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(this);
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        subMenu.getChildren().addAll(bagBtn, runBtn);
        controlsBox.getChildren().addAll(fightBtn, switchBtn, subMenu);
        
        battleLogArea = new TextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setWrapText(true);
        battleLogArea.setPrefHeight(100);
        battleLogArea.setPrefWidth(500);
        battleLogArea.setStyle("-fx-control-inner-background: #222; -fx-text-fill: white; -fx-font-family: 'Consolas', 'Monospace'; -fx-font-size: 14px; -fx-background-radius: 5;");
        battleLogArea.setText("Battle Started!\n\n");
        battleLogArea.setPadding(new Insets(0, 0, 10, 0));
        
        bottomSection.getChildren().addAll(controlsBox, battleLogArea);

        mainLayout.setTop(enemyField);
        mainLayout.setCenter(playerField);
        mainLayout.setBottom(bottomSection);

        this.getChildren().addAll(new StackPane(bg), mainLayout, settingsCog);
	}
	
	/**
	 * Updates all UI elements to reflect the current battle state.
	 * Also refreshes the move menu if it is currently visible.
	 */
	public void updateStats() {
        updateUIFromModel();
        if (moveMenu != null && moveMenu.isVisible()) {
            populateMoveButtonsFromModel();
        }
    }

	/**
	 * Synchronizes UI with current battle state.
	 */
	private void updateUIFromModel() {
        updateTrainerState(controller.getPlayer(), playerNameLabel, playerLevelLabel, playerHpLabel, playerXpLabel, playerHpBar, playerXpBar, playerGooberView, true);
        updateTrainerState(controller.getOpponent(), enemyNameLabel, enemyLevelLabel, enemyHpLabel, enemyXpLabel, enemyHpBar, enemyXpBar, enemyGooberView, false);
	}
	
	/**
	 * Updates the HUD for a single trainer.
	 *
	 * @param t the trainer being rendered
	 * @param name label displaying Goober name
	 * @param lvl label displaying level
	 * @param hpTxt HP text label
	 * @param xpTxt XP text label
	 * @param hpBar HP bar rectangle
	 * @param xpBar XP bar rectangle
	 * @param sprite Goober sprite image
	 * @param isPlayer true if this is the player trainer
	 */
	private void updateTrainerState(Trainer t, Label name, Label lvl, Label hpTxt, Label xpTxt, Rectangle hpBar, Rectangle xpBar, ImageView sprite, boolean isPlayer) {
        if (t == null || t.getActiveGoober() == null) return;
        Goober g = t.getActiveGoober();
        
        name.setText(g.getName());
        lvl.setText("Lv." + g.getLevel());
        hpTxt.setText(g.getCurrentHp() + "/" + g.getMaxHp());
        
        xpTxt.setText(g.getXpManager().getCurrentXp() + "/" + g.getXpManager().getMaxXp());
        
        double pct = (double) g.getCurrentHp() / g.getMaxHp();
        hpBar.setWidth(250 * Math.max(0, Math.min(1, pct)));
        hpBar.setFill(pct > 0.5 ? Color.GREEN : pct > 0.2 ? Color.ORANGE : Color.RED);
        
        double xpPct = (double) g.getXpManager().getCurrentXp() / g.getXpManager().getMaxXp();
        xpBar.setWidth(250 * Math.max(0, Math.min(1, xpPct)));

        boolean showFront = !isPlayer; 
        ImageView newSprite = ViewStyles.loadSprite(g.getName(), showFront);
        
        if (newSprite != null && (sprite.getImage() == null || !sprite.getImage().equals(newSprite.getImage()))) {
            sprite.setImage(newSprite.getImage());
        }
        
        HBox iconBox = isPlayer ? playerStatusIcons : enemyStatusIcons;
        if (iconBox != null) {
        	iconBox.getChildren().clear();
        	
        	for (Effect e : g.getEffects()) {
        		ImageView icon = createEffectIcon(e.getType(), e.getStrength());
        		if (icon != null) {
        			iconBox.getChildren().add(icon);
        		}
        	}
        }
    }
	
	/**
	 * Handles move button selection.
	 *
	 * @param index selected move index
	 */
	private void handleMoveSelected(int index) {
		controller.onAttackSelected(index);
		if (moveMenu != null) moveMenu.setVisible(false);
    }

	/**
	 * Creates a HUD box displaying name, HP, XP,
	 * and active status effects for a trainer.
	 *
	 * @param isPlayer true if this HUD belongs to the player
	 * @return configured status box
	 */
	private VBox createStatusBox(boolean isPlayer) {
		VBox box = new VBox(5);
        box.setPrefSize(400, 100);
        box.setMaxSize(400, 100);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3; -fx-background-radius: 10; -fx-border-radius: 10;");
        
        // Name and Level Row
        BorderPane topRow = new BorderPane();
        Label nameLbl = new Label("???");
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLbl.setMaxWidth(280);
        nameLbl.setTextOverrun(OverrunStyle.ELLIPSIS);
        
        HBox statusIcons = new HBox(5);
        statusIcons.setAlignment(Pos.CENTER_LEFT);
        statusIcons.setMinHeight(24);
        
        Label lvlLbl = new Label("Lv.1");
        lvlLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        topRow.setLeft(nameLbl);
        topRow.setRight(lvlLbl);
        
        VBox lvlAndXp = new VBox(10);
        lvlAndXp.setAlignment(Pos.CENTER_LEFT);
        
        StackPane hpBarContainer = new StackPane();
        hpBarContainer.setAlignment(Pos.CENTER_LEFT);
        Rectangle bgBar = new Rectangle(250, 15, Color.LIGHTGRAY);
        Rectangle fillBar = new Rectangle(250, 15, Color.GREEN);
        hpBarContainer.getChildren().addAll(bgBar, fillBar);
        StackPane.setAlignment(fillBar, Pos.CENTER_LEFT);
        
        StackPane xpBarContainer = new StackPane();
        xpBarContainer.setAlignment(Pos.CENTER_LEFT);
        Rectangle xpBar = new Rectangle(250, 10, Color.LIGHTGRAY);
        Rectangle xpFillBar = new Rectangle(250, 10, Color.BLUE);
        xpBarContainer.getChildren().addAll(xpBar, xpFillBar);
        StackPane.setAlignment(xpFillBar, Pos.CENTER_LEFT);
        
        Label hpNums = new Label("0/0");
        hpNums.setFont(Font.font("Arial", 14));
        
        Label xpNums = new Label("0/0");
        xpNums.setFont(Font.font("Arial", 14));
        
        lvlAndXp.getChildren().addAll(hpBarContainer, hpNums, xpBarContainer, xpNums);

        box.getChildren().addAll(topRow, statusIcons, lvlAndXp);
        
        if (isPlayer) {
            playerNameLabel = nameLbl;
            playerLevelLabel = lvlLbl;
            playerHpBar = fillBar;
            playerHpLabel = hpNums;
            playerXpBar = xpFillBar;
            playerXpLabel = xpNums;
            playerStatusIcons = statusIcons;
        } else {
            enemyNameLabel = nameLbl;
            enemyLevelLabel = lvlLbl;
            enemyHpBar = fillBar;
            enemyHpLabel = hpNums;
            enemyXpBar = xpFillBar;
            enemyXpLabel = xpNums;
            enemyStatusIcons = statusIcons;
        }
        
        return box;
	}
	  

	/**
	 * Loads the background image for the current battle level.
	 *
	 * @return background image view
	 */
	private ImageView createBackgroundForLevel() {
		String path = getBackgroundPathForLevel(levelIdx);
		
		try {
			// Load via classpath resource
			URL url = BattlePage.class.getResource(path);
			if (url == null) {
				System.out.println("Battle background not found at " + path + ", using fallback.");
				return createFallbackBackground();
			}
			
			Image img = new Image(url.toExternalForm());
			return new ImageView(img);
		} catch (Exception event) {
			System.out.println("Error loading battle background for level " + levelIdx + ": " + event.getMessage());
			return createFallbackBackground();
		}
	}

	/**
	 * Returns the background image path for a given level.
	 *
	 * @param levelIdx the level index
	 * @return resource path to background image
	 */
	private String getBackgroundPathForLevel(int levelIdx) {
		// Adjust these paths to match your actual PNG names
		switch (levelIdx) {
			case 1:
				return "/view/assets/images/Backdrop/Battle/level1.png";
			
			case 2: 
				return "/view/assets/images/Backdrop/Battle/level2.png";
			
			case 3:
				return "/view/assets/images/Backdrop/Battle/level3.png";
			
			case 4: 
				return "/view/assets/images/Backdrop/Battle/level4.png";
			
			case 5:
				return "/view/assets/images/Backdrop/Battle/level5.png";
			
			case 6:
				return "/view/assets/images/Backdrop/Battle/level6.png";
			
			case 7: 
				return "/view/assets/images/Backdrop/Battle/level7.png";
			
			default:
				// Fallback if something weird happens
				return "/view/assets/images/Backdrop/Battle/Desert.png";
		}
	}

	/**
	 * Returns a fallback battle background if loading fails.
	 *
	 * @return fallback image view
	 */
	private ImageView createFallbackBackground() {
		try {
			URL url = BattlePage.class.getResource("/view/assets/images/Backdrop/Battle/Desert.png");
			
			if (url != null) {
				return new ImageView(new Image(url.toExternalForm()));
			}
		} catch (Exception ignored) {}      
		
		// Ultra-fallback: empty ImageView so we don't crash
		return new ImageView();
	}

	/**
	 * Displays the move selection menu.
	 */
	private void showMoveMenu() {
		if (moveMenu == null) {
			buildMoveMenu();
		}
		populateMoveButtonsFromModel();
		moveMenu.setVisible(true);
		
	}
	
	/**
	 * Builds the move selection UI overlay.
	 */
	private void buildMoveMenu() {
		moveMenu = new VBox(10);
        moveMenu.setAlignment(Pos.CENTER);
        moveMenu.setPadding(new Insets(20));
        // Dark panel, rounded corners, white border, slight shadow
        moveMenu.setStyle("-fx-background-color: rgba(30, 30, 30, 0.95); " +
                          "-fx-background-radius: 20; " +
                          "-fx-border-color: #FFFFFF; " +
                          "-fx-border-width: 3; " +
                          "-fx-border-radius: 20; " +
                          "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 5);");
        
        moveMenu.setMaxWidth(800);
        moveMenu.setVisible(false);

        Label title = new Label("Select a Move");
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        moveButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            Button btn = ViewStyles.createStyledButton("-");
            btn.setPrefWidth(300);
            btn.setPrefHeight(60);

            final int index = i;
            btn.setOnAction(event -> handleMoveSelected(index));
            moveButtons[i] = btn;
            
            grid.add(btn, i % 2, i / 2); 
        }
        
        abilityButton = ViewStyles.createStyledButton("ABILITY");
        abilityButton.setPrefWidth(620); // Full width of grid
        abilityButton.setPrefHeight(60);
        abilityButton.setOnAction(e -> {
        	submitPlayerAction(new TrainerAction(controller.getPlayer()));
        });
        

        moveDescriptionLabel = new Label("Hover over a move to see details...");
        moveDescriptionLabel.setWrapText(true);
        moveDescriptionLabel.setAlignment(Pos.CENTER);
        moveDescriptionLabel.setPrefWidth(600);
        moveDescriptionLabel.setTextAlignment(TextAlignment.CENTER);
        moveDescriptionLabel.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 16px; -fx-font-family: 'Arial'; -fx-background-color: rgba(0,0,0,0.3); -fx-padding: 5; -fx-background-radius: 5;");
        moveDescriptionLabel.setMinHeight(80);
        
        Button back = ViewStyles.createStyledButton("Back");
        back.setPrefWidth(300);
        back.setPrefHeight(60);
        back.setOnAction(e -> moveMenu.setVisible(false));

        moveMenu.getChildren().addAll(title, grid, abilityButton, moveDescriptionLabel, back);

        StackPane.setAlignment(moveMenu, Pos.BOTTOM_CENTER);
        StackPane.setMargin(moveMenu, new Insets(60, 0, 60, 0));

        this.getChildren().add(moveMenu);
		
	}
	  
	/**
	 * Plays an attack animation on a given sprite.
	 *
	 * @param subject the sprite to animate
	 */
	private void playAttackAnimation(ImageView subject) {
		if (subject == null) return;

        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.millis(100));
        tt.setNode(playerGooberView);
        tt.setByX(30);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
 
	}
	
	/**
	 * Executes a full turn of battle, resolving actions
	 * and updating visuals and logs.
	 */
	private void runTurn() {
		if (controller == null || controller.gameIsFinished()) {
			return;
		}
		
		Action playerAction = getPlayerAction();
        if (playerAction == null) return;
        
        Action opponentAction = controller.getAiAction();
        BattleTurnResult result = controller.resolveTurn(playerAction, opponentAction);
        showTurnLogs(result.getLogs());
		updateUIFromModel();
		
		boolean playerAttacked = (playerAction.getType() == ActionType.ATTACK);
		
		if (playerAttacked) {
			playAttackAnimation(playerGooberView);
		}
		
		PauseTransition delay = new PauseTransition(Duration.seconds(1.0));
		delay.setOnFinished(e -> {
			Trainer opponent = controller.getOpponent();
			if (opponent != null && opponent.getActiveGoober() != null && 
				!opponent.getActiveGoober().isFainted() && 
				!opponent.getActiveGoober().getState().isStunned()) {
				
				playAttackAnimation(enemyGooberView);
			}
			
			if (!controller.gameIsFinished()) {
                String newGoober = controller.attemptAutoSwitch();
                if (newGoober != null) {
                    showTurnLogs(List.of("Go " + newGoober + "!\n"));
                    updateUIFromModel();
                }
            }
			
			if (controller.gameIsFinished()) {
				Platform.runLater(this::showBattleResult);
			}
		});
		delay.play();
	}
	
	/**
	 * Submits the player's chosen action to be processed this turn.
	 *
	 * @param action the player's selected action
	 */
	public void submitPlayerAction(Action action) {
	    if (action == null) return;

	    // Hide move menu if it was open
	    if (moveMenu != null) {
	        moveMenu.setVisible(false);
	    }

	    // Are we in multiplayer or single-player?
	    var gm = GameManager.getInstance();
	    var state = gm.getState();

	    if (state instanceof game.MultiplayerBattleState) {
	        // MULTIPLAYER: let MultiplayerBattleState handle sending & resolving
	        ((game.MultiplayerBattleState) state).onLocalAction(action);
	    } else {
	        // SINGLE-PLAYER: keep old behavior
	        pendingPlayerAction = action;
	        runTurn();
	    }
	}
	
	/**
	 * Applies an item selected from the Bag screen.
	 *
	 * @param item the item to use
	 */
	public void useItemFromBag(Item item) {
	    if (item == null || controller.getPlayer() == null) return;

	    Trainer targetTrainer = item.isTargetSelf() ? controller.getPlayer() : controller.getOpponent();
	    Action action = new ItemAction(controller.getPlayer(), targetTrainer, item.getName());

	    var gm = GameManager.getInstance();
	    var state = gm.getState();

	    if (state instanceof MultiplayerBattleState) {
	        ((game.MultiplayerBattleState) state).onLocalAction(action);
	    } else {
	        pendingPlayerAction = action;
	        runTurn();
	    }
	}
	
	/**
	 * Displays the opening dialogue sequence
	 * before the first battle turn.
	 */
	private void showIntroDialogue() {
		// If we somehow don't have trainers, just skip the intro.
		Trainer player = controller.getPlayer();
        Trainer opponent = controller.getOpponent();

        if (player == null || opponent == null) return;
		
		// Pull random lines (can be null if no file exists)
		opponentOpeningLine = DialogueManager.getRandomLine(opponent, "opening");
		playerOpeningLine = DialogueManager.getRandomLine(player, "opening");
		
		// Load trainer sprites (full body, front)
		playerTrainerSprite = ViewStyles.loadSprite(player.getName(), true);
		opponentTrainerSprite = ViewStyles.loadSprite(opponent.getName(), true);
		
		if (playerTrainerSprite != null) {
			playerTrainerSprite.setFitHeight(320);
			playerTrainerSprite.setPreserveRatio(true); 
		} 
		
		if (opponentTrainerSprite != null) {
			opponentTrainerSprite.setFitHeight(320);
			opponentTrainerSprite.setPreserveRatio(true); 
		}
		
		
		introOverlay = new VBox(20);
        introOverlay.setAlignment(Pos.CENTER);
        introOverlay.setPadding(new Insets(20));
        introOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        introOverlay.setPrefSize(1140, 640);
		
		introText = ViewStyles.createStyledLabel("");
		introText.setWrapText(true);
        introText.setAlignment(Pos.CENTER);
        introText.setTextAlignment(TextAlignment.CENTER);
        introText.setPrefWidth(700);
        introText.setMinHeight(200);
		
		introDialogueRow = new HBox(40);
		introDialogueRow.setAlignment(Pos.CENTER);
		
		// Let it size more naturally inside the overlay 
		updateIntroLayoutForStep(0);
		
		// Styled "Next" button 
		Button nextBtn = ViewStyles.createStyledButton("Next");
		nextBtn.setOnAction(event -> advanceIntro());
		
		introOverlay.getChildren().addAll(introDialogueRow, nextBtn);
		
		this.getChildren().add(introOverlay);
		
		introStep = 0;
		advanceIntro();
	}
	
	/**
	 * Updates the layout of the intro dialogue based on dialogue step.
	 *
	 * @param step current dialogue step
	 */
	private void updateIntroLayoutForStep(int step) {
		introDialogueRow.getChildren().clear();
		
		switch (step) {
			case 0: 
				// Opponent speaks: text LEFT, sprite RIGHT
				introDialogueRow.getChildren().add(introText);
				if (opponentTrainerSprite != null) {
					introDialogueRow.getChildren().add(opponentTrainerSprite);
				}
				break;
			case 1: // Player speaks: sprite LEFT, text RIGHT
				if (playerTrainerSprite != null) {
					introDialogueRow.getChildren().add(playerTrainerSprite);
				}
				introDialogueRow.getChildren().add(introText);
				break;
			
			default: // Final line: text centered, no sprite (or keep last)
				introDialogueRow.getChildren().add(introText);
				break;
		}
		
	}

	/**
	 * Advances the intro dialogue sequence.
	 */
	private void advanceIntro() {
	    if (introText == null) {
	    	return;
	    }
	    
	    Trainer p = controller.getPlayer();
        Trainer o = controller.getOpponent();
        String pName = (p != null) ? p.getName() : "You";
        String oName = (o != null) ? o.getName() : "Opponent";
        
        String pGoober = (p != null && p.getActiveGoober() != null) ? p.getActiveGoober().getName() : "Goober";
	    
	    switch (introStep) {
	    	case 0: {
	    	// Opponent’s randomized opening line (or fallback)
	    		String core = (opponentOpeningLine != null)? opponentOpeningLine: "Let's see what your team can do."; 
	    		introText.setText(oName + ": \"" + core + "\"");
	    		updateIntroLayoutForStep(0);
	    		introStep++;
	    		break;
	    	}
	    	case 1: {
	    		// Player’s randomized response (or fallback)
	    		String core = (playerOpeningLine != null)? playerOpeningLine: "I'm not backing down.";
	    		introText.setText(pName + ": \"" + core + " " + pGoober + ", I choose you!\"");
	    		updateIntroLayoutForStep(1);
	    		introStep++;
	    		break;
	    	}
	    	default:
	    		// Remove overlay and let the player act
	    		if (introOverlay != null) {
	    			this.getChildren().remove(introOverlay);
	    			introOverlay = null;
	    		}
	    	break;
	    }
	}
	
	
	/**
	 * Populates move buttons based on the active Goober's move set
	 * and updates button states accordingly.
	 */
	private void populateMoveButtonsFromModel() {
		Trainer p = controller.getPlayer();
        if (p == null || p.getActiveGoober() == null || moveButtons == null) return;
		
		Goober g = p.getActiveGoober();
		List<GooberMove> usable = g.getUsableMoves(); // new list each call
		
		for (int i = 0; i < moveButtons.length; i++) {
			Button btn = moveButtons[i];
			
			btn.setOnMouseEntered(null); 
            btn.setOnMouseExited(null);
            
            String baseStyle = "-fx-background-color: #FF5500; -fx-text-fill: white; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;";
            String hoverStyle = "-fx-background-color: #FF7700; -fx-text-fill: white; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;";
            String disabledStyle = "-fx-background-color: #555555; -fx-text-fill: #AAAAAA; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #777777; -fx-border-width: 2;";
            
			if (i < usable.size()) {
				GooberMove m = usable.get(i);
				btn.setDisable(false);
                btn.setText(m.getName());
                btn.setStyle(baseStyle);
				
				String desc = m.getName() + 
                        "\nDMG: " + (m.getDamage() + (m.getDamage() != 0 ? g.getAttack() : 0)) + 
                        " | Hit: " + Math.round(m.getHitChance() * 100) + "%" +
                        " | Crit: " + Math.round(m.getCritChance() * 100) + "%" + 
                        ((m.getEffect() != null) ? (" | Effect: " + m.getEffect().getType().getName()) : "");

				
				desc += "\n" + MoveDescriptions.getDescription(m.getName());
				
				final String finalDesc = desc;
				
                btn.setOnMouseEntered(event -> {
                    btn.setStyle(hoverStyle);
                    moveDescriptionLabel.setText(finalDesc);
                });
                btn.setOnMouseExited(event -> {
                    btn.setStyle(baseStyle);
                    moveDescriptionLabel.setText("Hover over a move to see details...");
                });
			} else {// No move in this slot
				btn.setDisable(true);
                btn.setText("—");
                btn.setStyle(disabledStyle);
			}
			
			if (abilityButton != null) {
	            abilityButton.setOnMouseEntered(null);
	            abilityButton.setOnMouseExited(null);
	            
	            boolean usableAbility = controller.isTrainerAbilityUsable();
	            
	            if (usableAbility) {
	                abilityButton.setDisable(false);
	                abilityButton.setText("ABILITY: " + p.getRole().getName());
	                abilityButton.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;");
	                
	                String abilityDesc = "TRAINER ABILITY: " + p.getRole().getActiveDescription();
	                
	                abilityButton.setOnMouseEntered(e -> {
	                    abilityButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;");
	                    moveDescriptionLabel.setText(abilityDesc);
	                });
	                
	                abilityButton.setOnMouseExited(e -> {
	                    abilityButton.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: white; -fx-border-width: 2; -fx-cursor: hand;");
	                    moveDescriptionLabel.setText("Hover over a move to see details...");
	                });
	            } else {
	                abilityButton.setDisable(true);
	                abilityButton.setText(p.hasUsedAbility() ? "ABILITY USED" : "ABILITY UNAVAILABLE");
	                abilityButton.setStyle(disabledStyle);
	            }
	        }
		}
	}
	
	/**
	 * Returns the player's trainer.
	 *
	 * @return player trainer
	 */
	public Trainer getPlayerTrainer() { return controller.getPlayer(); }
	/**
	 * Returns the opponent trainer.
	 *
	 * @return opponent trainer
	 */
    public Trainer getOpponentTrainer() { return controller.getOpponent(); }

    /**
     * Retrieves and clears the pending player action.
     *
     * @return selected action for this turn
     */
    public Action getPlayerAction() {
        Action a = pendingPlayerAction;
        pendingPlayerAction = null;
        return a;
    }
	
    /**
     * Clears any pending player action.
     */
	public void clearSelection() {
		pendingPlayerAction = null;
	}
	
	/**
	 * Displays the end-of-battle results screen
	 * and handles post-battle transitions.
	 */
	private void showBattleResult() {
		Trainer winner = controller.getWinner();
        boolean playerWon = (winner == controller.getPlayer());
        
        if (playerWon) {
            controller.processBattleRewards();
        }
        
        int stars = 0;
        if (playerWon) {
             for (Goober g : controller.getPlayer().getTeam()) {
                if (!g.isFainted()) stars++;
            }
        }
        int coins = stars;
        
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setMaxSize(500, 400);
        box.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 20; -fx-border-color: white; -fx-border-width: 4; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, black, 20, 0, 0, 0);");
        
        Label title = new Label(playerWon ? "VICTORY ROYAL" : "GET GOOD KID");
        title.setTextFill(playerWon ? Color.GOLD : Color.RED);
        title.setFont(Font.font("Impact", 60));
        
        HBox starBox = new HBox(10);
        starBox.setAlignment(Pos.CENTER);
        if (playerWon) {
            for (int i=0; i<3; i++) {
                Label s = new Label("★");
                s.setFont(Font.font(50));
                s.setTextFill(i < stars ? Color.YELLOW : Color.GRAY);
                s.setStyle("-fx-effect: dropshadow(one-pass-box, black, 5, 0, 0, 2);");
                starBox.getChildren().add(s);
            }
        }
        
        Label coinLbl = new Label(playerWon ? "+" + coins + " Coins" : "No Coins earned.");
        coinLbl.setTextFill(Color.WHITE);
        coinLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        String buttonTxt = (levelIdx == 7 && playerWon) ? "LOPUNNY IS YOURS" : "RETURN TO MAP";
        Button backBtn = ViewStyles.createStyledButton(buttonTxt);
        backBtn.setOnAction(e -> {
            // Check for Final Boss win scenario
            if (levelIdx == 7 && playerWon) {
            	Rectangle fade = new Rectangle(1140, 640, Color.BLACK);
                fade.setOpacity(0);
                this.getChildren().add(fade);
                
                FadeTransition ft = new FadeTransition(Duration.seconds(5.0), fade);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.setOnFinished(event -> {
                    GameManager.getInstance().setState(new EndGameState(GameManager.getInstance()));
                });
                
                AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/outro_sound.mp3", 35.0, 10000.0);
                ft.play();
                
            } else {
            	AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/intro_sound.mp3");
                GameManager.getInstance().setState(new MapState(GameManager.getInstance()));
            }
        });
		
        box.getChildren().addAll(title, starBox, coinLbl, backBtn);
        overlay.getChildren().add(box);
        
        this.getChildren().add(overlay);
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), overlay);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
	}
	
	/**
	 * Creates an icon representing a status effect.
	 *
	 * @param type effect type
	 * @param strength effect strength modifier
	 * @return icon image view or null
	 */
	private ImageView createEffectIcon(EffectType type, Double strength) {
		String path = "/view/assets/images/other/";
		
		switch (type) {
		case HEAL:   return null;
        case POISON: path += "poison.png"; break;
        case STUN:   path += "stun.png"; break;
        case DIZZY:  path += "dizzy.png"; break;
        
        case DAMAGE_MODIFICATION:  
        	if (strength > 0) path += "damage_increase.png"; 
        	else path += "damage_reduction.png";
        	break;
        	
        case DEFENCE_MODIFICATION: 
        	if (strength > 0) path += "defense_increase.png"; 
        	else path += "defense_reduction.png";
        	break;
        	
        case CRIT_MODIFICATION:    
        	path += "crit_increase.png"; break;
        	
        default: return null;
		}
		
		try {
	        Image img = new Image(getClass().getResource(path).toExternalForm());
	        ImageView icon = new ImageView(img);
	        icon.setFitWidth(24); 
	        icon.setFitHeight(24);
	        icon.setPreserveRatio(true);
	        return icon;
	    } catch (Exception e) {
	        System.out.println("Missing icon for: " + type.getName());
	        return null;
	    }
	}
	
	/**
	 * Appends battle log messages to the log area.
	 *
	 * @param logs list of turn log messages
	 */
	private void showTurnLogs(List<String> logs) {
		if (logs == null || logs.isEmpty()) return;
	    
	    StringBuilder sb = new StringBuilder();
	    for (String log : logs) {
	    	if (log.isEmpty()) {
	    		sb.append("\n");
	    		continue;
	    	}
	        sb.append("• ").append(log).append("\n");
	    }
	    
	    sb.append("\n------------------------------------------\n");
	    
	    // Append logs to the text area
	    if (battleLogArea != null) {
	    	battleLogArea.appendText(sb.toString());
	    	battleLogArea.appendText("\n");
	    	battleLogArea.setScrollTop(Double.MAX_VALUE);
	    }
	}
	
	public void showTurnResult(BattleTurnResult result) {
	    if (result == null) return;

	    showTurnLogs(result.getLogs());
	    updateUIFromModel();
	    // Optional: you can reuse the attack animations here if you want
	}

	public void refreshFromState() {
	    updateStats();
	}
	
	
	
}