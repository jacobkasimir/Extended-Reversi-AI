/**
 * Represents the current attributes of a game state.
 * Tracks the number of players, their individual states, the current game phase,
 * and the ID of the player using this client.
 */
public class GameAttributes {

    private final Player[] players;
    private int numberPlayers;
    private boolean phase1; // true: Phase 1; false: Phase 2
    private int ourPlayerId;

    /**
     * Constructs a new GameAttributes instance with the specified parameters.
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
        this.players[number - 1] = p;
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
     * Sets the number of players.
     *
     * @param numberPlayers the number of players to set
     */
    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    /**
     * Set boolean value for phase 1.
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
     * Gets the Player for the playerId (from 1...).
     *
     * @param id the 1-based playerId
     * @return the Player object
     */
    public Player getPlayer(int id) {
        return players[id - 1];
    }

    /**
     * Gets the ID of the current player which represents our Group.
     *
     * @return our player ID
     */
    public int getOurPlayerId() {
        return ourPlayerId;
    }

    /**
     * Sets the ID of the current player which represent our Group.
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
     * Updates the game attributes according to the given move.
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
                //Placing a stone on an occupied field or an expnsion tile requires an override
                player.setNumberOverwrites(player.getNumberOverwrites() - 1);
            } else if (special == 20) {
                //getting an extra bomb through a bonus field
                player.setNumberBombs(player.getNumberBombs() + 1);
            } else if (special == 21) {
                //getting an extra override stone through a bonus field
                player.setNumberOverwrites(player.getNumberOverwrites() + 1);
            }
        } else {
            //Moves in phase 2 are always bomb throws -> bombCount must be decremented by 1
            player.setNumberBombs(player.getNumberBombs() - 1);
        }
    }
}
