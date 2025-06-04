package algorithms.strategies;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
import gamestate.GameState;
import validation.MoveVerifier;

public class AlphaBetaNewVersion {

    private final Node root;

    private final GameState currentGameState;

    private final Heuristics heuristics;

    private final int searchdepth;

    private final SearchInfo searchInfo;

    private IntLinkedList moves;

    private final boolean moveSorting;


    public AlphaBetaNewVersion(GameState gameState, Heuristics heuristics, int searchdepth, SearchInfo searchInfo, boolean sorting){
        this.currentGameState = gameState;
        this.heuristics = heuristics;
        this.searchdepth = searchdepth;
        this.searchInfo =searchInfo;
        this.moveSorting = sorting;

    }

    public int alphaBetaSearch(){
        int player = currentGameState.attributes().getOurPlayerId();

        Result result = maxValue(currentGameState.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, player);

        return result.move();        
    }

    public Result maxValue(GameState gameState, int alpha,int beta, int previousPlayer){

        IntLinkedList allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer);
        
        if(allMoves.isEmpty()){ //Pseudo: is Terminal?
            return new Result(heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId()), -1);
        }

        int v = Integer.MIN_VALUE;
        int move;

        ListNode a = allMoves.getHead();

        while(a!=null){
            Result result2 = minValue(gameState, alpha, beta, previousPlayer)
            
            if (result2.value() < v) {
                
            }

            a = a.getNext();
        }


        return;
    }


    public Result minValue(GameState gameState, int alpha,int beta, int previousPlayer){

    }


}
