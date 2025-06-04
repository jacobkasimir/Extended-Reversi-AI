package algorithms.strategies;

import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
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

        Result result = maxValue(currentGameState.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, player, searchdepth); 

        return result.move();        
    }








    public Result maxValue(GameState gameState, int alpha,int beta, int previousPlayer, int depth){

        if(depth <= 0){
            int value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            return new Result(value, -1);
        }

        IntLinkedList allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer);
        
        if(allMoves.isEmpty()){ //Pseudo: is Terminal?
            return new Result(heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId()), -1);
        }

        if (Coder.decodePlayerId(allMoves.getHead().getValue()) != gameState.attributes().getOurPlayerId()){
            int v = Integer.MAX_VALUE;
            int move = -1;

            ListNode a = allMoves.getHead();

            while(a!=null){
                GameState newGameState = gameState.clone();
                newGameState.updateGameState(a.getValue());
                Result result2 = maxValue(newGameState, alpha, beta, Coder.decodePlayerId(a.getValue()), depth - 1);
                
                if (result2.value() < v) {
                    v = result2.value();
                    move = a.getValue();
                    
                    beta = Math.min(beta, v);
                }

                if (v <= alpha) {
                    return new Result(v, move);
                }

                a = a.getNext();
            }
            
            return new Result(v, move);
        }

        int v = Integer.MIN_VALUE;
        int move = -1;

        ListNode a = allMoves.getHead();

        while(a!=null){
            GameState newGameState = gameState.clone();
            newGameState.updateGameState(a.getValue());
            Result result2 = minValue(newGameState, alpha, beta, Coder.decodePlayerId(a.getValue()), depth - 1);
            
            if (result2.value() > v) {
                v = result2.value();
                move = a.getValue();
                
                alpha = Math.max(alpha, v);
            }

            if (v >= beta) {
                return new Result(v, move);
            }

            a = a.getNext();
        }

        return new Result(v, move);
    }










    public Result minValue(GameState gameState, int alpha,int beta, int previousPlayer, int depth){
        
        if(depth <= 0){
            int value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
            return new Result(value, -1);
        }

        IntLinkedList allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer);
        
        if(allMoves.isEmpty()){ //Pseudo: is Terminal?
            return new Result(heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId()), -1);
        }


        if (Coder.decodePlayerId(allMoves.getHead().getValue()) == gameState.attributes().getOurPlayerId()) {
            
            int v = Integer.MIN_VALUE;
            int move = -1;

            ListNode a = allMoves.getHead();

            while(a!=null){
                GameState newGameState = gameState.clone();
                newGameState.updateGameState(a.getValue());
                Result result2 = minValue(newGameState, alpha, beta, Coder.decodePlayerId(a.getValue()), depth - 1);
                
                if (result2.value() > v) {
                    v = result2.value();
                    move = a.getValue();
                    
                    alpha = Math.max(alpha, v);
                }

                if (v >= beta) {
                    return new Result(v, move);
                }

                a = a.getNext();
            }

            return new Result(v, move);
        }



        int v = Integer.MAX_VALUE;
        int move = -1;

        ListNode a = allMoves.getHead();

        while(a!=null){
            GameState newGameState = gameState.clone();
            newGameState.updateGameState(a.getValue());
            Result result2 = maxValue(newGameState, alpha, beta, Coder.decodePlayerId(a.getValue()), depth - 1);
            
            if (result2.value() < v) {
                v = result2.value();
                move = a.getValue();
                
                beta = Math.min(beta, v);
            }

            if (v <= alpha) {
                return new Result(v, move);
            }

            a = a.getNext();
        }
        
        return new Result(v, move);
    }


}
