import java.util.ArrayList;

public class AlphaBeta {
    private Node root;
    private ArrayList<Move> moves;
    private GameState current_GameState;
    private Heuristics heuristics;
    private int searchdepth;
    private int searchTime;


    public AlphaBeta(GameState gameState, Heuristics heuristics, int searchTime, int searchdepth){
        this.moves = MoveVerifier.calculateMoves(gameState, gameState.attributes().getOurPlayerId());
        this.current_GameState = gameState;
        this.heuristics = heuristics;
        this.root = new Node(null, null, gameState.clone(), heuristics);
        this.searchdepth = searchdepth;
        this.searchTime = searchTime;

        ArrayList<Node> children = new ArrayList<>();
        for(Move m : moves){
            GameState newGameState = gameState.clone();
            children.add(new Node(m, root, newGameState, heuristics));
        }
        root.setNextMoves(children);
    }

    

    public Move chooseMove(){
        int maxValue = Integer.MIN_VALUE;
        Move result = null;
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
