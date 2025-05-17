/**
 * Represents a player in the game, tracking their available bombs,
 * overwrites, player number, and in-game status.
 */
public class Player {
    private int numberBombs;
    private int numberOverwrites;
    private int playerNumber;
    private boolean inGame = true;
    IntLinkedList tiles;

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
        this.tiles = new IntLinkedList();
    }

    public Player(Player player){
        this.numberBombs = player.getNumberBombs();
        this.numberOverwrites = player.getNumberOverwrites();
        this.playerNumber = player.getPlayerNumber();
        this.inGame = player.isInGame();
        this.tiles = new IntLinkedList();
        this.tiles.addAll(player.getTiles());
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
    public void setPlayerNumber(byte playerNumber) {
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

    /**
     * Getter function for the set containing all tiles of one player.
     * @return tiles of one player.
     */
    public IntLinkedList getTiles() {
        return tiles;
    }


    /**
     * Sets the Set of tiles, to the tiles given by the function.
     */
    public void setTiles(IntLinkedList tiles) {
        this.tiles = tiles;
    }


    /**
     * Adds one tile to the set if it isn't already added.
     */
    public void addTile(int c){
        this.tiles.addIfNotContains(c);
    }    


}
