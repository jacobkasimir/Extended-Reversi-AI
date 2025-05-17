/**
 * Represents the current state of the game.
 * Tracks the number of players, their individual states, the current game phase,
 * and the ID of the player using this client.
 */
public class GameAttributes {

    private final Player[] players;
    private int numberPlayers;
    private boolean phase1; // true: Phase 1; false: Phase 2
    private int ourPlayerId;

    /**
     * Constructs a new GameAttributes instance with the specified initial game setup.
     *
     * @param numberPlayers the number of players participating in the game
     * @param overwrites the initial number of Overwrites per player
     * @param bombs the initial number of bombs per player
     * @param bombRadius the bomb radius (currently unused)
     * @param gamePhase 1 for phase 1, 2 for phase 2
     */
    public GameAttributes(int numberPlayers, int overwrites, int bombs, int bombRadius, byte gamePhase) {
        this.numberPlayers = numberPlayers;
        this.setGamePhase(gamePhase);
        this.players = new Player[numberPlayers];

        for (int i = 1; i <= numberPlayers; i++) {
            Player p = new Player(bombs, overwrites, i, true);
            this.setPlayer(p, i);
        }
    }

    /**
     * Copy constructor. Creates a deep copy of another GameAttributes instance.
     *
     * @param obj the GameAttributes instance to copy
     */
    public GameAttributes(GameAttributes obj) {
        this.numberPlayers = obj.getNumberPlayers();
        this.phase1 = obj.isPhase1();
        this.ourPlayerId = obj.getOurPlayerId();
        this.players = new Player[obj.getNumberPlayers()];

        for (int i = 0; i < numberPlayers; i++) {
            players[i] = new Player(obj.getPlayer(i + 1));
        }
    }

    /**
     * Sets a Player instance at the specified player index.
     *
     * @param p the Player object to assign
     * @param number the 1-based player number
     */
    private void setPlayer(Player p, int number) {
        players[number - 1] = p;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return the player count
     */
    public int getNumberPlayers() {
        return numberPlayers;
    }

    /**
     * Sets the number of players (adjusting internal tracking, but not player data).
     *
     * @param numberPlayers the number of players to set
     */
    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    /**
     * Enables or disables phase 1.
     *
     * @param phase1 true to enable phase 1, false for phase 2
     */
    public void setPhase1(boolean phase1) {
        this.phase1 = phase1;
    }

    /**
     * Checks if the game is currently in phase 1.
     *
     * @return true if in phase 1, false if in phase 2
     */
    public boolean isPhase1() {
        return phase1;
    }

    /**
     * Gets the Player instance for the given 1-based player number.
     *
     * @param number the 1-based player number
     * @return the Player object
     */
    public Player getPlayer(int number) {
        return players[number - 1];
    }

    /**
     * Gets the ID of the current player using this client.
     *
     * @return the player ID
     */
    public int getOurPlayerId() {
        return ourPlayerId;
    }

    /**
     * Sets the ID of the player using this client.
     *
     * @param id the player ID
     */
    public void setOurPlayerId(int id) {
        this.ourPlayerId = id;
    }

    /**
     * Disqualifies a player from the game.
     *
     * @param id the player ID to disqualify
     */
    public void playerDisqualified(int id) {
        getPlayer(id).setInGame(false);
    }

    /**
     * Sets the current game phase.
     *
     * @param phase 1 for phase 1, 2 for phase 2
     */
    public void setGamePhase(byte phase) {
        this.phase1 = (phase == 1);
    }

    /**
     * Gets the current game phase.
     *
     * @return 1 for phase 1, 2 for phase 2
     */
    public byte getGamePhase() {
        return (byte) (phase1 ? 1 : 2);
    }

    /**
     * Updates the game state according to the given move.
     * Handles Overwrites and bomb gains/losses according to current phase and rules.
     *
     * @param move the encoded move
     * @param map the current map state
     */
    public void updateState(int move, MapController map) {
        int playerId = MoveVerifier.decodePlayerId(move);
        Player player = getPlayer(playerId);
        int special = MoveVerifier.decodeSpecial(move);

        if (phase1) {
            if (GameRules.occupied(move, map) || GameRules.isExpansion(map, move)) {
                player.setNumberOverwrites(player.getNumberOverwrites() - 1);
            } else if (special == 20) {
                player.setNumberBombs(player.getNumberBombs() + 1);
            } else if (special == 21) {
                player.setNumberOverwrites(player.getNumberOverwrites() + 1);
            }
        } else {
            player.setNumberBombs(player.getNumberBombs() - 1);
        }
    }
}
