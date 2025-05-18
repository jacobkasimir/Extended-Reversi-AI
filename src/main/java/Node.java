import java.util.ArrayList;
/**
 * Represents a node in a game tree for implementing game AI algorithms like minimax.
 * Each node contains game state information and methods for tree traversal.
 */
public class Node {
    /** The heuristic value of this node */
    private int heuristicValue;
    /** The move associated with this node */
    private int move;
    /** The current game state at this node */
    private GameState gameState;
    /** The heuristics calculator */
    private Heuristics heuristics;
    /** List of possible next moves from this position */
    private ArrayList<Node> nextMoves;
    /** Reference to the parent node */
    private Node parent;
    
    /**
     * Creates a new Node with the specified parameters.
     *
     * @param move       The move value associated with this node
     * @param parent     The parent node in the game tree
     * @param gameState  The game state at this node
     * @param heuristics The heuristics calculator to evaluate positions
     */
    public Node(int move, Node parent, GameState gameState, Heuristics heuristics){
        this.move = move;
        this.parent = parent;
        this.gameState = gameState;
        this.heuristics = heuristics;
        this.nextMoves = new ArrayList<Node>();
    }

    // Getter and setter methods ...

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public ArrayList<Node> getNextMoves() {
        return nextMoves;
    }

    public void setNextMoves(ArrayList<Node> nextMoves) {
        this.nextMoves = nextMoves;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    
    /**
     * Performs a depth-first search in the game tree implementing minimax algorithm.
     *
     * @param searchDepth   The maximum depth to search
     * @param lastPlayerId  The ID of the player who made the last move
     * @return             The heuristic value of the best move found
     */
    public int dfs(int searchDepth, int lastPlayerId){
        if(searchDepth > 0){
            gameState.updateGameState(this.move);
            IntLinkedList possibleMoves = MoveVerifier.calculateNextMoves(gameState, lastPlayerId);
            if(possibleMoves == null) {
                return heuristics.evaluate(gameState.mapController(), gameState.attributes(), lastPlayerId);
            }

            ListNode node = possibleMoves.getHead();

            while (node != null) {
                this.nextMoves.add(new Node(node.getValue(), this, gameState.clone(), this.heuristics));
                node = node.getNext();
            }
            int result;
            if(MoveVerifier.decodePlayerId(move) == gameState.attributes().getOurPlayerId()){
                result = Integer.MAX_VALUE;
                for (Node child : nextMoves) {
                    result = Math.min(result, child.dfs(searchDepth-1, MoveVerifier.decodePlayerId(this.move)));
                    if(result == Integer.MAX_VALUE){
                        break;
                    }
                }
            }else{
                result = Integer.MIN_VALUE;
                for (Node child : nextMoves) {
                    result = Math.max(result, child.dfs(searchDepth-1, MoveVerifier.decodePlayerId(this.move)));
                }
            }
            this.nextMoves = null;
            return result;      
        }else{
            return heuristics.evaluate(gameState.mapController(), gameState.attributes(), lastPlayerId);
        }
    }

    /**
     * Performs alpha-beta pruning search in the game tree, an optimized version of minimax algorithm.
     *
     * @param searchDepth   The maximum depth to search
     * @param lastPlayerId  The ID of the player who made the last move
     * @param alpha        The alpha value for pruning
     * @param beta         The beta value for pruning
     * @return             The heuristic value of the best move found
     */
    public int pruning(int searchDepth, int lastPlayerId, int alpha, int beta){
        if(searchDepth > 0){
            gameState.updateGameState(this.move);
            IntLinkedList possibleMoves = MoveVerifier.calculateNextMoves(gameState, lastPlayerId);
            if(possibleMoves == null) {
                return heuristics.evaluate(gameState.mapController(), gameState.attributes(), lastPlayerId);
            }

            ListNode m = possibleMoves.getHead();
            while (m != null){
                this.nextMoves.add(new Node(m.getValue(), this, gameState.clone(), this.heuristics));
                m = m.getNext();
            }
            int result;
            if(MoveVerifier.decodePlayerId(move) == gameState.attributes().getOurPlayerId()){
                result = Integer.MAX_VALUE;
                for (Node child : nextMoves) {
                    result = Math.min(result, child.pruning(searchDepth-1, MoveVerifier.decodePlayerId(this.move), alpha, beta));
                    beta = Math.min(beta, result);
                    if(beta <= alpha){
                        break;
                    }
                }
            }else{
                result = Integer.MIN_VALUE;
                for (Node child : nextMoves) {
                    result = Math.max(result, child.pruning(searchDepth-1, MoveVerifier.decodePlayerId(this.move), alpha, beta));
                    alpha = Math.max(alpha, result);
                    if(beta <= alpha){
                        break;
                    }
                }
            }
            this.nextMoves = null;
            return result;      
        }else{
            return heuristics.evaluate(gameState.mapController(), gameState.attributes(), lastPlayerIds);
        }
    }
}