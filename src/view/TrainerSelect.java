package view;

import controller.TrainerSelectController;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Goober;
import models.GooberFactory;
import models.GooberType;
import models.TrainerRole;

import java.net.URL;
import java.util.List;

/**
 * File: TrainerSelect.java
 *
 * Purpose:
 *     Presents the pre-battle team selection interface.
 *
 * Responsibilities:
 *     - Allow the player to choose a Trainer role
 *     - Allow selection of a Special Goober and Normal Goobers
 *     - Display visual slots representing current selections
 *     - Provide info popups describing Trainers and Goobers
 *     - Signal readiness state to begin the battle
 *
 * Design Notes:
 *     This class functions strictly as the VIEW layer in MVC.
 *     All selection logic and validation is delegated to
 *     {@link TrainerSelectController}.
 */
public class TrainerSelect extends StackPane {
	private Stage stage;
    private TrainerSelectController controller;
    
    private StackPane trainerSlot, specialSlot, normalSlot1, normalSlot2;
    private VBox selectionPanel;
    private Button startBtn;
    
    private StackPane infoOverlay;
    private VBox infoContentBox;
    
    /**
     * Constructs the trainer and team selection screen.
     *
     * @param stage the primary application stage
     * @param controller controller handling selection logic
     */
    public TrainerSelect(Stage stage, TrainerSelectController controller) {
    	this.stage = stage;
        this.controller = controller;
        this.controller.setView(this); 
        execute();
    }
    
    /**
     * Builds and lays out all UI components for the selection screen.
     */
	public void execute() {
		Image bgImg = new Image("view/assets/images/Backdrop/lobby_and_main/TeamSelectPage.png");
		
		ImageView background = new ImageView(bgImg);
        background.setFitWidth(1140);
        background.setFitHeight(640);
        background.setPreserveRatio(false);
        
        trainerSlot = createSlot("Select Trainer", -2, 60, 1.7); 
        trainerSlot.setOnMouseClicked(e -> showTrainerSelection());
        
        specialSlot = createSlot("Select Special", -166, 45, 1.2);
        specialSlot.setOnMouseClicked(e -> {
            controller.onSlotClicked(0);
            showGooberSelection(true);
        });
        
        normalSlot1 = createSlot("Select Normal", 140, 50, 1.2);
        normalSlot1.setOnMouseClicked(e -> {
            controller.onSlotClicked(1);
            showGooberSelection(false);
        });
        
        normalSlot2 = createSlot("Select Normal", 270, 45, 1.0);
        normalSlot2.setOnMouseClicked(e -> {
            controller.onSlotClicked(2);
            showGooberSelection(false);
        });
        
        VBox uiLayer = new VBox();
        uiLayer.setMouseTransparent(true); 
        uiLayer.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("SQUAD UP");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Impact", 50));
        title.setEffect(new DropShadow(10, Color.BLACK));
        
        Button randomBtn = new Button("RANDOM TEAM");
        randomBtn.setPrefSize(200, 60);
        randomBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 20px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10;"
        );
        randomBtn.setTranslateX(-465);
        randomBtn.setTranslateY(285);
        randomBtn.setOnAction(e -> controller.onRandomizeClicked());
        
        startBtn = new Button("READY UP");
        startBtn.setPrefSize(200, 60);
        updateStartButton(false);
        startBtn.setTranslateX(465);
        startBtn.setTranslateY(285);
        startBtn.setOnAction(e -> controller.onStartClicked());
        
