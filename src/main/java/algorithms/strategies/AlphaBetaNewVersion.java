package algorithms.strategies;

import java.util.ArrayList;
import java.util.Collections;

import algorithms.datastructures.IntLinkedList;
import control.Coder;
import control.Constants;
import gamestate.GameState;
import validation.MoveVerifier;

public class AlphaBetaNewVersion {

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

        player--;
        if (player == 0) player = currentGameState.attributes().getNumberPlayers();

        Result result = maxValue(currentGameState.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, player, searchdepth, -1); 
        
        if(result == null) return -1;
        else return result.move();        
    }


    public Result maxValue(GameState gameState, int alpha,int beta, int previousPlayer, int depth, int heuristicValue){
        //in case of a time-out
        if (searchInfo.isTimeout()) {
            return null;
        }
        long currentTime = System.nanoTime() / 1000000;
        if (currentTime - searchInfo.getStartTime() + Constants.TIMELIMIT_TIMEOUT_OFFSET >= searchInfo.getTimeLimit()) {
            searchInfo.setTimeout(true);
            return null;
        }

        //in case of no time-out
        if(depth <= 0){
            int value;
            if(heuristicValue != -1) value = heuristicValue;
            else value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            return new Result(value, -1);
        }
        ArrayList<Score> allMoves = new ArrayList<>();
        allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer,heuristics);
        
        if(allMoves.isEmpty()){ //Pseudo: is Terminal?
            return new Result(heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId()), -1);
        }

        if (Coder.decodePlayerId(allMoves.getFirst().move()) != gameState.attributes().getOurPlayerId()){
            int v = Integer.MAX_VALUE;
            int move = -1;

            if(moveSorting) Collections.sort(allMoves); // Aufsteigend nach score

            for(Score a: allMoves){
                Result result2 = maxValue(a.gameState().clone(), alpha, beta, Coder.decodePlayerId(a.move()), depth - 1, a.score());
                
                // case of a timeout
                if (searchInfo.isTimeout()) {
                    return null;
                }

                if (result2.value() < v) {
                    v = result2.value();
                    move = a.move();
                    
                    beta = Math.min(beta, v);
                }

                if (v <= alpha) {
                    return new Result(v, move);
                }
            }
            
            return new Result(v, move);
        }

        int v = Integer.MIN_VALUE;
        int move = -1;

        if(moveSorting) Collections.sort(allMoves,Collections.reverseOrder());

        for(Score a: allMoves){
            Result result2 = maxValue(a.gameState().clone(), alpha, beta, Coder.decodePlayerId(a.move()), depth - 1, a.score());

            // case of a timeout
            if (searchInfo.isTimeout()) {
                return null;
            }

            if (result2.value() > v) {
                v = result2.value();
                move = a.move();
                
                alpha = Math.max(alpha, v);
            }

            if (v >= beta) {
                return new Result(v, move);
            }
        }

        return new Result(v, move);
    }
}