package networking;

import java.io.Serializable;
import java.util.List;

import models.Trainer;

public class BattleUpdatePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Trainer player;
    private final Trainer opponent;
    private final List<String> logs;

    public BattleUpdatePacket(Trainer player, Trainer opponent, List<String> logs) {
        this.player = player;
        this.opponent = opponent;
        this.logs = logs;
    }

    public Trainer getPlayer() { return player; }
    public Trainer getOpponent() { return opponent; }
    public List<String> getLogs() { return logs; }
}