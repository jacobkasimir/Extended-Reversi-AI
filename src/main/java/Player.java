/**
 * Represents a player in the game, tracking their available bombs,
 * overwrites, player number, and in-game status.
 */
public class Player {
    private int numberBombs;
    private int numberOverwrites;
    private int playerNumber;
    private boolean inGame = true;

    /**
     * Constructs a Player with specified resources and status.
     *
     * @param bombs       initial number of bombs available to the player
     * @param overwrites  initial number of overwrite tokens available
     * @param number      unique identifier for the player
     * @param in          initial in-game status (true if active)
     */
    public Player(int bombs, int overwrites, int number, boolean in) {
        this.numberBombs = bombs;
        this.numberOverwrites = overwrites;
        this.playerNumber = number;
        this.inGame = in;
    }

    /**
     * Checks whether the player is still in the game.
     *
     * @return true if the player is active, false if disqualified
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * Sets the player's in-game status.
     *
     * @param inGame true to mark the player as active, false to disqualify
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * Retrieves the player's unique identifier.
     *
     * @return the integer representing the player's number
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Updates the player's unique identifier.
     *
     * @param playerNumber new player number to set
     */
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * Retrieves the current number of overwrite tokens the player has.
     *
     * @return the count of overwrites available
     */
    public int getNumberOverwrites() {
        return numberOverwrites;
    }

    /**
     * Sets the number of overwrite tokens for the player.
     *
     * @param numberOverwrites new count of overwrites
     */
    public void setNumberOverwrites(int numberOverwrites) {
        this.numberOverwrites = numberOverwrites;
    }

    /**
     * Retrieves the current number of bombs the player has.
     *
     * @return the count of bombs available
     */
    public int getNumberBombs() {
        return numberBombs;
    }

    /**
     * Sets the number of bombs available to the player.
     *
     * @param numberBombs new count of bombs
     */
    public void setNumberBombs(int numberBombs) {
        this.numberBombs = numberBombs;
    }
}
