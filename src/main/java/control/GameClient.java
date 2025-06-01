package control;

import algorithms.strategies.*;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import gamestate.map.Transitions;

/**
 * GameClient manages the overall game logic on the client side, including the game state,
 * with player attributes, map controller, transitions, and move calculations.
 */
public class GameClient {
    private GameState current_gameState;
    private Transitions transitions;
    private Heuristics heuristics;
    private int depthLimit;
    private int timeLimit;
    private boolean compareOn;

    /**
     * Extracts a subarray of lines between the given start (inclusive) and end (exclusive).
     * Helping method for splitting map string.
     *
     * @param lines full array
     * @param start inclusive start index
     * @param end   exclusive end index
     * @return subarray of lines
     */
    private static String[] extractLines(String[] lines, int start, int end) {
        String[] result = new String[end - start];
        System.arraycopy(lines, start, result, 0, result.length);
        return result;
    }

    /**
     * Returns the map controller for the current game.
     *
     * @return the MapController instance
     */
    public MapController getCurrentMapController() {
        return current_gameState.mapController();
    }

    /**
     * Returns the game attributes of the current game state.
     *
     * @return the GameAttributes instance
     */
    public GameAttributes getCurrentGameAttributes() {
        return current_gameState.attributes();
    }

    /**
     * Returns the transition data used for the map.
     *
     * @return the Transitions instance
     */
    public Transitions getTransitions() {
        return transitions;
    }

    /**
     * Returns the current game state.
     *
     * @return the GameState instance
     */
    public GameState getCurrentGameState() {
        return current_gameState;
    }

    /**
     * Initializes the game state and attributes/objects held by the GameClient based on the given map input lines.
     * Extracts player, bomb, map, and transition information.
     *
     * @param lines the input map file content as an array of lines
     */
    public void init(String[] lines, boolean compare, int depthLimit) {
        int playerCount = Integer.parseInt(lines[0]);
        int overrideCount = Integer.parseInt(lines[1]);

        String[] bombInfo = lines[2].split(" ");
        int bombCount = Integer.parseInt(bombInfo[0]);
        int bombRadius = Integer.parseInt(bombInfo[1]);

        String[] mapInfo = lines[3].split(" ");
        int height = Integer.parseInt(mapInfo[0]);
        int width = Integer.parseInt(mapInfo[1]);

        String[] mapLines = extractLines(lines, 4, 4 + height);
        String[] transitionLines = extractLines(lines, 4 + height, lines.length);

        int gamePhase = 1;

        initTransitions(transitionLines, width, height);

        this.current_gameState = new GameState(
                new MapController(mapLines, transitions, width, height, bombRadius),
                new GameAttributes(playerCount, overrideCount, bombCount, bombRadius, gamePhase)
        );

        this.heuristics = new Heuristics(this.getCurrentGameState(), bombCount, overrideCount, playerCount);

        this.compareOn = compare;

        this.depthLimit = depthLimit;

        System.out.println("Initialization completed.");
    }

    /**
     * Initializes the transition relationships between map tiles.
     * Each line contains symmetric transitions that must be applied in both directions.
     *
     * @param transitionLines the array of transition string lines
     * @param width           map width
     * @param height          map height
     */
    public void initTransitions(String[] transitionLines, int width, int height) {
        this.transitions = new Transitions(width, height);

        for (String line : transitionLines) {
            String[] parts = line.split(" ");
            int fromX = Integer.parseInt(parts[0]);
            int fromY = Integer.parseInt(parts[1]);
            int fromDirec = Integer.parseInt(parts[2]);
            int toX = Integer.parseInt(parts[4]);
            int toY = Integer.parseInt(parts[5]);
            int toDirec = Integer.parseInt(parts[6]);

            transitions.setTransition(fromX, fromY, fromDirec, toX, toY, toDirec);
            transitions.setTransition(toX, toY, toDirec, fromX, fromY, fromDirec); //Bidirectional
        }
    }

    /**
     * Applies a move to the current game state.
     *
     * @param move the encoded move to be applied
     */
    public void updateGame(int move) {
        current_gameState.updateGameState(move);
    }

    /**
     * Sets our player ID given by the server.
     *
     * @param id our player ID
     */
    public void setOurPlayerId(int id) {
        current_gameState.attributes().setOurPlayerId(id);

        System.out.println("Player ID received: " + id);
    }

    /**
     * Marks a player as disqualified in the game state.
     *
     * @param playerId the ID of the disqualified player
     */
    public void playerDisqualified(byte playerId) {
        current_gameState.attributes().playerDisqualified(playerId);
    }

    /**
     * Configures the time and depth limits for move calculations.
     *
     * @param timeLimit the time limit in milliseconds
     * @param maxDepth  the maximum depth of search
     */
    public void setCalculateParameters(int timeLimit, byte maxDepth) {
        this.timeLimit = timeLimit;
        this.depthLimit = maxDepth;
    }

    /**
     * Computes and returns the best move with Alpha Beta Pruning (compareOn false) or with MiniMax AND Alpha Beta Pruning and compares their result (compareOn true).
     *
     * @return the encoded move
     */
    public int calculateMove() {
        if (compareOn) {

            MiniMax miniMax = new MiniMax(current_gameState, this.heuristics, depthLimit);
            int moveMiniMax = miniMax.chooseMove();

            SearchInfo info = new SearchInfo(Integer.MAX_VALUE, 0);

            AlphaBeta alphaBeta = new AlphaBeta(current_gameState, this.heuristics, depthLimit, info);
            int moveAlphaBeta = alphaBeta.chooseMove();

            if (moveMiniMax != moveAlphaBeta) {
                System.err.println("Different Results! MiniMax: " + moveMiniMax + " | AlphaBeta: " + moveAlphaBeta);
            }
            return moveAlphaBeta;

        } else {
            if (timeLimit == 0) { //case for only depth limit
                SearchInfo info = new SearchInfo(Integer.MAX_VALUE, 0);
                AlphaBeta alphaBeta = new AlphaBeta(current_gameState, this.heuristics, depthLimit, info);
                return alphaBeta.chooseMove();
            } else {  //case for only time limit
                return IterativeDeepening.findMove(timeLimit, this.heuristics, current_gameState);
            }


        }
    }

    /**
     * Switches the game to phase 2 when the server receives a message
     */
    public void endPhase() {
        current_gameState.attributes().setGamePhase(2);
    }

    /**
     * Is called when the server sends the game ending message
     */
    public void endGame() {
        // Possible additions for game evaluation?
    }

}
