package networking;

import java.io.Serializable;

import models.Trainer;

/**
 * Simple data packet used to indicate that one player has finished their trainer / team selection and is ready to start the multiplayer battle.
 */
public class ReadyPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/* The trainer (with selected goobers) for this player. */
	private final Trainer trainer;
	
	public ReadyPacket(Trainer trainer) {
		this.trainer = trainer;
	}
	
	public Trainer getTrainer() {
		return trainer;
	}
}
 