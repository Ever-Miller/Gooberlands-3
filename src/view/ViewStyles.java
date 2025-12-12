package view;

import java.net.URL;

import ai.Difficulty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * File: ViewStyles.java
 *
 * Purpose:
 *     Centralized UI styling and asset-loading helpers for the view layer.
 *
 *     This class provides:
 *       - Consistent button, label, and text field styles
 *       - Settings button creation logic
 *       - Sprite loading utilities for trainers, Goobers, and difficulty icons
 *
 *     This class contains only static helpers and holds no state.
 */
public class ViewStyles {
	
	public static final Color BACKGROUND_COLOR = Color.web("#2c3e50");
	
	/** Creates a styled primary button. 
	 *
	 * @param text button display text
	 * @return styled button
	 */
	public static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(380);
        btn.setPrefHeight(70);

        String style = 
            "-fx-background-color: #FF5500;" +
            "-fx-border-color: #FFFFFF;" +
            "-fx-border-width: 4px;" +         
            "-fx-background-radius: 30;" +     
            "-fx-border-radius: 30;" +
            "-fx-font-family: 'Comic Sans MS', Impact;" + 
            "-fx-font-weight: bold;" +
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #FFFFFF;" +        
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 3);";

        String hoverStyle = 
            "-fx-background-color: #FF7700;" +
            "-fx-border-color: #FFFFFF;" +     
            "-fx-border-width: 4px;" +
            "-fx-background-radius: 30;" +
            "-fx-border-radius: 30;" +
            "-fx-font-family: 'Comic Sans MS', Impact;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 22px;" +           
            "-fx-text-fill: #FFFFFF;" +        
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 8, 0, 0, 5);";

        btn.setStyle(style);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(style));
        
        return btn;
    }
	
	/**
     * Creates a styled label.
     *
     * @param text label text
     * @return styled Label
     */
	public static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setPrefWidth(380);
        label.setPrefHeight(70);

        String style = 
            "-fx-font-family: 'Comic Sans MS', Impact;" + 
            "-fx-font-weight: bold;" +
            "-fx-font-size: 22px;" +
            "-fx-text-fill: #FFFFFF;";

        label.setStyle(style);
        
        return label;
    }
	
	/**
     * Creates a styled text field.
     *
     * @param text initial value
     * @return styled TextField
     */
	public static TextField createStyledTextField(String text) {
		TextField textField = new TextField(text);
		textField.setPrefWidth(380);
		textField.setPrefHeight(70);

        String style = 
            "-fx-font-family: 'Comic Sans MS', Impact;" + 
            "-fx-font-weight: bold;" +
            "-fx-font-size: 22px;";

        textField.setStyle(style);
        
        return textField;
    }
	
	/**
     * Creates the settings button and wires return logic.
     *
     * @param stage active stage
     * @param returnLogic logic executed on exit
     * @return settings Button
     */
	public static Button createSettingsButton(javafx.stage.Stage stage, Runnable returnLogic) {
        Button btn = new Button("⚙");
        
        btn.setStyle(
            "-fx-background-color: rgba(0,0,0,0.5);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 50px;" +
            "-fx-min-height: 50px;" +
            "-fx-max-width: 50px;" +
            "-fx-max-height: 50px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 50%;"
        );
        
        // Hover Effect
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: rgba(255, 85, 0, 0.8);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 50px;" +
            "-fx-min-height: 50px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 50%;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: rgba(0,0,0,0.5);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 50px;" +
            "-fx-min-height: 50px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: white;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 50%;"
        ));

        btn.setOnAction(e -> {
            SettingsView settings = new SettingsView(stage, returnLogic);
            stage.getScene().setRoot(settings);
        });
        
        return btn;
    }
	
	/** Loads a sprite image.
	 *
	 * @param name entity name
	 * @param isFront true for front sprite
	 * @return sprite ImageView or null
	 */
	public static ImageView loadSprite(String name, boolean isFront) {
		String path = getSpritePath(name, isFront);
		
		try {
			URL url = ViewStyles.class.getResource(path);
			if (url == null) {
            	System.out.println("Image not found: " + path);
            	return null;
            }
			Image img = new Image(url.toExternalForm());
            ImageView iv = new ImageView(img);
            iv.setFitHeight(120);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            System.out.println("Missing sprite: " + name + " (" + (isFront?"Front":"Back") + ")");
            return null;
        }
	}
	
	/**
     * Resolves sprite file path based on entity name.
     *
     * @param name entity name
     * @param isFront true for front sprite
     * @return resource path
     */
	public static String getSpritePath(String name, boolean isFront) {
		String folder = "";
		String fileName;
		
		switch (name) {
        case "Tralalero Tralala":   
        	fileName = isFront ? "tralalero_front" : "tralalero_back"; 
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "DaBaby":              
        	fileName = isFront ? "Dababy_front" : "Dababy_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Peter Griffin":       
        	fileName = isFront ? "petergriffin_front" : "petergriffin_back"; 
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "John Pork":           
        	fileName = isFront ? "JohnPork_front" : "johnpork_back"; 
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Tung Tung Tung Sahur":
        	fileName = isFront ? "tung_front" : "tung_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Doge":                
        	fileName = isFront ? "Doge_Front" : "Doge_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Boss Baby":           
        	fileName = isFront ? "Boss_front" : "boss_back"; 
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Duolingo Bird":      
        	fileName = isFront ? "duo_front" : "duo_back"; 
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "William Shakespear": 
        	fileName = isFront ? "Shakespear_front" : "Shakespear_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Mahoraga":          
        	fileName = isFront ? "mahoraga_front" : "mahoraga_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "LeBron":            
        	fileName = isFront ? "Lebron_front" : "lebron_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Gigachad":          
        	fileName = isFront ? "Gigachad_front" : "gigachad_back";
        	folder = isFront ? "Goober_sprites_front/" : "Goober_sprites_back/";
        	break;
        case "Necromancer":
        	fileName = isFront ? "Necromancer_front" : "Necromancer_back";
        	folder = isFront ? "Trainer_sprites_front/" : "Trainer_sprites_back/";
        	break;
        case "Gambler":
        	fileName = isFront ? "Gambler_front" : "Gambler_back";
        	folder = isFront ? "Trainer_sprites_front/" : "Trainer_sprites_back/";
        	break;
        case "CS Student":
        	fileName = isFront ? "Femboy_front" : "Femboy_back";
        	folder = isFront ? "Trainer_sprites_front/" : "Trainer_sprites_back/";
        	break;
        case "Weeb":
        	fileName = isFront ? "Weeb_front" : "Weeb_back";
        	folder = isFront ? "Trainer_sprites_front/" : "Trainer_sprites_back/";
        	break;
        case "Joker":
            fileName = isFront ? "Joker_front" : "Joker_back";
            folder = isFront ? "Trainer_sprites_front/" : "Trainer_sprites_back/";
            break;
        default:                   
        	fileName = name; break;
		}
		
		return "/view/assets/images/" + folder + fileName + ".png";
	}
	
	/**
     * Loads the difficulty icon sprite.
     *
     * @param d difficulty
     * @return ImageView or null
     */
	public static ImageView getDiffSprite(Difficulty d) {
		String path = "/view/assets/images/other/" + d.getName() + "_face.png";
		URL url = ViewStyles.class.getResource(path);
		if (url == null) {
        	System.out.println("Image not found: " + path);
        	return null;
        }
		Image img = new Image(url.toExternalForm());
        ImageView iv = new ImageView(img);
        return iv;
	}
}
