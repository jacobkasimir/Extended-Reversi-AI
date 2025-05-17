
import java.util.ArrayList;

public class Node {
    private int heuristicValue;
    private int move;
    private GameState gameState;
    private Heuristics heuristics;

    private ArrayList<Node> nextMoves;
    private Node parent;
    
    public Node(int move, Node parent, GameState gameState, Heuristics heuristics){
        this.move = move;
        this.parent = parent;
        this.gameState = gameState;
        this.heuristics = heuristics;
        this.nextMoves = new ArrayList<Node>();
    }

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

    
    public int dfs(int searchDepth, int lastPlayerId){
        if(searchDepth > 0){
            gameState.updateGameState(this.move);
            IntLinkedList possibleMoves = MoveVerifier.calculateNextMoves(gameState, lastPlayerId);
            if(possibleMoves.isEmpty()) {
                return heuristics.evaluate(gameState);
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
            return heuristics.evaluate(gameState);
        }
    }

    public int pruning(int searchDepth, int lastPlayerId, int alpha, int beta){
        if(searchDepth > 0){
            gameState.updateGameState(this.move);
            IntLinkedList possibleMoves = MoveVerifier.calculateNextMoves(gameState, lastPlayerId);
            if(possibleMoves.isEmpty()) {
                return heuristics.evaluate(gameState);
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
            return heuristics.evaluate(gameState);
        }
    }
}
