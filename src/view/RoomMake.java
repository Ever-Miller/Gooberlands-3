package view;

import java.io.IOException;
import java.net.InetAddress;

import game.GameManager;
import game.GameState;
import game.MultiplayerBattleState;
import game.MultiplayerSetupState;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import networking.NetworkManager;

public class RoomMake extends StackPane {

    private final Stage stage;
    private NetworkManager net;

    public RoomMake(Stage stage) {
        this.stage = stage;
        execute();
    }

    public void execute() {
        Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);

        BorderPane screen = new BorderPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(45);

        String ipAddress = "Unknown";
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {}

        Label ipLabel = ViewStyles.createStyledLabel("Your IP: " + ipAddress);
        Label statusLabel = ViewStyles.createStyledLabel("Waiting for connection...");

        Button btnBack = ViewStyles.createStyledButton("BACK");
        btnBack.setOnAction(event -> {
            if (net != null) {
                net.close();
            }

            FriendBattleSelect next_page = new FriendBattleSelect(stage);
            Scene scene = new Scene(next_page, 1140, 640);
            stage.setScene(scene);
        });

        Button settingsCog = ViewStyles.createSettingsButton(stage, () -> {
            if (net != null) {
                net.close();
            }
            stage.getScene().setRoot(new RoomMake(stage));
        });

        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new javafx.geometry.Insets(20));

        vbox.getChildren().addAll(ipLabel, statusLabel, btnBack);
        screen.setCenter(vbox);

        // --- Create NetworkManager and route messages to current GameState ---
        net = new NetworkManager();
        net.setListener(data -> {
            System.out.println("[HOST] Received data: " + data);

            // We are NOT on the JavaFX thread here
            Platform.runLater(() -> {
                GameManager gm = GameManager.getInstance();
                GameState state = gm.getState();

                if (state instanceof MultiplayerSetupState) {
                    ((MultiplayerSetupState) state).onNetworkDataReceived(data);
                } else if (state instanceof MultiplayerBattleState) {
                    ((MultiplayerBattleState) state).onNetworkDataReceived(data);
                } else {
                    System.out.println("[HOST] Received data in non-multiplayer state: " + data);
                }
            });
        });

        // Start listening as host on a background thread.
        new Thread(() -> {
            try {
                System.out.println("[HOST] Starting server on port 4000...");
                net.startHost(4000);   // blocks until client connects

                // Once connected, move to team select (MultiplayerSetupState)
                Platform.runLater(() -> {
                    statusLabel.setText("Client connected! Opening team select...");
                    GameManager gm = GameManager.getInstance();
                    MultiplayerSetupState setupState =
                            new MultiplayerSetupState(gm, net, true);
                    gm.setState(setupState);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Error starting server."));
            }
        }, "Host-Accept-Thread").start();

        this.getChildren().addAll(background, screen, settingsCog);
    }
}