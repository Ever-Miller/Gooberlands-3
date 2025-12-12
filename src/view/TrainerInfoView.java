package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.TrainerRole;

/**
 * File: TrainerInfoView.java
 *
 * Purpose:
 *     Displays detailed information about a Trainer role.
 *
 * Responsibilities:
 *     - Show trainer role name
 *     - Display front and back sprites for the role
 *     - Present passive and active ability descriptions
 *
 * Design Notes:
 *     This class is a pure VIEW component.
 *     It does not modify game state and only renders data
 *     provided by {@link TrainerRole}.
 */
public class TrainerInfoView extends VBox {
	
	/**
	 * Constructs a UI panel displaying information for a specific trainer role.
	 *
	 * @param role the trainer role whose information is displayed
	 */
	public TrainerInfoView(TrainerRole role) {
		this.setPadding(new Insets(25));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95); -fx-background-radius: 15; -fx-border-color: white; -fx-border-width: 3px; -fx-border-radius: 15;");
        this.setMaxWidth(450);
        this.setAlignment(Pos.TOP_CENTER);
        
        Label nameLbl = new Label(role.getName());
        nameLbl.setTextFill(Color.GOLD);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        
        HBox spriteBox = new HBox(30);
        spriteBox.setAlignment(Pos.CENTER);
        
        ImageView frontView = ViewStyles.loadSprite(role.getName(), true);
        ImageView backView = ViewStyles.loadSprite(role.getName(), false);
        
        if (frontView != null) spriteBox.getChildren().add(frontView);
        if (backView != null) spriteBox.getChildren().add(backView);
        
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label passiveHeader = new Label("PASSIVE ABILITY");
        passiveHeader.setTextFill(Color.ORANGE);
        passiveHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label passiveDesc = new Label(role.getPassiveDescription());
        passiveDesc.setTextFill(Color.WHITE);
        passiveDesc.setFont(Font.font("Arial", 14));
        passiveDesc.setWrapText(true);
        
        Label activeHeader = new Label("ACTIVE ABILITY");
        activeHeader.setTextFill(Color.CYAN);
        activeHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label activeDesc = new Label(role.getActiveDescription());
        activeDesc.setTextFill(Color.WHITE);
        activeDesc.setFont(Font.font("Arial", 14));
        activeDesc.setWrapText(true);
        
        content.getChildren().addAll(passiveHeader, passiveDesc, activeHeader, activeDesc);
        this.getChildren().addAll(nameLbl, spriteBox, content);
	}
}
