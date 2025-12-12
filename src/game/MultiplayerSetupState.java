package game;

import controller.TrainerSelectController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Trainer;
import networking.NetworkManager;
import networking.ReadyPacket;
import view.TrainerSelect;
import game.UserSession;
import game.TrainerSelectState;
import game.GameManager;
import game.GameState;

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

    // called by controller
    public void setLocalTrainer(Trainer t) {
        this.localTrainer = t;
        gm.getSession().setPlayerTrainer(t);
    }

    public void onLocalReady() {
        localReady = true;
        maybeStartBattle();
    }

    // called by NetworkManager listener
    public void onNetworkDataReceived(Object obj) {
        if (obj instanceof ReadyPacket) {
        	ReadyPacket rp = (ReadyPacket) obj;
        	remoteTrainer = rp.getTrainer();
        	remoteReady = true;
        }
        maybeStartBattle();
    }

    private void maybeStartBattle() {
        if (localTrainer != null && remoteTrainer != null && localReady && remoteReady) {
            MultiplayerBattleState battle = new MultiplayerBattleState(gm, net, remoteTrainer);
            gm.setState(battle);
        }
    }

    @Override
    public void enter(UserSession session) {
        // create a fresh TrainerSelectState model (same one you use in single-player)
        TrainerSelectState selectModel = new TrainerSelectState(gm);

        // use the multiplayer-aware controller
        TrainerSelectController controller = new TrainerSelectController(selectModel, this);

        // build the view
        Stage stage = gm.getStage();
        TrainerSelect view = new TrainerSelect(stage, controller);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(view, 1140, 640));
        } else {
            stage.getScene().setRoot(view);
        }
    }

	public void onLocalReady(Trainer trainer) {
		this.localTrainer = trainer;
		localReady = true;
		
		gm.ensureSession();
		gm.getSession().setPlayerTrainer(trainer);
		
		net.send(new ReadyPacket(trainer));
		maybeStartBattle();
		
	}
}