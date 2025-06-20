package algorithms.strategies;

import java.util.ArrayList;
import java.util.Collections;

import algorithms.datastructures.IntLinkedList;
import control.Coder;
import gamestate.GameState;
import validation.MoveVerifier;

public class AlphaBetaNewVersion {

    private final GameState currentGameState;

    private final Heuristics heuristics;

    private final int searchdepth;

    private final SearchInfo searchInfo;

    private IntLinkedList moves;

    private final boolean moveSorting;

    private int alpha;

    private int beta;


    public AlphaBetaNewVersion(GameState gameState, Heuristics heuristics, int searchdepth, SearchInfo searchInfo, boolean sorting, int low, int high){
        this.currentGameState = gameState;
        this.heuristics = heuristics;
        this.searchdepth = searchdepth;
        this.searchInfo =searchInfo;
        this.moveSorting = sorting;
        this.alpha = low;
        this.beta = high;
    }

    public Result alphaBetaSearch(){
        int player = currentGameState.attributes().getOurPlayerId();
        searchInfo.resetCount();

        player--;
        if (player == 0) player = currentGameState.attributes().getNumberPlayers();

        Result result = maxValue(currentGameState.clone(), player, searchdepth, -1);

        if(result == null) return null;
        else return result;        
    }


    public Result maxValue(GameState gameState, int previousPlayer, int depth, int heuristicValue){
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
            return new Result(heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId()), -1);
        }

        if (Coder.decodePlayerId(allMoves.getFirst().move()) != gameState.attributes().getOurPlayerId()){
            int v = Integer.MAX_VALUE;
            int move = -1;

            if(moveSorting) Collections.sort(allMoves); // ascending (score)

            for(Score a: allMoves){
                Result result2 = maxValue(a.gameState().clone(), Coder.decodePlayerId(a.move()), depth - 1, a.score());

                if(result2 != null && result2.value() == -1) return result2;
                
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
                } else {
                    return new Result(-1, 0);
                }
            }
            
            return new Result(v, move);
        }

        int v = Integer.MIN_VALUE;
        int move = -1;

        if(moveSorting) Collections.sort(allMoves,Collections.reverseOrder());

        for(Score a: allMoves){
            Result result2 = maxValue(a.gameState().clone(), Coder.decodePlayerId(a.move()), depth - 1, a.score());

            if(result2 != null && result2.value() == -1) return result2;

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
            } else {
                return new Result(-1, 0);
            }
        }

        return new Result(v, move);
    }
}