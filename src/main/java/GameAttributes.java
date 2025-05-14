/**
 * Captures the current state of the game, including information about the number of players,
 * their individual game status, the current game phase, and the ID of the player using the client.
 */
public class GameAttributes {
    private Player[] players;
    private int numberPlayers;
    private boolean phase1;                 //Phase 1: true; Phase 2: false
    private byte ourPlayerId;

    /**
     * Constructor of class GameAttributes.
     * @param numberPlayers number of players participating in the game.
     * @param overwrites number of overwrites, each player has initially.
     * @param bombs number of bombs, each player has initially.
     * @param bombRadius the radius of the bomb.
     * @param gamePhase Phase 1 or Phase 2
     */
    public GameAttributes(int numberPlayers, int overwrites, int bombs, int bombRadius, byte gamePhase){
        this.numberPlayers = numberPlayers;
        this.setGamePhase(gamePhase);
        this.players = new Player[numberPlayers];
        for (int i = 1; i <= numberPlayers; i++) {
            Player p = new Player(bombs, overwrites, i, true);
            this.setPlayer(p, i);
        }
    }

    public GameAttributes(GameAttributes obj){
        this.numberPlayers = obj.getNumberPlayers();
        this.phase1 = obj.isPhase1();
        this.ourPlayerId = obj.getOurPlayerId();
        this.players = new Player[obj.getNumberPlayers()];
        for (int i = 0; i < numberPlayers; i++) {
            players[i] = new Player(obj.getPlayer(i+1));
        }
    }

    /**
     * Adds a player to the dictionary at number.
     * @param p
     * @param number
     */
    private void setPlayer(Player p, int number){
        players[number -1] = p;
    }

    /**
     * Returns the number of players.
     * @return amount of currently playing players.
     */
    public int getNumberPlayers() {
        return numberPlayers;
    }

    /**
     * Defines, how many players can participate.
     * @param numberPlayers Number of players
     */
    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    /**
     * Sets whether the game is in phase 1.
     * @param phase1 true to activate phase 1, false for phase 2.
     */
    public void setPhase1(boolean phase1) {
        this.phase1 = phase1;
    }

    /**
     * Checks if the game is currently in phase 1.
     * @return true if in phase 1, false if in phase 2.
     */
    public boolean isPhase1() {
        return phase1;
    }

    /**
     * Returns the Player object associated with the given player number.
     * @param number the identifier of the player.
     * @return the Player instance for the specified player number.
     */
    public Player getPlayer(int number){
        return players[number -1];
    }

    /**
     * Returns the ID of the local player using this client.
     * @return the byte value representing our player ID.
     */
    public byte getOurPlayerId(){
        return ourPlayerId;
    }

    /**
     * Sets the ID of the local player using this client.
     * @param id the byte value of the player ID to assign.
     */
    public void setOurPlayerId(byte id){
        ourPlayerId = id;
    }

    /**
     * Marks the specified player as disqualified (out of the game).
     * @param id the ID of the player to disqualify.
     */
    public void playerDisqualified(byte id){
        this.getPlayer(id).setInGame(false);
    }

    /**
     * Sets the current game phase based on the provided phase number.
     * @param phase the phase to set (1 for phase1, 2 for phase2).
     */
    public void setGamePhase(byte phase){
        if (phase == 1) {
            this.setPhase1(true);
        } else if (phase == 2) {
            this.setPhase1(false);
        }
    }

    /**
     * Retrieves the current game phase as a byte value.
     * @return 1 if the game is in phase 1, otherwise 2 for phase 2.
     */
    public byte getGamePhase(){
        return (byte) (this.isPhase1() ? 1 : 2);
    }

    /**
     * Refreshes the game state based on the provided move.
     * @param move the Move object representing the latest action to apply.
     */
    public void updateState(Move move, MapController map){
        
        if(isPhase1()){
            if(GameRules.occupied(move, map) || GameRules.isExpansion(map, move)){
                this.getPlayer(move.player()).setNumberOverwrites(this.getPlayer(move.player()).getNumberOverwrites() - 1);
            }else if (move.special() == 20) {
                this.getPlayer(move.player()).setNumberBombs(this.getPlayer(move.player()).getNumberBombs() + 1);
            }else if (move.special() == 21) {
                this.getPlayer(move.player()).setNumberOverwrites(this.getPlayer(move.player()).getNumberOverwrites() + 1);
            }
        }else{
            this.getPlayer(move.player()).setNumberBombs(this.getPlayer(move.player()).getNumberBombs() - 1);
        }
    }
}

