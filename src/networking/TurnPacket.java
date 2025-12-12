package networking;

import java.io.Serializable;

public class TurnPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { ATTACK, SWITCH, ITEM, RUN }

    private final Type type;
    private final int index; // moveIndex or teamIndex
    private final String itemName; // for item use, null otherwise

    public TurnPacket(Type type, int index, String itemName) {
        this.type = type;
        this.index = index;
        this.itemName = itemName;
    }

    public Type getType() { return type; }
    public int getIndex() { return index; }
    public String getItemName() { return itemName; }
}