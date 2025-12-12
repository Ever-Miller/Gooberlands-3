/**
 * File: IntroState.java
 * Purpose:
 *      Displays the introductory cinematic sequence when a new
 *      single-player game begins before transitioning to the world map.
 */

package game;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * A game state that presents a cinematic intro sequence.
 * <p>
 * This state renders a sequence of styled text elements with timed
 * fade-in and fade-out animations to establish story context and mood.
 * Once the sequence completes, the game transitions to {@link MapState}.
 * </p>
 */
public class IntroState implements GameState {

    private GameManager gm;
    private StackPane root;

    /**
     * Constructs an IntroState.
     *
     * @param gm the main game manager
     */
    public IntroState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes and plays the intro cinematic sequence.
     * Sets up animated text elements, applies transitions,
     * and transitions to the world map upon completion.
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        Label title = new Label("GOOBERLANDS III™");
        title.setTextFill(Color.RED);
        title.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        title.setOpacity(0);

        Label creators = new Label(
                "Created By\n\n[  Ever Miller  Eric Romero  Yuito Sugimoto  Akshat Jaiswal  ]\n\n(The Goober Team)"
        );
        creators.setTextFill(Color.WHITE);
        creators.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        creators.setAlignment(Pos.CENTER);
        creators.setTextAlignment(TextAlignment.CENTER);
        creators.setOpacity(0);

        Label truth = new Label("Based on a true story");
        truth.setTextFill(Color.WHITE);
        truth.setFont(Font.font("Courier New", FontWeight.BOLD, 40));
        truth.setOpacity(0);

        VBox loreBox = new VBox(20);
        loreBox.setAlignment(Pos.CENTER);
        loreBox.setOpacity(0);

        Label lore1 = new Label("The JOKER has taken over");
        lore1.setTextFill(Color.PURPLE);
        lore1.setFont(Font.font("Arial", FontWeight.BOLD, 45));

        Label lore2 = new Label("You must fight through his army of goobers");
        lore2.setTextFill(Color.WHITE);
        lore2.setFont(Font.font("Arial", 30));

        Label lore3 = new Label("and take him down");
        lore3.setTextFill(Color.RED);
        lore3.setFont(Font.font("Arial", FontWeight.BOLD, 40));

        loreBox.getChildren().addAll(lore1, lore2, lore3);

        Label enjoy = new Label("Good Luck.");
        enjoy.setTextFill(Color.WHITE);
        enjoy.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        enjoy.setOpacity(0);

        root.getChildren().addAll(title, creators, truth, loreBox, enjoy);

        if (gm.getStage() != null) {
            gm.getStage().getScene().setRoot(root);
        }

        SequentialTransition seq = new SequentialTransition();
        seq.getChildren().addAll(
                new PauseTransition(Duration.seconds(3.3)),
                createFadeSequence(title, 1.5, 1.0, 0.5),
                createFadeSequence(creators, 1.5, 1.0, 0.5),
                createFadeSequence(truth, 1.0, 1.0, 0.5),
                createFadeSequence(loreBox, 1.5, 4.0, 1.0),
                createFadeSequence(enjoy, 1.5, 1.0, 1.5),
                new PauseTransition(Duration.seconds(0.5))
        );

        seq.setOnFinished(e -> gm.setState(new MapState(gm)));
        seq.play();
    }

    /**
     * Creates a fade-in, pause, and fade-out animation sequence
     * for a JavaFX node.
     *
     * @param target the node to animate
     * @param fadeInTime duration of the fade-in phase (seconds)
     * @param stayTime duration to remain fully visible (seconds)
     * @param fadeOutTime duration of the fade-out phase (seconds)
     * @return a configured {@link SequentialTransition}
     */
    private SequentialTransition createFadeSequence(
            Node target,
            double fadeInTime,
            double stayTime,
            double fadeOutTime) {

        FadeTransition in = new FadeTransition(Duration.seconds(fadeInTime), target);
        in.setFromValue(0);
        in.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(stayTime));

        FadeTransition out = new FadeTransition(Duration.seconds(fadeOutTime), target);
        out.setFromValue(1);
        out.setToValue(0);

        return new SequentialTransition(in, stay, out);
    }
}
