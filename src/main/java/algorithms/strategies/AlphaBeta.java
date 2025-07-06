/*
 *  The implementation of this Alpha-Beta pruning algorithm is strictly oriented towards
 *  the pseudocode from the source provided (Russell, Norvig: Artificial intelligence. A modern approach. (Chapter: “Adversarial Search”)).
 */
package algorithms.strategies;

import java.util.ArrayList;
import java.util.Collections;

import control.Coder;
import gamestate.GameState;
import validation.MoveVerifier;


public class AlphaBeta {

    private final GameState currentGameState;

    private final Heuristics heuristics; // Heuristic used to evaluate game states

    private final SearchInfo searchInfo; // Information about the search process

    private final int searchdepth;

    private final boolean moveSorting;

    private final boolean aspirationWindow;

    private final int low;  // Lower bound for aspiration window, used in alpha-beta pruning
    private final int high; // Upper bound for aspiration window, used in alpha-beta pruning

     
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

    /**
     * Performs the alpha-beta search on the current game state. With or without aspiration window and with or without move sorting.
     * 
     * @return Result containing the best move and its heuristic value, or null if a timeout occurs.
     */
    public Result alphaBetaSearch(){
        int player = currentGameState.attributes().getOurPlayerId();
        searchInfo.resetCount();

        player--; // Find out the last player who made a move.
        if (player == 0) player = currentGameState.attributes().getNumberPlayers();

        Result result;
        // If aspiration window is used, the search is limited to the range [low, high].
        if(aspirationWindow){
            result = alphaBetaRecursive(currentGameState.clone(), low, high, player, searchdepth, -1);
        }else{ 
            result = alphaBetaRecursive(currentGameState.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, player, searchdepth, -1);
        }

        // If the result is null, it means a timeout occurred.
        if(result == null) return null;

        // If aspiration window is used and the result is outside the range [low, high], it means a fail high or fail low occured. 
        else if(aspirationWindow && result.value() < low || result.value() > high){
            return new Result(-1, 0);
        }
        else return result;        
    }


    /**
     * Recursive sub method for the alpha-beta pruning algorithm.
     * 
     * @param gameState The game state which is visited for this method call.
     * @param alpha The alpha value for pruning.
     * @param beta The beta value for pruning.
     * @param previousPlayer The ID of the player who made the last move.
     * @param depth The remaining depth to search.
     * @param heuristicValue The heuristic value of gameState, or -1 if not evaluated yet.
     * @return Result containing the best move and its heuristic value, or null if a timeout occurs.
     */
    private Result alphaBetaRecursive(GameState gameState, int alpha, int beta, int previousPlayer, int depth, int heuristicValue){
        
        // collect data for future time esimation
        searchInfo.addNode(); 

        //in case of a time-out
        if (searchInfo.isTimeout()) {
            return null;
        } else{ //in case of no time-out
            
            if(depth <= 0){ // if depth is 0, we evaluate the game state
                searchInfo.addNodesLastLvl();
                int value;
                if(heuristicValue != -1) value = heuristicValue; //case of a heuristic value already calculated
                else value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
                return new Result(value, -1);
            }

            ArrayList<Score> allMoves;
            allMoves = MoveVerifier.calculateNextMoves(gameState, previousPlayer, heuristics);
            
            if(allMoves.isEmpty()){ // final state reached
                int value;
                if(heuristicValue != -1) value = heuristicValue; //case of a heuristic value already calculated
                else value = heuristics.evaluate(gameState, gameState.attributes().getOurPlayerId());
                return new Result(value, -1);
            }

            // If the move is made by the opponent, we need to minimize the scores of the following game states.
            if (Coder.decodePlayerId(allMoves.getFirst().move()) != gameState.attributes().getOurPlayerId()){
                int v = Integer.MAX_VALUE;
                int move = -1;

                if(moveSorting) Collections.sort(allMoves); // sort the children in ascending order based on their heuristic score (smallest first)

                for(Score a: allMoves){

                    Result result2 = alphaBetaRecursive(a.gameState().clone(), alpha, beta, Coder.decodePlayerId(a.move()), depth - 1, a.score());
                    
                    // case of a timeout
                    if (searchInfo.getTimeout()) {
                        return null;
                    }

                    // If the value is less than the current minimum, update the minimum and the move.
                    if (result2.value() < v) {
                        v = result2.value();
                        move = a.move();
                        
                        beta = Math.min(beta, v);
                    }

                    // If the value is less than or equal to alpha, we can prune the search tree.
                    if (v <= alpha) {
                        return new Result(v, move);
                    } 
                }
                
                return new Result(v, move);

            } else { // If the move is made by us, we need to maximize the scores of the following game states.

                int v = Integer.MIN_VALUE;
                int move = -1;

                // sort the children in descending order based on their heuristic score (largest first)
                if(moveSorting) Collections.sort(allMoves,Collections.reverseOrder());

                for(Score a: allMoves){
                    Result result2 = alphaBetaRecursive(a.gameState().clone(), alpha, beta, Coder.decodePlayerId(a.move()), depth - 1, a.score());

                    // case of a timeout
                    if (searchInfo.getTimeout()) {
                        return null;
                    }

                    // If the value is greater than the current maximum, update the maximum and the move.
                    if (result2.value() > v) {
                        v = result2.value();
                        move = a.move();
                        
                        alpha = Math.max(alpha, v);
                    }

                    // If the value is greater than or equal to beta, we can prune the search tree.
                    if (v >= beta) {
                        return new Result(v, move);
                    }
                }

                return new Result(v, move);
            }
        }
    }
}