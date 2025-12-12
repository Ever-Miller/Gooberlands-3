package view;

import java.io.IOException;

import game.GameManager;
import game.GameState;
import game.MultiplayerBattleState;
import game.MultiplayerSetupState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import networking.NetworkManager;

public class Search extends StackPane {

    private final Stage stage;
    private NetworkManager net;

    public Search(Stage stage) {
        this.stage = stage;
        execute();
    }

    public void execute() {
        Rectangle background = new Rectangle(1140, 640, ViewStyles.BACKGROUND_COLOR);

        BorderPane screen = new BorderPane();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(45);

        HBox code_input = new HBox();
        code_input.setAlignment(Pos.CENTER);

        Label lbl = ViewStyles.createStyledLabel("HOST IP:");
        lbl.setAlignment(Pos.CENTER_RIGHT);
        lbl.setPadding(new Insets(0, 15, 0, 0));

        TextField inputField = ViewStyles.createStyledTextField("IP ADDRESS");
        code_input.getChildren().addAll(lbl, inputField);

        Button btnConnect = ViewStyles.createStyledButton("Connect");

        btnConnect.setOnAction(event -> {
            String ip = inputField.getText().trim();
            if (ip.isEmpty()) {
                return;
            }

            btnConnect.setText("Connecting...");
            btnConnect.setDisable(true);

            net = new NetworkManager();

            // Route all incoming data to the *current* GameState,
            // just like we did on the host side.
            net.setListener(data -> {
                System.out.println("[CLIENT] Received data: " + data);
                Platform.runLater(() -> {
                    GameManager gm = GameManager.getInstance();
                    GameState state = gm.getState();

                    if (state instanceof MultiplayerSetupState) {
                        ((MultiplayerSetupState) state).onNetworkDataReceived(data);
                    } else if (state instanceof MultiplayerBattleState) {
                        ((MultiplayerBattleState) state).onNetworkDataReceived(data);
                    } else {
                        System.out.println("[CLIENT] Received multiplayer data in non-multiplayer state: " + data);
                    }
                });
            });

            // Connect in a background thread
            new Thread(() -> {
                try {
                    net.connectTo(ip, 4000);

                    // Once connected, go into MultiplayerSetupState (team select)
                    Platform.runLater(() -> {
                        GameManager gm = GameManager.getInstance();
                        MultiplayerSetupState setupState =
                                new MultiplayerSetupState(gm, net, false); // isHost = false on client
                        gm.setState(setupState);
                    });

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        btnConnect.setText("Connect");
                        btnConnect.setDisable(false);
                    });
                }
            }, "Client-Connect-Thread").start();
        });

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
            stage.getScene().setRoot(new Search(stage));
        });

        StackPane.setAlignment(settingsCog, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsCog, new Insets(20));

        code_input.setPadding(new Insets(0, 200, 0, 0));
        vbox.getChildren().addAll(code_input, btnConnect, btnBack);
        screen.setCenter(vbox);

        this.getChildren().addAll(background, screen, settingsCog);
    }
}