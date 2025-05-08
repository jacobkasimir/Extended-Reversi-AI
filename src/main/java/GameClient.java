/**
 * GameClient manages the state of the game, including the map and player data.
 * It is responsible for initializing the game, updating state after moves, and computing valid moves.
 */
import java.util.Random;

public class GameClient {
    private MapController mapController;
    private GameAttributes gameAttributes;

    /**
     * @return The map controller object held by the game client.
     */
    public MapController getMapController() {
        return mapController;
    }

    /**
     * @return The game state object held by the game client.
     */
    public GameAttributes getGameAttributes() {
        return gameAttributes;
    }

    /**
     * Initializes the game state and map controller with the map and player information from the input lines.
     * @param lines The map file content split by lines.
     */
    public void init(String[] lines) {
        int playerCount = Integer.valueOf(lines[0]);
        int overrideCount = Integer.valueOf(lines[1]);

        String[] bombInfo = lines[2].split(" ");
        int bombCount = Integer.valueOf(bombInfo[0]);
        int bombRadius = Integer.valueOf(bombInfo[1]);

        String[] mapInfo = lines[3].split(" ");
        int height = Integer.valueOf(mapInfo[0]);
        int width = Integer.valueOf(mapInfo[1]);

        String[] map = splitString(lines, 4, (4 + height));
        String[] transitions = splitString(lines, (4 + height), lines.length);

        byte gamePhase = 1;

        this.gameAttributes = new GameAttributes(playerCount, overrideCount, bombCount, bombRadius, gamePhase);
        this.mapController = new MapController(map, transitions, width, height, bombRadius);

        System.out.println("Init abgeschlossen");
    }

    /**
     * Applies a move to the game, updating both the map and game state.
     * @param move The move to apply.
     */
    public void updateGame(Move move) {
        this.gameAttributes.updateState(move, this.mapController);
        this.mapController.updateMap(move, this.gameAttributes);
    }

    /**
     * Sets the ID for the current player.
     * @param id The player ID.
     */
    public void setOurPlayerId(byte id) {
        this.gameAttributes.setOurPlayerId(id);
        this.mapController.resetOurTiles(id);
        
        System.out.println("Spieler-ID erhalten: " + id);
    }

    /**
     * Handles disqualification of a player.
     * @param playerId ID of the disqualified player.
     */
    public void playerDisqualified(byte playerId) {
        this.gameAttributes.playerDisqualified(playerId);
    }

    /**
     * Stores time and depth limits for move calculations.
     * @param timeLimit The time limit for move calculation.
     * @param maxDepth The maximum search depth.
     */
    public void setCalculateParameters(int timeLimit, byte maxDepth) {
        // Placeholder
    }

    /**
     * Calculates a valid move using a simple brutforce deterministic strategy.
     * @param timeLimit Time constraint.
     * @param maxDepth Depth constraint.
     * @return A valid move or a dummy move if none found.
     */
    public Move calculateMove(int timeLimit, int maxDepth) {
        if(gameAttributes.isPhase1()) return MoveVerifier.allMovesPhase1(mapController, gameAttributes).getFirst();
        else return MoveVerifier.allMovesPhase2(mapController, gameAttributes).getFirst();
    }

    /**
     * Generates a random move within the bounds of the map.
     * @return A randomly generated move.
     */
    private Move generateRandomMove() {
        Random rand = new Random();
        int x = rand.nextInt(0, mapController.getWidth());
        int y = rand.nextInt(0, mapController.getHeight());
        return new Move(x, y, 0, gameAttributes.getOurPlayerId());
    }

    /**
     * Extracts a portion of the lines array into a new sub-array.
     * @param lines The full array.
     * @param min Start index (inclusive).
     * @param max End index (exclusive).
     * @return Sub-array of strings.
     */
    private static String[] splitString(String[] lines, int min, int max) {
        String[] res = new String[max - min];
        for (int i = min; i < max; i++) {
            res[i - min] = lines[i];
        }
        return res;
    }

    /**
     * Ends the current game phase by incrementing the phase.
     */
    public void endPhase() {
        this.gameAttributes.setGamePhase((byte) 2); 
    }

    /**
     * Ends the game (implementation pending).
     */
    public void endGame() {
        // End game logic
    }
}
