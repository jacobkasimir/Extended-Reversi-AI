package algorithms.strategies;

import java.util.ArrayList;

import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
import gamestate.GameState;
import validation.MoveVerifier;
/**
 * Implementation of the MiniMax algorithm.
 * This class enables the calculation of optimal game moves based on a given game state
 * using the MiniMax algorithm with a specified search depth.
 *
 * <p>The MiniMax algorithm works by building a tree of possible game states.
 * It then selects the move that maximizes the player's advantage
 * while assuming the opponents will make optimal moves.</p>
 */
public class MiniMax {
    /** The root node of the game tree */
    private Node root;

    /** List of possible moves */
    private IntLinkedList moves;

    /** Current state of the game */
    private GameState current_GameState;

    /** Heuristic used to evaluate game states */
    private Heuristics heuristics;

    /** Maximum search depth for the MiniMax algorithm */
    private final int searchdepth;


    /**
     * Constructs a new MiniMax decision tree with the given game state and parameters.
     *
     * @param gameState    The current state of the game
     * @param heuristics   The heuristic used to evaluate game states
     * @param searchTime   The maximum time allowed for search in milliseconds
     * @param searchdepth  The maximum depth to search in the game tree
     */
    public MiniMax(GameState gameState, Heuristics heuristics, int searchdepth){
        this.moves = MoveVerifier.calculateMoves(gameState, gameState.attributes().getOurPlayerId());
        this.current_GameState = gameState;
        this.heuristics = heuristics;
        //create the root node for the branched tree of possible moves (or game states)
        this.root = new Node(0, null, gameState.clone(), heuristics);
        this.searchdepth = searchdepth;

        //should store all children (possible game states) of the root node
        ArrayList<Node> children = new ArrayList<>();

        //iterate over all possible moves
        ListNode m = moves.getHead();
        while (m != null){
            //clone game state
            GameState newGameState = gameState.clone();

            //add node for the game state when performing move m
            children.add(new Node(m.getValue(), root, newGameState, heuristics));
            m = m.getNext();
        }

        //add all nodes to the root node
        root.setNextMoves(children);

    }



    public int chooseMove(){

        //initialize maxValue and move (result)
        double maxValue = Integer.MIN_VALUE;
        int result = 0;

        //iterate over all children of the root
        for (Node child : root.getNextMoves()) {

            //calculate score of current node, by using the DFS-algorithm in class "Node"
            double childScore = child.dfs(searchdepth-1, current_GameState.attributes().getOurPlayerId());

            //update the move and maxValue if the score is higher than the current maxValue
            if(maxValue < childScore){
                result = child.getMove();
                maxValue = childScore;
            }
        }
        return result;
    }

}
