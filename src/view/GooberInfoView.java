package view;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import models.Goober;
import models.GooberMove;
import models.MoveDescriptions;
import models.MoveFactory;

/**
 * File: GooberInfoView.java
 *
 * Purpose:
 *     Displays detailed information about a Goober.
 *
 *     This view presents:
 *       - Goober name and type
 *       - Front and back sprites
 *       - Core combat statistics
 *       - Move list with descriptions
 *
 *     Intended for use as an informational overlay or
 *     detail panel within the battle UI.
 */
public class GooberInfoView extends VBox {
    
    /**
     * Constructs a Goober information panel.
     *
     * @param goober the Goober whose details are displayed
     */
    public GooberInfoView(Goober goober) {
    	this.setPadding(new Insets(20));
        this.setSpacing(15);
        
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-background-radius: 10; -fx-border-color: #3498db; -fx-border-width: 2px; -fx-border-radius: 10;");
        this.setMaxWidth(500);
        this.setAlignment(Pos.TOP_CENTER);

        Label nameLbl = new Label(goober.getName());
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Label typeLbl = new Label(goober.getType().toString());
        typeLbl.setTextFill(Color.CYAN);
        typeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        HBox spriteBox = new HBox(30);
        spriteBox.setAlignment(Pos.CENTER);
        
        ImageView frontView = ViewStyles.loadSprite(goober.getName(), true);
        ImageView backView = ViewStyles.loadSprite(goober.getName(), false);
        
        if (frontView != null) spriteBox.getChildren().add(frontView);
        if (backView != null) spriteBox.getChildren().add(backView);
        
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getChildren().addAll(
            createStatLbl("HP", goober.getMaxHp(), Color.LIGHTGREEN),
            createStatLbl("ATK", goober.getAttack(), Color.RED),
            createStatLbl("DEF", (int)(goober.getDefence()*100) + "%", Color.CYAN),
            createStatLbl("SPD", goober.getSpeed(), Color.ORANGE),
            createStatLbl("CRIT", (int)(goober.getCritChance()*100) + "%", Color.YELLOW)
        );
        
        VBox movesContainer = new VBox(10);
        movesContainer.setAlignment(Pos.CENTER_LEFT);

        Label movesHeader = new Label("ABILITIES:");
        movesHeader.setTextFill(Color.GOLD);
        movesHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        movesContainer.getChildren().add(movesHeader);
        
        ArrayList<GooberMove> allMoves = MoveFactory.getMoves(goober.getName());

        for (GooberMove move : allMoves) {
            VBox moveBox = new VBox(2);
            Label mName = new Label("• " + move.getName());
            mName.setTextFill(Color.WHITE);
            mName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            Label mDesc = new Label(MoveDescriptions.getDescription(move.getName()));
            mDesc.setTextFill(Color.LIGHTGRAY);
            mDesc.setWrapText(true);
            mDesc.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
            
            moveBox.getChildren().addAll(mName, mDesc);
            movesContainer.getChildren().add(moveBox);
        }
        
        this.getChildren().addAll(nameLbl, typeLbl, spriteBox, statsBox, movesContainer);
    }
    
    /**
     * Creates a formatted label for displaying a single Goober stat.
     *
     * @param name stat name
     * @param val stat value
     * @param c text color
     * @return styled stat label
     */
    private Label createStatLbl(String name, Object val, Color c) {
    	Label l = new Label(name + ": " + val);
        l.setTextFill(c);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return l;
    }
}
