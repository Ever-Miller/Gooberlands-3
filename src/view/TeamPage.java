package view;

import controller.TeamController;
import effects.Effect;
import effects.EffectType;
import game.GameManager;
import game.MapState;
import game.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Goober;
import models.GooberFactory;
import models.GooberMove;
import models.MoveDescriptions;
import models.MoveFactory;
import models.Trainer;
import models.TrainerRole;
import models.XPManager;
/**
 * File: TeamPage.java
 *
 * Purpose:
 *     Displays the full team management screen.
 *
 *     This screen allows the player to:
 *       - View trainer information and role abilities
 *       - Inspect all Goobers on the team
 *       - View detailed stats and unlocked abilities
 *       - Return to the world map
 *
 *     This class functions strictly as the VIEW layer.
 *     All data access and updates are delegated to {@link TeamController}.
 */
public class TeamPage extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

    /** Controller providing team and trainer data. */
    private TeamController controller;

    /**
     * Constructs the team overview screen.
     *
     * @param stage active JavaFX stage
     * @param controller controller managing team data
     */
    public TeamPage(Stage stage, TeamController controller) {
        this.stage = stage;
        this.controller = controller;
        execute();
    }
    
    /**
     * Builds and renders the team page UI.
     *
     * This includes:
     *   - Header with navigation
     *   - Trainer summary panel
     *   - Scrollable list of Goober cards
     */
    private void execute() {
        Rectangle bg = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(30, 50, 30, 50));
        
        BorderPane header = new BorderPane();
        
        // Left: Back Button + Title
        HBox leftHeader = new HBox(20);
        leftHeader.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("⬅ MAP");
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            GameManager.getInstance().setState(new MapState(GameManager.getInstance()));
        });
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        
        Label title = new Label("MY SQUAD");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Impact", 50));
        
        leftHeader.getChildren().addAll(backBtn, title);
        
        header.setLeft(leftHeader);
        mainLayout.setTop(header);
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new MainPage(stage, true));
        });
        
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        HBox contentSplit = new HBox(30);
        contentSplit.setPadding(new Insets(20, 0, 0, 0));
        contentSplit.setAlignment(Pos.TOP_LEFT);

        VBox trainerPane = createTrainerPane();
        
        VBox gooberList = new VBox(20);
        for (Goober g : controller.getTeam()) {
            gooberList.getChildren().add(createGooberCard(g));
        }
        
        ScrollPane scroll = new ScrollPane(gooberList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        HBox.setHgrow(scroll, Priority.ALWAYS);
        
        contentSplit.getChildren().addAll(trainerPane, scroll);
        mainLayout.setCenter(contentSplit);
        
        this.getChildren().addAll(bg, mainLayout, settingsCog);
    }
    
    /**
     * Creates the trainer information panel.
     *
     * Displays:
     *   - Trainer sprite (front/back toggle)
     *   - Name and coin count
     *   - Passive and active role descriptions
     *
     * @return trainer info panel
     */
    private VBox createTrainerPane() {
    	Trainer player = controller.getPlayerTrainer();
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(15));
        box.setPrefWidth(350);
        box.setMinWidth(350);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-background-radius: 15; -fx-border-color: #FFFFFF; -fx-border-width: 3; -fx-border-radius: 15;");
        VBox.setVgrow(box, Priority.ALWAYS);
        
        Label lbl = new Label("YOUR TRAINER");
        lbl.setTextFill(Color.GRAY);
        lbl.setFont(Font.font("Impact", 30));
        
        StackPane spriteContainer = new StackPane();
        ImageView avatar = new ImageView();
        avatar.setFitHeight(170); 
        avatar.setPreserveRatio(true);
        
        updateImage(avatar, player.getName(), true);
        
        final boolean[] isFront = {true};

        Button turnBtn = new Button("↻");
        turnBtn.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 20; -fx-cursor: hand;");
        turnBtn.setOnAction(e -> {
        	isFront[0] = !isFront[0];
            updateImage(avatar, player.getName(), isFront[0]);
        });
        
        spriteContainer.getChildren().addAll(avatar, turnBtn);
        
        StackPane.setAlignment(turnBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(turnBtn, new Insets(10, 70, 0, 0));
        
        Label nameLbl = new Label(player.getName().toUpperCase());
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label coinsLbl = new Label("💰 Coins: " + controller.getPlayerCoins());
        coinsLbl.setTextFill(Color.GOLD);
        coinsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TrainerRole role = player.getRole();
        
        VBox abilitiesBox = new VBox(6);
        abilitiesBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10; -fx-background-radius: 10;");
        abilitiesBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(abilitiesBox, Priority.ALWAYS);
        
        Label passTitle = new Label("PASSIVE:");
        passTitle.setTextFill(Color.ORANGE);
        passTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label passDesc = new Label(role.getPassiveDescription());
        passDesc.setTextFill(Color.LIGHTGRAY);
        passDesc.setFont(Font.font("Arial", 10));
        passDesc.setWrapText(true);
        passDesc.setPrefWidth(290);
        
        Label actTitle = new Label("ACTIVE:");
        actTitle.setTextFill(Color.LIGHTGREEN);
        actTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label actDesc = new Label(role.getActiveDescription());
        actDesc.setTextFill(Color.LIGHTGRAY);
        actDesc.setFont(Font.font("Arial", 10));
        actDesc.setWrapText(true);
        actDesc.setPrefWidth(290);
        
        abilitiesBox.getChildren().addAll(passTitle, passDesc, actTitle, actDesc);
        
        box.getChildren().addAll(lbl, spriteContainer, nameLbl, coinsLbl, abilitiesBox);
        return box;
    }
    
    /**
     * Creates a Goober card.
     *
     * Displays:
     *   - Trainer sprite (front/back toggle)
     *   - Name and coin count
     *   - Passive and active role descriptions
     *
     * @return trainer info panel
     */
    private HBox createGooberCard(Goober g) {
    	HBox card = new HBox(15);
        card.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 10; -fx-padding: 15; -fx-border-color: #3498db; -fx-border-width: 2px; -fx-border-radius: 10;");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(420); 
        card.setMinHeight(420);
        
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPrefWidth(180);
        infoBox.setMinWidth(180);
        
        StackPane spriteContainer = new StackPane();
        ImageView img = new ImageView();
        img.setFitHeight(120);
        img.setPreserveRatio(true);
        
        updateImage(img, g.getName(), true);
        
        final boolean[] isFront = {true};
        
        Button turnBtn = new Button("↻");
        turnBtn.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 20; -fx-cursor: hand;");
        turnBtn.setOnAction(e -> {
            isFront[0] = !isFront[0];
            updateImage(img, g.getName(), isFront[0]);
        });
        
        spriteContainer.getChildren().addAll(img, turnBtn);
        StackPane.setAlignment(turnBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(turnBtn, new Insets(0, 10, 0, 0));
        
        Label nameLbl = new Label(g.getName());
        nameLbl.setStyle("-fx-text-fill: white;");
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Label classLbl = new Label(g.getType().toString());
        classLbl.setStyle("-fx-text-fill: cyan;");
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Label lvlLbl = new Label("Lvl. " + g.getLevel());
        lvlLbl.setStyle("-fx-text-fill: gold;");
        lvlLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        FlowPane statsBox = new FlowPane();
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setHgap(8);
        statsBox.setVgap(2);
        statsBox.setPadding(new Insets(10, 5, 0, 5));
        
        statsBox.getChildren().add(statLine("HP", g.getMaxHp(), "lightgreen"));
        statsBox.getChildren().add(statLine("ATK", g.getAttack(), "#ff6b6b"));
        statsBox.getChildren().add(statLine("DEF", String.format("%.0f%%", g.getDefence() * 100), "cyan"));
        statsBox.getChildren().add(statLine("CRIT", String.format("%.0f%%", g.getCritChance() * 100), "gold"));
        statsBox.getChildren().add(statLine("SPD", g.getSpeed(), "orange"));
        
        infoBox.getChildren().addAll(spriteContainer, nameLbl, classLbl, lvlLbl, statsBox);
        
        VBox abilityBox = new VBox(5);
        HBox.setHgrow(abilityBox, Priority.ALWAYS);
        abilityBox.setPadding(new Insets(0, 0, 0, 10));
        
        Label abilHeader = new Label("ABILITIES");
        abilHeader.setTextFill(Color.LIGHTGRAY);
        abilHeader.setFont(Font.font("Impact", 18));
        abilityBox.getChildren().add(abilHeader);
        
        for (GooberMove move : MoveFactory.getMoves(g.getName())) {
            abilityBox.getChildren().add(createAbilityRow(g, move));
        }
        
        card.getChildren().addAll(infoBox, abilityBox);
        return card;
    }
    
    /**
     * Creates a visual row describing a single Goober ability.
     *
     * Displays:
     *   - Ability name and power
     *   - Unlock status
     *   - Effect description
     *
     * @param g owning Goober
     * @param move ability being rendered
     * @return formatted ability row
     */
    private VBox createAbilityRow(Goober g, GooberMove move) {
    	int currentLevel = g.getLevel();
        boolean unlocked = currentLevel >= move.getUnlockLevel();
        
        String textColor = unlocked ? "white" : "gray";
        String descColor = unlocked ? "lightgray" : "#555555"; 
        String bgStyle = unlocked ? 
            "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 5;" :
            "-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5; -fx-background-radius: 5; -fx-opacity: 0.6;";
        
        VBox row = new VBox(2);
        row.setPadding(new Insets(5, 8, 5, 8));
        row.setStyle(bgStyle);
        VBox.setVgrow(row, Priority.ALWAYS);
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label levelLbl = new Label(unlocked ? "" : "🔒");
        levelLbl.setStyle("-fx-text-fill: " + (unlocked ? "lightgreen" : "red") + ";");
        levelLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label name = new Label(move.getName());
        name.setStyle("-fx-text-fill: " + textColor + ";");
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label power = new Label();
        power.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        if (unlocked) {
            StringBuilder powerText = new StringBuilder();
            String colorStyle = "-fx-text-fill: white;";

            if (move.getDamage() > 0) {
                int rawDamage = move.getDamage() + g.getAttack();
                powerText.append("Dmg: ").append(rawDamage);
                colorStyle = "-fx-text-fill: #ff6b6b;";
            }

            Effect e = move.getEffect();
            if (e != null) {
                if (powerText.length() > 0) powerText.append(" + ");

                switch(e.getType()) {
                    case HEAL:
                        powerText.append("Heal: ").append((int)(e.getStrength() * 100)).append("%");
                        if (powerText.toString().startsWith("Heal")) colorStyle = "-fx-text-fill: lightgreen;";
                        break;
                    case STUN:
                        powerText.append("Stun (").append(e.getDuration()).append("T)");
                        if (move.getDamage() == 0) colorStyle = "-fx-text-fill: yellow;";
                        break;
                    case POISON:
                        powerText.append("Poison");
                        if (move.getDamage() == 0) colorStyle = "-fx-text-fill: violet;";
                        break;
                    case DIZZY:
                        powerText.append("Dizzy");
                        if (move.getDamage() == 0) colorStyle = "-fx-text-fill: pink;";
                        break;
                    case DEFENCE_MODIFICATION:
                    case DAMAGE_MODIFICATION:
                    case CRIT_MODIFICATION:
                        String sign = e.getStrength() > 0 ? "+" : "";
                        String stat = "";
                        if (e.getType() == EffectType.DEFENCE_MODIFICATION) stat = "Def";
                        else if (e.getType() == EffectType.DAMAGE_MODIFICATION) stat = "Atk";
                        else stat = "Crit";
                        
                        powerText.append(stat).append(" ").append(sign).append((int)(e.getStrength() * 100)).append("%");
                        if (move.getDamage() == 0) colorStyle = "-fx-text-fill: cyan;";
                        break;
                    default:
                        powerText.append("Effect");
                }
            }
            
            if (powerText.length() == 0) {
                powerText.append("-");
                colorStyle = "-fx-text-fill: gray;";
            }

            power.setText(powerText.toString());
            power.setStyle(colorStyle);
            
        } else {
            power.setText("Unlocks Lvl: " + move.getUnlockLevel());
            power.setStyle("-fx-text-fill: #ff4444;"); 
        }
        
        header.getChildren().addAll(levelLbl, name, spacer, power);
        
        Label desc = new Label(MoveDescriptions.getDescription(move.getName()));
        desc.setStyle("-fx-text-fill: " + descColor + ";");
        desc.setFont(Font.font("Arial", 10));
        desc.setWrapText(true);
        desc.setPrefWidth(300);
        
        row.getChildren().addAll(header, desc);
        return row;
    }
    
    /**
     * Updates a sprite image depending on orientation.
     *
     * @param view target image view
     * @param name sprite owner name
     * @param isFront true for front sprite, false for back
     */
    private void updateImage(ImageView view, String name, boolean isFront) {
        ImageView temp = ViewStyles.loadSprite(name, isFront);
        if (temp != null && temp.getImage() != null) {
            view.setImage(temp.getImage());
        }
    }
    
    /**
     * Creates a compact stat label pair.
     *
     * @param label stat name
     * @param val stat value
     * @param colorStyle CSS color styling
     * @return stat line container
     */
    private HBox statLine(String label, Object val, String colorStyle) {
    	HBox line = new HBox(3);
        line.setAlignment(Pos.CENTER);
        
        Label l = new Label(label + ":");
        l.setStyle("-fx-text-fill: " + colorStyle + ";");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label v = new Label(val.toString());
        v.setStyle("-fx-text-fill: white;");
        v.setFont(Font.font("Arial", 12));
        
        line.getChildren().addAll(l, v);
        return line;
    }

}
