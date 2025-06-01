package algorithms.strategies;
import java.util.ArrayList;
import java.util.Collections;

import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
import control.Constants;
import gamestate.GameState;
import validation.MoveVerifier;

/**
 * Implementation of the Alpha-Beta pruning algorithm.
 * This class performs minimax search with alpha-beta pruning to determine 
 * the optimal move in a game state.
 * <p>
 * The algorithm evaluates possible moves up to a specified depth, using
 * heuristic evaluation to estimate positions beyond the search depth.
 */
public class AlphaBeta {
    /** Root node of the game tree */
    private final Node root;
    
    /** List of possible moves from the current position */
    private IntLinkedList moves;
    
    /** Current state of the game */
    private final GameState current_GameState;
    
    /** Heuristic evaluator used to assess game positions */
    private final Heuristics heuristics;
    
    /** Maximum depth of search in the game tree */
    private final int searchdepth;
    
    /** An object that holds all information for the optimal search time */
    private final SearchInfo info;

    /**
     * Constructs a new Alpha-Beta search instance.
     *
     * @param gameState The current state of the game
     * @param heuristics The heuristic evaluation function to use
     * @param searchTime Maximum time allowed for search in milliseconds
     * @param searchdepth Maximum depth to search in the game tree
     */
    public AlphaBeta(GameState gameState, Heuristics heuristics, int searchdepth, SearchInfo searchInfo){
        this.moves = MoveVerifier.calculateMoves(gameState, gameState.attributes().getOurPlayerId());
        this.current_GameState = gameState;
        this.heuristics = heuristics;
        this.root = new Node(0, null, gameState.clone(), heuristics);
        this.searchdepth = searchdepth;
        this.info = searchInfo;

        ArrayList<Node> children = new ArrayList<>();
        ListNode m = moves.getHead();

        //for each of our moves, do:
        while (m != null){
            //clone game state
            GameState newGameState = gameState.clone();

            //create nodes for each possible move, with the cloned game state
            children.add(new Node(m.getValue(), root, newGameState, heuristics));
            m = m.getNext();
        }

        Collections.sort(children);
        // add all nodes (possible states) to the root node
        root.setNextMoves(children);
        info.addNewBranchingFactor(children.size(), searchdepth);
    }

    

    /**
     * Performs the alpha-beta search and returns the best move found.
     *
     * <p>This method initiates the alpha-beta pruning algorithm from the root node,
     * evaluating all possible moves to the specified depth. It uses the minimax
     * principle with pruning to efficiently search the game tree.</p>
     *
     * @return The encoded move value representing the best move found
     */
    public int chooseMove(){
        int maxValue = Integer.MIN_VALUE;
        int result = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        // Iterate over all possible child nodes (moves) of the root node
        for (Node child : root.getNextMoves()) {
            // Perform alpha-beta pruning on this node and retrieve its score
            int childScore = child.pruning(searchdepth - 1, current_GameState.attributes().getOurPlayerId(), alpha, beta, info);

            //case of a timeout
            if(info.isTimeout()){
                return -1;
            }
            long currentTime = System.nanoTime()/1000000;
            if(currentTime-info.getStartTime() + Constants.TIMELIMIT_TIMEOUT >= info.getTimeLimit()){
                info.setTimeout(true);
                return -1;
            }
           
            
            // Update the result if the score is higher than the current maxValue
            if(maxValue < childScore) {
                result = child.getMove();
                maxValue = childScore;
            }
            // Update alpha and exit early if alpha is greater than or equal to beta
            alpha = Math.max(alpha, maxValue);
            if(beta <= alpha) {
                break;
            }
        }
        return result;
    }
}