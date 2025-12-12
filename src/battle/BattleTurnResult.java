/**
 * File: BattleTurnResult.java
 * Purpose:
 *      Encapsulates the result of a single battle turn.
 *      Stores whether the battle has concluded and any log messages
 *      generated during the turn.
 */

package battle;

import java.util.ArrayList;
import java.util.List;

public class BattleTurnResult {
    private boolean finished;
    private List<String> logs;

    /**
     * Constructs a BattleTurnResult for a single turn.
     *
     * @param finished true if the battle has ended after this turn,
     *                 false otherwise
     */
    public BattleTurnResult(boolean finished) {
        this.finished = finished;
        this.logs = new ArrayList<>();
    }

    /**
     * Adds a log message describing an event that occurred during the turn.
     *
     * @param message the log message to add
     */
    public void addLog(String message) {
        logs.add(message);
    }

    /**
     * Returns the list of log messages generated during the turn.
     *
     * @return the turn log messages
     */
    public List<String> getLogs() {
        return logs;
    }

    /**
     * Indicates whether the battle has ended after this turn.
     *
     * @return true if the battle is finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }
}
