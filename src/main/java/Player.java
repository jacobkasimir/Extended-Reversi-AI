/**
 * Represents a player in the game, tracking their available bombs,
 * override stones, player number, and in-game status.
 */
public class Player {
    private int numberBombs;
    private int numberOverrides;
    private int playerId;
    private boolean inGame = true;

    /**
     * Constructs a Player with specified parameters.
     *
     * @param bombs       initial number of bombs available to the player
     * @param overrides   initial number of overrides stones available
     * @param id          unique identifier for the player
     * @param in          initial in-game status (true if active)
     */
    public Player(int bombs, int overrides, int id, boolean inGame) {
        this.numberBombs = bombs;
        this.numberOverrides = overrides;
        this.playerId = id;
        this.inGame = inGame;
    }

    /**
     * Copy constructor. Creates a deep copy of another Player instance.
     *
     * @param player the Player instance to copy
     */
    public Player(Player player){
        this.numberBombs = player.getNumberBombs();
        this.numberOverrides = player.getNumberOverrides();
        this.playerId = player.getPlayerNumber();
        this.inGame = player.isInGame();
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
     * Returns the player's unique identifier.
     *
     * @return the integer representing the player's number
     */
    public int getPlayerNumber() {
        return playerId;
    }

    /**
     * Updates the player's unique identifier.
     *
     * @param id new playerId to set
     */
    public void setPlayerId(int id) {
        this.playerId = id;
    }

    /**
     * Returns the current number of override stones the player has.
     *
     * @return the count of override stones available
     */
    public int getNumberOverrides() {
        return numberOverrides;
    }

    /**
     * Sets the number of override stones for the player.
     *
     * @param numberOverrides new count of override stones
     */
    public void setNumberOverrides(int numberOverrides) {
        this.numberOverrides = numberOverrides;
    }

    /**
     * Returns the current number of bombs the player has.
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