        Button backBtn = new Button("LEAVE MATCH");
        backBtn.setPrefSize(200, 60);
        backBtn.setStyle(
            "-fx-background-color: rgba(255, 85, 0, 0.8);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 20px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10;"
        );
        backBtn.setTranslateX(-460);
        backBtn.setTranslateY(-280);
        backBtn.setOnAction(e -> {
        	AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/main_page.mp3");
            stage.getScene().setRoot(new ModeSelect(stage));
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
        	stage.getScene().setRoot(this);
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
		
        
        selectionPanel = new VBox(10);
        selectionPanel.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-padding: 20; -fx-background-radius: 0 10 10 0;");
        selectionPanel.setMaxWidth(300);
        selectionPanel.setTranslateX(-600);
        StackPane.setAlignment(selectionPanel, Pos.CENTER_LEFT);
        
        setupInfoOverlay();
        
        this.getChildren().addAll(background, normalSlot2, trainerSlot, specialSlot, normalSlot1, uiLayer, randomBtn, startBtn, backBtn, selectionPanel, infoOverlay, settingsCog);
	}
	
	/**
     * Initializes the overlay used for displaying info popups.
     */
	private void setupInfoOverlay() {
	    infoOverlay = new StackPane();
	    infoOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
	    infoOverlay.setVisible(false);
	    
	    infoContentBox = new VBox(10);
	    infoContentBox.setAlignment(Pos.CENTER);
	    infoContentBox.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
	    
	    Button closeInfoBtn = new Button("CLOSE");
	    closeInfoBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
	    closeInfoBtn.setOnAction(e -> infoOverlay.setVisible(false));
	    
	    infoOverlay.setOnMouseClicked(e -> {
	        if (e.getTarget() == infoOverlay) infoOverlay.setVisible(false);
	    });
	    
	    infoOverlay.getChildren().add(infoContentBox);
	}
	
	/**
     * Displays an informational popup over the screen.
     *
     * @param content node containing info UI
     */
	private void showInfoPopup(Node content) {
	    infoContentBox.getChildren().clear();
	    infoContentBox.getChildren().add(content);
	    
	    Button closeInfoBtn = new Button("CLOSE");
	    closeInfoBtn.setPrefWidth(100);
	    closeInfoBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 5;");
	    closeInfoBtn.setOnAction(e -> infoOverlay.setVisible(false));
	    
	    infoContentBox.getChildren().add(closeInfoBtn);
	    infoOverlay.setVisible(true);
	}
	
	/**
     * Refreshes the displayed team slots based on current selections.
     *
     * @param team list representing selected Goobers
     */
	public void refreshTeamDisplay(List<Goober> team) {
		models.TrainerRole role = controller.getSelectedTrainer();
		String trainerName = (role != null) ? role.getName() : "Select Trainer";
		boolean trainerSelected = (role != null);
		
		updateSlotVisual(trainerSlot, trainerName, trainerSelected);
		
		if (team.get(0) != null) updateSlotVisual(specialSlot, team.get(0).getName(), true);
        else clearSlot(specialSlot, "Select Special");
        
        if (team.get(1) != null) updateSlotVisual(normalSlot1, team.get(1).getName(), true);
        else clearSlot(normalSlot1, "Select Normal");
        
        if (team.get(2) != null) updateSlotVisual(normalSlot2, team.get(2).getName(), true);
        else clearSlot(normalSlot2, "Select Normal");
    }
	
	/**
     * Enables or disables the start button depending on readiness.
     *
     * @param ready whether the team selection is complete
     */
	public void updateStartButton(boolean ready) {
        startBtn.setDisable(!ready);
        if (ready) {
        	startBtn.setStyle(
                    "-fx-background-color: #2ecc71;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 24px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-opacity: 1.0;"
                );
        } else {
        	startBtn.setStyle(
                    "-fx-background-color: #c0392b;" +
                    "-fx-text-fill: #bdc3c7;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 24px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-opacity: 1.0;"
                );
        }
    }
	
	
    /**
     * Creates a selectable slot used during team composition.
     *
     * Each slot visually represents either:
     *   - The Trainer role
     *   - A Special Goober
     *   - A Normal Goober
     *
     * The slot displays a placeholder label until populated,
     * supports hover highlighting, and routes click events
     * to the appropriate selection panel.
     *
     * @param placeholder default label shown when slot is empty
     * @param x horizontal translation offset
     * @param y vertical translation offset
     * @param scale visual scale factor applied to the slot
     * @return fully configured StackPane representing the slot
     */

	private StackPane createSlot(String placeholder, double x, double y, double scale) {
        StackPane slot = new StackPane();
        slot.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
        slot.setTranslateX(x);
        slot.setTranslateY(y);
        slot.setScaleX(scale);
        slot.setScaleY(scale);
        
        Label lbl = new Label(placeholder);
        lbl.setTextFill(Color.WHITE);
        lbl.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5; -fx-background-radius: 5;");
        
        ImageView avatar = new ImageView();
        avatar.setFitHeight(150);
        avatar.setPreserveRatio(true);
        
        slot.getChildren().addAll(avatar, lbl);
        slot.setCursor(javafx.scene.Cursor.HAND);
        
        slot.setOnMouseEntered(e -> slot.setEffect(new DropShadow(20, Color.WHITE)));
        slot.setOnMouseExited(e -> slot.setEffect(null));
        
        slot.setOnMouseClicked(e -> {
            System.out.println("Slot Clicked: " + placeholder);
            if (placeholder.contains("Trainer")) showTrainerSelection();
            else showGooberSelection(placeholder.contains("Special"));
        });
        
        return slot;
    }
	
    /**
     * Opens the sidebar panel for selecting a Trainer role.
     *
     * This method populates the selection panel with all
     * available {@link TrainerRole} values and allows the
     * player to inspect and select a role.
     */

	private void showTrainerSelection() {
        populateSidebar("CHOOSE CLASS", TrainerRole.values());
    }
	
    /**
     * Opens the sidebar panel for selecting a Goober.
     *
     * Depending on the flag provided, this method restricts
     * the selection to either Special or Normal Goobers.
     *
     * @param specialsOnly true to show Special Goobers only;
     *                     false to show Normal Goobers
     */

	private void showGooberSelection(boolean specialsOnly) {
		if (specialsOnly) {
			populateSidebar("DRAFT GOOBER", GooberFactory.SPECIAL_GOOBERS.toArray()); 
		} else {
			populateSidebar("DRAFT GOOBER", GooberFactory.NORMAL_GOOBERS.toArray()); 
		}
    }
	
    /**
     * Populates the left-side selection panel with selectable entries.
     *
     * Each row represents a selectable Trainer role or Goober,
     * includes a name label and an info button, and forwards
     * selection events to the controller.
     *
     * Certain restricted entries are intentionally filtered.
     *
     * @param title header text displayed at the top of the panel
     * @param items array of selectable objects (TrainerRole or Goober identifiers)
     */

	private void populateSidebar(String title, Object[] items) {
        selectionPanel.getChildren().clear();
        
        Label head = new Label(title);
        head.setTextFill(Color.YELLOW);
        head.setFont(Font.font("Arial", 20));
        selectionPanel.getChildren().add(head);
        
        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        closeBtn.setOnAction(e -> selectionPanel.setTranslateX(-600));
        selectionPanel.getChildren().add(closeBtn);
        
        for (Object item : items) {
            if (item.toString().equals("JOKER") || item.toString().equals("LOPUNNY")) continue;
            
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: gray; -fx-padding: 5; -fx-cursor: hand;");
            
            Label nameLbl;
            if (item instanceof TrainerRole) {
            	nameLbl = new Label(((TrainerRole) item).getName());
            } else {
            	nameLbl = new Label(item.toString());
            }
            
            nameLbl.setTextFill(Color.WHITE);
            nameLbl.setFont(Font.font("Arial", 14));
            
            Button infoBtn = new Button("?");
            infoBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
            
            infoBtn.setOnAction(e -> {
                showInfoPopup(buildInfoView(item));
            });
            
            row.getChildren().addAll(nameLbl, infoBtn);
            
            row.setOnMouseClicked(e -> {
                if (item instanceof TrainerRole) controller.onRoleSelected((TrainerRole)item);
                else controller.onGooberClicked(item.toString());
            });
            
            selectionPanel.getChildren().add(row);
        }

        selectionPanel.setTranslateX(0);
    }
	
    /**
     * Builds an informational view for a selectable entity.
     *
     * Returns either a {@link TrainerInfoView} or
     * {@link GooberInfoView} depending on the object type.
     *
     * This view is shown inside a modal overlay.
     *
     * @param item TrainerRole or Goober identifier
     * @return JavaFX Node containing the informational UI
     */

	private Node buildInfoView(Object item) {
        if (item instanceof TrainerRole) return new TrainerInfoView((TrainerRole) item);
        return new GooberInfoView(GooberFactory.getGoober(item.toString()));
    }
	
    /**
     * Refreshes all slot visuals and readiness state.
     *
     * Updates:
     *   - Trainer slot
     *   - Goober slots
     *   - Start button enabled state
     *
     * This method is called by the controller whenever the
     * selection state changes.
     *
     * @param role currently selected Trainer role
     * @param team current list of selected Goobers
     */

	public void updateDisplay(TrainerRole role, List<Goober> team) {
		updateSlotVisual(trainerSlot, role != null ? role.getName() : "Select Trainer", role != null);
        refreshTeamDisplay(team);
        updateStartButton(controller.isReady());
    }
	
    /**
     * Updates the visual contents of a slot.
     *
     * When filled, the slot displays the entity sprite and
     * hides the placeholder label.
     *
     * When unfilled, the sprite is cleared and the placeholder
     * label is restored.
     *
     * @param slot target slot to update
     * @param name name of the entity represented
     * @param filled true if the slot should display a sprite
     */

	private void updateSlotVisual(StackPane slot, String name, boolean filled) {
		ImageView img = (ImageView) slot.getChildren().get(0);
        Label lbl = (Label) slot.getChildren().get(1);
        
        lbl.setText(name);
        if (filled) {
            try {
                String path = ViewStyles.getSpritePath(name, true);
                URL url = getClass().getResource(path);
                if (url == null) {
                    throw new RuntimeException("File not found: " + path);
                }
                Image sprite = new Image(url.toExternalForm());
                img.setImage(sprite);
                lbl.setVisible(false);
            } catch (Exception e) { 
                System.out.println("Could not load image for: " + name);
                img.setImage(null);
                lbl.setVisible(true);
                lbl.setStyle("-fx-background-color: green; -fx-padding: 5;");
            }
        } else {
            img.setImage(null);
            lbl.setVisible(true);
            lbl.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5;");
        }
    }
	
    /**
     * Resets a slot to its empty placeholder state.
     *
     * This clears any displayed sprite and restores the
     * default placeholder label styling and text.
     *
     * @param slot slot to clear
     * @param placeholder default placeholder text
     */

	private void clearSlot(StackPane slot, String placeholder) {
		ImageView img = (ImageView) slot.getChildren().get(0);
        Label lbl = (Label) slot.getChildren().get(1);
        img.setImage(null);
        lbl.setText(placeholder);
        lbl.setVisible(true);
        lbl.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5;");
    }
	
	/**
     * Animates a fade-to-black transition when leaving the screen.
     *
     * @param onFinished callback executed after animation completes
     */
	public void animateExit(Runnable onFinished) {
        Rectangle fadeOverlay = new Rectangle(1140, 640, Color.BLACK);
        fadeOverlay.setOpacity(0);
        fadeOverlay.setMouseTransparent(true);
        fadeOverlay.setMouseTransparent(false);
        
        this.getChildren().add(fadeOverlay);

        FadeTransition ft = new FadeTransition(Duration.seconds(3.5), fadeOverlay);
        AudioManager.getInstance().playMusic("/view/assets/audio/BackgroundMusic/intro_sound.mp3", 33.0, 1500.0);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(e -> onFinished.run());
        ft.play();
    }
}