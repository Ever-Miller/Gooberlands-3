/**
 * File: Bag.java
 *
 * Purpose:
 *     Represents the in-battle inventory (Bag) UI screen.
 *
 *     This screen allows the player to:
 *       - View their current inventory
 *       - Inspect item icons and descriptions
 *       - Use items during battle
 *       - Return smoothly back to the BattlePage
 *
 *     The Bag is rendered as an overlay-style StackPane
 *     and is intended to be swapped directly into the
 *     current Scene root.
 */

package view;
import java.util.List;

import items.Item;
import items.ItemFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Trainer;

/**
 * Displays the player's inventory during battle.
 *
 * The Bag allows items to be selected and used mid-fight,
 * then returns control back to the BattlePage.
 */
public class Bag extends StackPane {

	/** JavaFX stage used for scene manipulation. */
	private final Stage stage;

	/** Reference to the BattlePage to return to after use. */
	private final BattlePage returnTo;

	/** The player Trainer owning the inventory. */
	private final Trainer player;

	/** Read-only view of the player's inventory. */
	private final List<Item> inventoryView;
	
	/**
	 * Constructs the Bag UI.
	 *
	 * @param stage active JavaFX stage
	 * @param battlePage battle screen to return to
	 */
	public Bag(Stage stage, BattlePage battlePage){
		this.stage = stage;
		this.returnTo = battlePage;
		
		if (battlePage != null && battlePage.getPlayerTrainer() != null) {
			this.player = battlePage.getPlayerTrainer();
			this.inventoryView = player.getInventory(); // unmodifiable view 
		} else {
			this.player = null;
			this.inventoryView = List.of();
		}
		
		execute();
	}
	
	/**
	 * Builds and renders the Bag UI elements.
	 *
	 * This includes:
	 *   - Inventory title
	 *   - Scrollable item list
	 *   - Back button
	 */
	public void execute() {
		Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
		
		VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setMaxWidth(900);
        
        Label title = new Label("Inventory");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 48));
        title.setStyle("-fx-effect: dropshadow(three-pass-box, black, 5, 0, 0, 0);");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(400);

        VBox itemList = new VBox(10);
        itemList.setPadding(new Insets(10));
        itemList.setStyle("-fx-background-color: transparent;");
        
        if (inventoryView.isEmpty()) {
            Label empty = new Label("Your bag is empty.");
            empty.setTextFill(Color.LIGHTGRAY);
            empty.setFont(Font.font("Comic Sans MS", 24));
            itemList.getChildren().add(empty);
            itemList.setAlignment(Pos.CENTER);
        } else {
            for (Item item : inventoryView) {
                itemList.getChildren().add(createItemRow(item));
            }
        }
        
        scrollPane.setContent(itemList);
        
        Button backBtn = ViewStyles.createStyledButton("Back to Battle");
        backBtn.setOnAction(event -> {
            if (returnTo != null) {
                returnTo.updateStats();
                stage.getScene().setRoot(returnTo);
            }
        });
        
        container.getChildren().addAll(title, scrollPane, backBtn);
        this.getChildren().addAll(background, container);
	}
	
	/**
	 * Creates a single inventory row for an item.
	 *
	 * Each row displays:
	 *   - Icon
	 *   - Name
	 *   - Description
	 *   - "Use" button
	 *
	 * @param item inventory item
	 * @return populated HBox representing the item entry
	 */
	private HBox createItemRow(Item item) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 10;");

        // Icon
        ImageView iconView = new ImageView();
        String imgPath = ItemFactory.getItemImagePath(item.getName());
        if (imgPath != null) {
            try {
                iconView = new ImageView(new Image(getClass().getResource(imgPath).toExternalForm()));
            } catch (Exception e) { /* ignore */ }
        }
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);

        // Text Info
        VBox info = new VBox(5);
        Label name = new Label(item.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));

        Label desc = new Label(describeItem(item));
        desc.setTextFill(Color.LIGHTGRAY);
        desc.setFont(Font.font("Arial", 14));
        desc.setWrapText(true);

        info.getChildren().addAll(name, desc);
        HBox.setHgrow(info, Priority.ALWAYS); // Push button to the right

        // Custom "Use" Button
        Button useBtn = new Button("USE");
        useBtn.setPrefSize(100, 40);
        String baseStyle = "-fx-background-color: #FF5500; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: white; -fx-border-width: 2; -fx-font-family: 'Comic Sans MS'; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #FF7700; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: white; -fx-border-width: 2; -fx-font-family: 'Comic Sans MS'; -fx-cursor: hand;";
        
        useBtn.setStyle(baseStyle);
        useBtn.setOnMouseEntered(e -> useBtn.setStyle(hoverStyle));
        useBtn.setOnMouseExited(e -> useBtn.setStyle(baseStyle));

        useBtn.setOnAction(e -> {
            if (returnTo != null && player != null) {
                returnTo.useItemFromBag(item);
                stage.getScene().setRoot(returnTo);
            }
        });
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(this);
        });
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));

        row.getChildren().addAll(iconView, info, useBtn);
        this.getChildren().add(settingsCog);
        return row;
    }
	
	/**
	 * Generates a short, player-facing description of an item
	 * based on its functional type.
	 *
	 * @param item inventory item
	 * @return formatted description string
	 */
	private String describeItem(Item item) {
		return switch (item.getType()) {
			case HEAL -> "Heals " + (int) (item.getMagnitude() * 100) + "% HP "+ (item.isTargetSelf() ? "to your active Goober." : "to the enemy (??).");
			case DAMAGE -> "Deals " + (int) (item.getMagnitude() * 100)+ "% of max HP as damage "+ (item.isTargetSelf() ? "to yourself (??)" : "to the enemy.");
			case STUN -> "Attempts to stun "+ (item.isTargetSelf() ? "your own Goober." : "the enemy Goober.");
			default -> "";};
	}
}
