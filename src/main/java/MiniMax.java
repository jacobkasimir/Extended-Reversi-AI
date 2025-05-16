import java.util.ArrayList;
/**
 * Implementation of the MiniMax algorithm.
 * This class enables the calculation of optimal game moves based on a given game state
 * using the MiniMax algorithm with a specified search depth.
 *
 * <p>The MiniMax algorithm works by building a tree of possible game states and evaluating them
 * using a heuristic function. It then selects the move that maximizes the player's advantage
 * while assuming the opponent will make optimal moves.</p>
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

    /** Maximum search time in milliseconds */
    private int searchTime;

    /**
     * Constructs a new MiniMax decision tree with the given game state and parameters.
     *
     * @param gameState    The current state of the game
     * @param heuristics   The heuristic used to evaluate game states
     * @param searchTime   The maximum time allowed for search in milliseconds
     * @param searchdepth  The maximum depth to search in the game tree
     */
    public MiniMax(GameState gameState, Heuristics heuristics, int searchTime, int searchdepth){
        this.moves = MoveVerifier.calculateMoves(gameState, gameState.attributes().getOurPlayerId());
        this.current_GameState = gameState;
        this.heuristics = heuristics;
        this.root = new Node(0, null, gameState.clone(), heuristics);
        this.searchdepth = searchdepth;
        this.searchTime = searchTime;

        ArrayList<Node> children = new ArrayList<>();

        ListNode m = moves.getHead();
        while (m != null){
            GameState newGameState = gameState.clone();
            //newGameState.updateGameState(m);
            children.add(new Node(m.getValue(), root, newGameState, heuristics));
            m = m.getNext();
        }
        root.setNextMoves(children);

    }



    public int chooseMove(){
        int maxValue = Integer.MIN_VALUE;
        int result = 0;
        for (Node child : root.getNextMoves()) {
            int childScore = child.dfs(searchdepth-1, current_GameState.attributes().getOurPlayerId());
            if(maxValue < childScore){
                result = child.getMove();
                maxValue = childScore;
            }
        }
        return result;
    }

}
