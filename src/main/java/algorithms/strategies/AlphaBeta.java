package algorithms.strategies;

import java.util.ArrayList;
import java.util.Collections;

import control.Coder;
import gamestate.GameState;
import validation.MoveVerifier;

public class AlphaBeta {

    private final GameState currentGameState;

    private final Heuristics heuristics;

    private final int searchdepth;

    private final SearchInfo searchInfo;

    private final boolean moveSorting;

    private final boolean aspirationWindow;

    private int low;
    private int high;

     
    public AlphaBeta(GameState gameState, Heuristics heuristics, int searchdepth, SearchInfo searchInfo, boolean sorting, int low, int high, boolean aspirationWindow) {
        this.currentGameState = gameState;
        this.heuristics = heuristics;
        this.searchdepth = searchdepth;
        this.searchInfo = searchInfo;
        this.moveSorting = sorting;
        this.aspirationWindow = aspirationWindow;

        this.low = low;
        this.high = high;   
    }

    public Result alphaBetaSearch(){
        int player = currentGameState.attributes().getOurPlayerId();
        searchInfo.resetCount();

        player--;
        if (player == 0) player = currentGameState.attributes().getNumberPlayers();

        Result result;
        if(aspirationWindow){
            result = maxValue(currentGameState.clone(), low, high, player, searchdepth, -1);
        }else{
            result = maxValue(currentGameState.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, player, searchdepth, -1);
        }

        if(result == null) return null;
        if(aspirationWindow && result.value() < low || result.value() > high){
            //System.out.println("FAIL HIGH/LOW: " + searchdepth + "heuristicValue: " + result.value());
            return new Result(-1, 0);
        }
        else return result;        
    }


    public Result maxValue(GameState gameState, int alpha, int beta, int previousPlayer, int depth, int heuristicValue){

        searchInfo.addNode();
        //in case of a time-out
        if (searchInfo.isTimeout()) {
            return null;
        }

        //in case of no time-out
        if(depth <= 0){
            searchInfo.addNodesLastLvl();
            int value;
            if(heuristicValue != -1) value = heuristicValue;
            else value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            return new Result(value, -1);
        }
        ArrayList<Score> allMoves;
        allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer,heuristics);
        
        if(allMoves.isEmpty()){ //Pseudo: is Terminal?

            int value;
            if(heuristicValue != -1) value = heuristicValue;
            else value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            return new Result(value, -1);
        }

        if (Coder.decodePlayerId(allMoves.getFirst().move()) != gameState.attributes().getOurPlayerId()){
            int v = Integer.MAX_VALUE;
            int move = -1;

            if(moveSorting) Collections.sort(allMoves); // ascending (score)

            for(Score a: allMoves){

                Result result2 = maxValue(a.gameState().clone(), alpha, beta, Coder.decodePlayerId(a.move()), depth - 1, a.score());

                //if(result2 != null && result2.value() == -1) return result2; vermutlich von vorher
                
                // case of a timeout
                if (searchInfo.getTimeout()) {
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

            //if(result2 != null && result2.value() == -1) return result2; vermmutlich von vorher

            // case of a timeout
            if (searchInfo.getTimeout()) {
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