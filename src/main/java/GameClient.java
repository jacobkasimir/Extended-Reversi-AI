/**
 * GameClient manages the state of the game, including the map and player data.
 * It is responsible for initializing the game, updating state after moves, and computing valid moves.
 */
import java.util.Random;

public class GameClient {
    private GameState current_gameState;
    private Transitions transitions;
    private Heuristics heuristics;
    private int depthLimit;
    private int timeLimit;

    /**
     * @return The map controller object held by the game client.
     */
    public MapController getCurrentMapController() {
        return current_gameState.mapController();
    }

    /**
     * @return The game state object held by the game client.
     */
    public GameAttributes getCurrentGameAttributes() {
        return current_gameState.attributes();
    }

    /**
     * @return The Transitions of the map.
     */
    public Transitions getTransitions(){
        return transitions;
    }

    /**
     * @return The current gameState
     */
    public GameState getCurrentGameState() {
        return current_gameState;
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

        //initializes transitions
        initTransitions(transitions, width, height);

        this.current_gameState = new GameState(new MapController(map, this.transitions, width, height, bombRadius), 
                                    new GameAttributes(playerCount, overrideCount, bombCount, bombRadius, gamePhase));
        
        this.heuristics = new Heuristics(getCurrentMapController());

        System.out.println("Init abgeschlossen");
    }

    /** 
     * Initializes the transitions by translating the message sent by the server
     * @param transitionsString The data sent by the server
     * @param width The width of the map
     * @param height The height of the map
     */
    public void initTransitions(String[] transitionsString, int width, int height){
        this.transitions = new Transitions(width, height);

        String[][] newTransition = new String[transitionsString.length][7]; //array that holds all transitions as Strings
        for (int i = 0; i < transitionsString.length; i++) {
            newTransition[i] = transitionsString[i].split(" ");
        }

        for (String[] newTransition1 : newTransition) {
            transitions.setTransition(Integer.parseInt(newTransition1[0]), Integer.parseInt(newTransition1[1]), 
                Integer.parseInt(newTransition1[2]), Integer.parseInt(newTransition1[4]), Integer.parseInt(newTransition1[5]), Integer.parseInt(newTransition1[6]));
            transitions.setTransition(Integer.parseInt(newTransition1[4]), Integer.parseInt(newTransition1[5]), Integer.parseInt(newTransition1[6]), 
                Integer.parseInt(newTransition1[0]), Integer.parseInt(newTransition1[1]), Integer.parseInt(newTransition1[2]));
        }
    }

    /**
     * Applies a move to the game, updating both the map and game state.
     * @param move The move to apply.
     */
    public void updateGame(Move move) {
        this.current_gameState.updateGameState(move);
    }

    /**
     * Sets the ID for the current player.
     * @param id The player ID.
     */
    public void setOurPlayerId(byte id) {
        this.current_gameState.attributes().setOurPlayerId(id);
        this.current_gameState.mapController().resetTiles(id, current_gameState.attributes());
        
        System.out.println("Spieler-ID erhalten: " + id);
    }

    /**
     * Handles disqualification of a player.
     * @param playerId ID of the disqualified player.
     */
    public void playerDisqualified(byte playerId) {
        this.current_gameState.attributes().playerDisqualified(playerId);
    }

    /**
     * Stores time and depth limits for move calculations.
     * @param timeLimit The time limit for move calculation.
     * @param maxDepth The maximum search depth.
     */
    public void setCalculateParameters(int timeLimit, byte maxDepth) {
        this.depthLimit = maxDepth;
        this.timeLimit = timeLimit;
    }

    public Move calculateMove(){
        MiniMax miniMax = new MiniMax(current_gameState, this.heuristics, timeLimit, depthLimit);
        return miniMax.chooseMove();

        //AlphaBeta alphaBeta = new AlphaBeta(current_gameState, this.heuristics, timeLimit, depthLimit);
        //return alphaBeta.chooseMove();
    }

    

    /**
     * Generates a random move within the bounds of the map.
     * @return A randomly generated move.
     */
    private Move generateRandomMove() {
        Random rand = new Random();
        int x = rand.nextInt(0, current_gameState.mapController().getWidth());
        int y = rand.nextInt(0, current_gameState.mapController().getHeight());
        return new Move(x, y, 0, current_gameState.attributes().getOurPlayerId());
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
        this.current_gameState.attributes().setGamePhase((byte) 2); 
    }

    /**
     * Ends the game (implementation pending).
     */
    public void endGame() {
        // End game logic
    }

}
