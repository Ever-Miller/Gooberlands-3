package view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * File: EndGameView.java
 *
 * Purpose:
 *     Displays the end-of-game victory and credits screen.
 *
 *     This screen:
 *       - Shows a victory message
 *       - Displays developer credits
 *       - Presents a final navigation button
 *
 *     Transitions are handled using timed fade animations.
 *     All navigation control is delegated via callback.
 */
public class EndGameView extends StackPane {

	/** JavaFX stage used for scene navigation. */
	private Stage stage;

	/** Callback executed when the player exits the end screen. */
    private Runnable onFinish;

    /**
     * Constructs the end-of-game view.
     *
     * @param stage active JavaFX stage
     * @param onFinish callback invoked when returning to main menu
     */
    public EndGameView(Stage stage, Runnable onFinish) {
        this.stage = stage;
        this.onFinish = onFinish;
        execute();
    }
    
    /**
     * Builds the victory, credits, and final navigation UI
     * and starts the animation sequence.
     */
    private void execute() {
        this.setStyle("-fx-background-color: black;");

        // VICTORY SCREEN 
        VBox victoryBox = new VBox(20);
        victoryBox.setAlignment(Pos.CENTER);
        victoryBox.setOpacity(0);

        Label title = new Label("VICTORY");
        title.setTextFill(Color.GOLD);
        title.setFont(Font.font("Impact", 80));
        title.setEffect(new javafx.scene.effect.DropShadow(10, Color.ORANGE));

        Label subTitle = new Label("The Joker has been defeated.");
        subTitle.setTextFill(Color.WHITE);
        subTitle.setFont(Font.font("Arial", 30));

        Label funny = new Label("(Society is finally healing)");
        funny.setTextFill(Color.GRAY);
        funny.setFont(Font.font("Comic Sans MS", 20));
        
        Label funny2 = new Label("Goober is the way of the future");
        funny2.setTextFill(Color.GRAY);
        funny2.setFont(Font.font("Comic Sans MS", 20));

        victoryBox.getChildren().addAll(title, subTitle, funny, funny2);

        // CREDITS
        VBox creditsBox = new VBox(30);
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setOpacity(0);

        Label credTitle = new Label("DEVELOPED BY");
        credTitle.setTextFill(Color.RED);
        credTitle.setFont(Font.font("Impact", 50));

        Label names = new Label("Ever Miller\nEric Romero\nYuito Sugimoto\nAkshat Jaiswal");
        names.setTextFill(Color.WHITE);
        names.setTextAlignment(TextAlignment.CENTER);
        names.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Label funny3 = new Label("Please give us an A.\nWe are tired.");
        funny3.setTextFill(Color.LIGHTGRAY);
        funny3.setTextAlignment(TextAlignment.CENTER);
        funny3.setFont(Font.font("Comic Sans MS", 18));

        creditsBox.getChildren().addAll(credTitle, names, funny3);

        // FINAL BUTTON
        VBox finalBox = new VBox(20);
        finalBox.setAlignment(Pos.CENTER);
        finalBox.setOpacity(0);

        Label thanks = new Label("Thanks for playing Gooberlands III\n and don't forget to like and subscribe");
        thanks.setTextFill(Color.CYAN);
        thanks.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        thanks.setTextAlignment(TextAlignment.CENTER);

        Button menuBtn = ViewStyles.createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> onFinish.run());

        finalBox.getChildren().addAll(thanks, menuBtn);

        this.getChildren().addAll(victoryBox, creditsBox, finalBox);

        // ANIMATION SEQUENCE
        SequentialTransition seq = new SequentialTransition();

        seq.getChildren().addAll(
            // 1. Victory Fade In/Out
            new PauseTransition(Duration.seconds(2.0)),
            animateNode(victoryBox, 1.5, 2.5),
            
            // 2. Credits Fade In/Out
            animateNode(creditsBox, 1.5, 2.5),

            // 3. Final Button Fade In (Stays)
            fadeIn(finalBox, 1.5)
        );

        seq.play();
    }
    
    /**
     * Creates a fade-in → pause → fade-out animation
     * sequence for a single UI node.
     *
     * @param node UI node to animate
     * @param fadeTime duration of fade transitions
     * @param pauseTime duration to remain visible
     * @return sequential animation transition
     */
    private SequentialTransition animateNode(javafx.scene.Node node, double fadeTime, double pauseTime) {
        FadeTransition in = new FadeTransition(Duration.seconds(fadeTime), node);
        in.setFromValue(0);
        in.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(pauseTime));

        FadeTransition out = new FadeTransition(Duration.seconds(fadeTime), node);
        out.setFromValue(1);
        out.setToValue(0);

        return new SequentialTransition(in, stay, out);
    }
    
    /**
     * Creates a simple fade-in animation for a UI node.
     *
     * @param node UI node to animate
     * @param time duration of the fade-in
     * @return fade transition
     */
    private FadeTransition fadeIn(javafx.scene.Node node, double time) {
        FadeTransition ft = new FadeTransition(Duration.seconds(time), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        return ft;
    }
}
