package game;

import controller.TrainerSelectController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Trainer;
import networking.NetworkManager;
import networking.ReadyPacket;
import view.TrainerSelect;

public class MultiplayerSetupState implements GameState {

    private final GameManager gm;
    private final NetworkManager net;
    private final boolean isHost;

    private Trainer localTrainer;
    private Trainer remoteTrainer;
    private boolean localReady = false;
    private boolean remoteReady = false;

    public MultiplayerSetupState(GameManager gm, NetworkManager net, boolean isHost) {
        this.gm = gm;
        this.net = net;
        this.isHost = isHost;
    }

    // set from TrainerSelectController
    public void setLocalTrainer(Trainer t) {
        this.localTrainer = t;
        if (gm.getSession() != null) {
        	gm.getSession().setPlayerTrainer(t);
        }
    }

    // called by NetworkManager listener
    public void onNetworkDataReceived(Object obj) {
        if (obj instanceof ReadyPacket) {
            ReadyPacket rp = (ReadyPacket) obj;
            remoteTrainer = rp.getTrainer();
            remoteReady = true;
            maybeStartBattle();
        }
    }

    private void maybeStartBattle() {
        if (localTrainer != null && remoteTrainer != null && localReady && remoteReady) {
            // from THIS client’s POV, localTrainer is "player", remoteTrainer is "opponent"
            MultiplayerBattleState battle = new MultiplayerBattleState(gm, net, localTrainer, remoteTrainer, isHost);
            gm.setState(battle);
        }
    }

    @Override
    public void enter(UserSession session) {
        TrainerSelectState selectModel = new TrainerSelectState(gm);
        TrainerSelectController controller = new TrainerSelectController(selectModel, this);
        Stage stage = gm.getStage();
        TrainerSelect view = new TrainerSelect(stage, controller);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(view, 1140, 640));
        } else {
            stage.getScene().setRoot(view);
        }
    }
    
    public void onLocalReady(Trainer trainer) {
        // remember local trainer
        this.localTrainer = trainer;

        // Only touch session if it exists
        if (gm.getSession() != null) {
            gm.getSession().setPlayerTrainer(trainer);
        }

        localReady = true;

        // send a ReadyPacket that includes our trainer
        net.send(new ReadyPacket(trainer));

        maybeStartBattle();
    }
    
 // used if the controller already called setLocalTrainer(...)
    public void onLocalReady() {
        if (localTrainer == null) {
            return; // nothing to send
        }

        if (gm.getSession() != null) {
            gm.getSession().setPlayerTrainer(localTrainer);
        }

        localReady = true;
        net.send(new ReadyPacket(localTrainer));
        maybeStartBattle();
    }
}