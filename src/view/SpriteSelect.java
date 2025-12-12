package view;
import java.util.List;

import action.Action;
import action.SwitchAction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Goober;
import models.Trainer;

/**
 * File: SpriteSelect.java
 *
 * Purpose:
 *     Displays the in-battle Goober switch screen.
 *
 *     This screen allows the player to:
 *       - View the currently active Goober
 *       - Inspect HP and XP values
 *       - Select a bench Goober to switch in
 *       - Return to the battle screen
 *
 *     All switching logic is delegated to {@link BattlePage}.
 */
public class SpriteSelect extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/** Battle screen to return to after selection. */
	private BattlePage returnTo;

	/** Player trainer owning the team. */
	private Trainer player;
	
	/**
	 * Constructs the sprite selection screen.
	 *
	 * @param stage active JavaFX stage
	 */
	public SpriteSelect(Stage stage){
		this(stage, null);
	}
	
	/**
	 * Constructs the sprite selection screen for an active battle.
	 *
	 * @param stage active JavaFX stage
	 * @param battlePage battle screen to return to
	 */
	public SpriteSelect(Stage stage, BattlePage battlePage) {
		this.stage = stage;
		this.returnTo = battlePage;
		
		if (battlePage != null) {
			this.player = battlePage.getPlayerTrainer();
		}
		execute();
	}
	
	/**
	 * Builds and renders the Goober selection UI.
	 *
	 * This includes:
	 *   - Active Goober display
	 *   - Bench Goober list
	 *   - Switch interaction
	 *   - Back navigation
	 */
	public void execute() {
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
		
		HBox gooberLayout = new HBox(20);
		gooberLayout.setAlignment(Pos.CENTER);
		gooberLayout.setPadding(new Insets(5));

        Label title = new Label("Your Team");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 48));
        
        VBox activeSection = createActiveGooberCard();
        
        // Bench section
        VBox benchSection = createBenchRow();
        
        Button backBtn = ViewStyles.createStyledButton("Back to Battle");
        backBtn.setOnAction(event -> {
            if (returnTo != null) {
                returnTo.updateStats();
            }
            stage.getScene().setRoot(returnTo);
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(this);
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));

        gooberLayout.getChildren().addAll(activeSection, benchSection);
        mainLayout.getChildren().addAll(title, gooberLayout, backBtn);
        
        this.getChildren().addAll(background, mainLayout, settingsCog);
	}
	
	/**
	 * Creates a card displaying the currently active Goober.
	 *
	 * @return active Goober display card
	 */
	private VBox createActiveGooberCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 20; -fx-border-color: white; -fx-border-radius: 20; -fx-border-width: 2;");
        card.setMaxWidth(600);
        card.setMaxHeight(400);
        card.setPadding(new Insets(20));

        Goober active = (player != null) ? player.getActiveGoober() : null;
        if (active == null) return card;

        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER);
        
        Label nameLbl = new Label(active.getName());
        nameLbl.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 32));
        nameLbl.setTextFill(Color.YELLOW);
        
        Label lvlLbl = new Label("Lv." + active.getLevel());
        lvlLbl.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));
        lvlLbl.setTextFill(Color.WHITE);
        
        infoRow.getChildren().addAll(nameLbl, lvlLbl);

        ImageView imgView = ViewStyles.loadSprite(active.getName(), true);
        if (imgView == null) imgView = new ImageView();
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);

        VBox hpBox = createHpBar(active);
        VBox xpBox = createXpBar(active);

        card.getChildren().addAll(infoRow, imgView, hpBox, xpBox);
        return card;
    }
	
	/**
	 * Creates the list of bench Goobers available for switching.
	 *
	 * @return bench Goober selection column
	 */
	private VBox createBenchRow() {
		VBox row = new VBox(30);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(20));

        if (player != null && player.getTeam() != null) {
            List<Goober> team = player.getTeam();
            Goober currentActive = player.getActiveGoober();
            int activeIndex = team.indexOf(currentActive);

            for (int i = 0; i < team.size(); i++) {
                if (i == activeIndex) continue;

                Goober g = team.get(i);
                final int index = i;
                boolean isFainted = g.getCurrentHp() <= 0;

                VBox gooberCard = new VBox(5);
                gooberCard.setAlignment(Pos.CENTER);
                gooberCard.setPadding(new Insets(10));
                
                ImageView gImg = ViewStyles.loadSprite(g.getName(), true);
                if (gImg == null) gImg = new ImageView();
                gImg.setFitHeight(100);
                gImg.setPreserveRatio(true);
                if (isFainted) gImg.setOpacity(0.5);

                Label gName = new Label(g.getName() + (isFainted ? " (X)" : ""));
                gName.setTextFill(isFainted ? Color.GRAY : Color.WHITE);
                gName.setFont(Font.font("Comic Sans MS", 14));

                Label gHp = new Label(g.getCurrentHp() + "/" + g.getMaxHp() + " HP");
                gHp.setTextFill(g.getCurrentHp() > 0 ? Color.LIGHTGREEN : Color.RED);
                gHp.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                
                Label gXp = new Label(g.getXpManager().getCurrentXp() + "/" + g.getXpManager().getMaxXp() + " XP");
                gXp.setTextFill(Color.BLUE);
                gXp.setFont(Font.font("Arial", FontWeight.BOLD, 12));

                if (!isFainted) {
	                gooberCard.setOnMouseClicked(e -> {
	                    if (returnTo != null && player != null) {
	                        Action switchAction = new SwitchAction(player, index);
	                        returnTo.submitPlayerAction(switchAction);
	                        returnTo.updateStats();
	                        stage.getScene().setRoot(returnTo);
	                    }
	                });
                }

                gooberCard.getChildren().addAll(gImg, gName, gHp, gXp);
                row.getChildren().add(gooberCard);
            }
        }
        return row;
    }
	
	/**
	 * Creates a visual HP bar for a Goober.
	 *
	 * @param g target Goober
	 * @return HP bar container
	 */
	private VBox createHpBar(Goober g) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        double pct = (double) g.getCurrentHp() / g.getMaxHp();
        
        StackPane barStack = new StackPane();
        Rectangle bg = new Rectangle(300, 20, Color.rgb(50, 50, 50));
        Rectangle fill = new Rectangle(300 * pct, 20,
                pct > 0.5 ? Color.GREEN : pct > 0.2 ? Color.ORANGE : Color.RED);
        
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        barStack.getChildren().addAll(bg, fill);

        Label hpText = new Label(g.getCurrentHp() + "/" + g.getMaxHp());
        hpText.setTextFill(Color.WHITE);
        hpText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        box.getChildren().addAll(barStack, hpText);
        return box;
    }
	
	/**
	 * Creates a visual XP bar for a Goober.
	 *
	 * @param g target Goober
	 * @return XP bar container
	 */
	private VBox createXpBar(Goober g) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        double pct = (double) g.getXpManager().getCurrentXp() / g.getXpManager().getMaxXp();
        
        StackPane barStack = new StackPane();
        Rectangle bg = new Rectangle(300, 15, Color.rgb(50, 50, 50));
        Rectangle fill = new Rectangle(300 * pct, 15, Color.BLUE);
        
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        barStack.getChildren().addAll(bg, fill);

        Label xpText = new Label(g.getXpManager().getCurrentXp() + "/" + g.getXpManager().getMaxXp());
        xpText.setTextFill(Color.WHITE);
        xpText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        box.getChildren().addAll(barStack, xpText);
        return box;
    }
}
