package algorithms.strategies;

import java.util.ArrayList;

import control.Coder;
import gamestate.GameState;
import validation.MoveVerifier;

/**
 * Represents a node in a game tree for implementing game AI algorithms like minimax.
 * Each node contains game state information and methods for tree traversal.
 */
public class Node implements Comparable<Node> {
    /**
     * The current game state at this node
     */
    private final GameState gameState;
    /**
     * The heuristics calculator
     */
    private final Heuristics heuristics;
    /**
     * The move associated with this node
     */
    private int move;
    /**
     * List of possible next moves from this position
     */
    private ArrayList<Node> nextMoves;
    /**
     * Reference to the parent node
     */
    private Node parent;
    /**
     * Heuristic score for this game state
     */
    private Integer score;


    public Node(int move, Node parent, GameState gameState, Heuristics heuristics, boolean moveSorting) {
        this.move = move;
        this.parent = parent;
        this.gameState = gameState;
        this.heuristics = heuristics;
        this.nextMoves = new ArrayList<>();
        if(moveSorting) this.score = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
    }


    /**
     * Getter function for the heuristic score of the game state.
     *
     * @return score
     */
    public int getScore() {
        return score;
    }


    /**
     * Get the move contained by this node.
     *
     * @return move.
     */
    public int getMove() {
        return move;
    }

    /**
     * Set move for an object of this class
     *
     * @param move contained by a node object
     */
    public void setMove(int move) {
        this.move = move;
    }

    /**
     * Retrieves the next possible moves from the current node in the game tree.
     *
     * @return An ArrayList of Node objects representing the next potential moves.
     */
    public ArrayList<Node> getNextMoves() {
        return nextMoves;
    }

    /**
     * Sets the list of next possible moves for this node in the game tree.
     *
     * @param nextMoves An ArrayList of Node objects representing the next potential moves.
     */
    public void setNextMoves(ArrayList<Node> nextMoves) {
        this.nextMoves = nextMoves;
    }

    /**
     * Retrieves the parent node of the current node in the game tree.
     *
     * @return The parent node of this node, or null if this node does not have a parent.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent node of the current node.
     *
     * @param parent The parent node to be assigned to this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }


    /**
     * Performs a depth-first search in the game tree implementing minimax algorithm.
     *
     * @param searchDepth  The maximum depth to search
     * @param lastPlayerId The ID of the player who made the last move
     * @return The heuristic value of the best move found
     */
    public int dfs(int searchDepth, int lastPlayerId) {
        if (searchDepth > 0) {
            //update the game state when performing the move "related" to this node
            gameState.updateGameState(this.move);

            //calculate all possible moves in this state
            ArrayList<Score> possibleMoves = MoveVerifier.calculateNextMoves(gameState, lastPlayerId,heuristics);

            //use the heuristics if no moves available
            if (possibleMoves.isEmpty()) {
                return heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            }

            //iterate over all possible moves in the current game state
            for(Score node:possibleMoves) {
                //create the next layer of the "mini-max"-tree
                this.nextMoves.add(new Node(node.move(), this, gameState.clone(), this.heuristics, false));
            }
            int result;

            int playerIdUpcomingMove = Coder.decodePlayerId(possibleMoves.getFirst().move());

            //decide if we perform the next move or one of our opponents:
            if (playerIdUpcomingMove == gameState.attributes().getOurPlayerId()) {   //our move
                result = Integer.MIN_VALUE;
                //iterate over all moves in the next layer
                for (Node child : nextMoves) {
                    //recursive call of depth-first search algorithm (with lower depth) and update result
                    result = Math.max(result, child.dfs(searchDepth - 1, Coder.decodePlayerId(this.move)));
                }
            } else {  //opponents move
                result = Integer.MAX_VALUE;
                //iterate over all moves in the deeper layer
                for (Node child : nextMoves) {
                    //recursive call of depth-first search algorithm (with lower depth) and update result
                    result = Math.min(result, child.dfs(searchDepth - 1, Coder.decodePlayerId(this.move)));
                }
            }
            this.nextMoves = null;
            //return result that is used in "AlphaBeta"-class
            return result;
        } else {

            //use heursitics if searchDepth is 0
            return heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
        }
    }

    
    @Override
    public int compareTo(Node node) {
        return this.score.compareTo(node.getScore());
    }
}