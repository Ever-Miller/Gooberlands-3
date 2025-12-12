package view;

import java.net.URL;

import controller.ShopController;
import game.GameManager;
import game.MapState;
import items.ItemDescriptions;
import items.ItemFactory;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * File: ShopPage.java
 *
 * Purpose:
 *     Displays the in-game item shop screen.
 *
 *     This screen allows the player to:
 *       - Browse all purchasable items
 *       - View item descriptions and costs
 *       - Purchase items using coins
 *       - Return to the world map
 *
 *     All purchasing logic is delegated to {@link ShopController}.
 */
public class ShopPage extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

    /** Controller handling item purchases and coin deduction. */
    private ShopController controller;

    /** UI label displaying the player's current coin count. */
    private Label coinLabel;

    /**
     * Constructs the shop view.
     *
     * @param stage active JavaFX stage
     * @param controller shop controller handling purchases
     */
    public ShopPage(Stage stage, ShopController controller) {
        this.stage = stage;
        this.controller = controller;
        execute();
    }
    
    /**
     * Builds and renders the shop UI.
     *
     * This includes:
     *   - Item grid
     *   - Coin display
     *   - Back navigation
     *   - Settings access
     */
    private void execute() {
    	Rectangle bg = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);
    	
    	BorderPane mainLayout = new BorderPane();
    	mainLayout.setPadding(new Insets(20, 40, 20, 40));
        
    	StackPane header = new StackPane();
    	
    	Label title = new Label("ITEM SHOP");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Impact", 50));
        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        
        HBox coinBox = new HBox(10);
        coinBox.setAlignment(Pos.CENTER);
        Label coinIcon = new Label("💰");
        coinIcon.setFont(Font.font(30));
        
        coinLabel = new Label("Coins: " + controller.getPlayerCoins());
        coinLabel.setTextFill(Color.GOLD);
        coinLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        coinBox.getChildren().addAll(coinIcon, coinLabel);
        
        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            stage.getScene().setRoot(new MainPage(stage, true));
        });
        
        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));
        
        header.getChildren().addAll(title, coinBox);
        mainLayout.setTop(header);
        
        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(30);
        itemGrid.setVgap(20);
        itemGrid.setAlignment(Pos.CENTER);
        itemGrid.setPadding(new Insets(20, 0, 20, 0));
        
        int col = 0;
        int row = 0;
        
        for (String itemName : ItemFactory.getAllItemNames()) {
            itemGrid.add(createItemCard(itemName), col, row);
            
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        mainLayout.setCenter(itemGrid);
        
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 10, 0));
        
        Button backBtn = ViewStyles.createStyledButton("BACK TO MAP");
        backBtn.setPrefWidth(300);
        backBtn.setPrefHeight(60);
        backBtn.setOnAction(e -> {
            GameManager.getInstance().setState(new MapState(GameManager.getInstance()));
        });
        
        footer.getChildren().add(backBtn);
        mainLayout.setBottom(footer);
        
        this.getChildren().addAll(bg, mainLayout, settingsCog);
    }
    
    /**
     * Creates a visual card representing a single shop item.
     *
     * Each card displays:
     *   - Item icon
     *   - Name
     *   - Description
     *   - Cost
     *   - Purchase button
     *
     * @param itemName item identifier
     * @return populated item card
     */
    private VBox createItemCard(String itemName) {
    	VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(330);
        card.setMinHeight(220);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(15));
        
        card.setStyle(
                "-fx-background-color: rgba(0,0,0,0.6);" + 
                "-fx-background-radius: 15;" + 
                "-fx-border-color: white;" + 
                "-fx-border-width: 2px;" + 
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);"
            );
        
        ImageView imgView = new ImageView();
        try {
            String path = ItemFactory.getItemImagePath(itemName);
            if (path != null) {
                URL url = getClass().getResource(path);
                if (url != null) {
                    imgView.setImage(new Image(url.toExternalForm()));
                    imgView.setFitHeight(80);
                    imgView.setPreserveRatio(true);
                }
            }
        } catch (Exception e) {}

        if (imgView.getImage() != null) card.getChildren().add(imgView);
        
        Label nameLbl = new Label(itemName);
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLbl.setWrapText(true);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        
        Label descLbl = new Label(ItemDescriptions.getDescription(itemName));
        descLbl.setTextFill(Color.LIGHTGRAY);
        descLbl.setFont(Font.font("Arial", 11));
        descLbl.setWrapText(true);
        descLbl.setTextAlignment(TextAlignment.CENTER);
        descLbl.setPrefHeight(40);
        
        int cost = ItemFactory.getItemCost(itemName);
        Label costLbl = new Label(cost + " Coins");
        costLbl.setTextFill(Color.YELLOW);
        costLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Button buyBtn = new Button("BUY");
        buyBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        buyBtn.setPrefWidth(100);
        buyBtn.setOnAction(e -> handlePurchase(itemName));
        
        card.getChildren().addAll(nameLbl, descLbl, costLbl, buyBtn);
        return card;
    }
    
    /**
     * Handles an item purchase attempt.
     *
     * Updates the coin display on success and
     * briefly flashes red on failure.
     *
     * @param itemName item to purchase
     */
    private void handlePurchase(String itemName) {
        boolean success = controller.attemptPurchase(itemName);
        
        if (success) {
            coinLabel.setText("Coins: " + controller.getPlayerCoins());
            coinLabel.setTextFill(Color.GOLD);
        } else {
            coinLabel.setTextFill(Color.RED);
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> coinLabel.setTextFill(Color.GOLD)); 
            pause.play();
        }
    }
}
