/**
 * Represents a move made by a player on the board.
 * Contains coordinates, special field value, and player ID.
 */
public class Move {
    private final int x, y, special, player;

    /**
     * Constructs a move with specified parameters.
     * @param x X-coordinate on the board.
     * @param y Y-coordinate on the board.
     * @param special Code for special tile interaction.
     * @param player ID of the player making the move.
     */
    public Move(int x, int y, int special, int player) {
        this.x = x;
        this.y = y;
        this.special = special;
        this.player = player;
    }

    /**
     * @return The move´s x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The move´s y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The move´s special item.
     */
    public int getSpecial() {
        return special;
    }

    /**
     * @return The player who made the move.
     */
    public int getPlayer() {
        return player;
    }
}
