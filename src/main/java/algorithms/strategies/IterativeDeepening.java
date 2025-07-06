package algorithms.strategies;

import java.util.List;

import control.Constants;
import gamestate.GameState;

/**
 * This class handles the iterative deepening strategies to calculate a
 * move under a given time limit
 */
public class IterativeDeepening {

    /**
     * This method does an iterative search depth deepening to find a move
     * and be able to return it in time
     *
     * @param timeLimit  The given time limit for this move request
     * @param heuristics The heuristics instance
     * @param state      The current game state
     * @return The computed move
     */
    public static int findMove(int timeLimit, Heuristics heuristics, GameState state, boolean moveSorting, boolean aspirationWindow) {

        long startTimeMS = System.nanoTime() / 1000000; // start time for estimation of left time
        int currentDepth = 1; // starting depth
        Result resultMove = new Result(0,0);
        Result posResultMove;
        int lower = Integer.MIN_VALUE;
        int higher = Integer.MAX_VALUE;

        SearchInfo info = new SearchInfo(timeLimit, startTimeMS);

        
        while (true) {

            long depthStart = System.nanoTime();
            System.gc(); // trigger the garbage collector manually so that we won't be interrupted during our computations
            
            // calculate a move with a certain depth via Alpha Beta Pruning
            AlphaBeta alphaBeta = new AlphaBeta(state, heuristics, currentDepth, info, moveSorting, lower, higher, aspirationWindow);
            posResultMove = alphaBeta.alphaBetaSearch();

            if(aspirationWindow && posResultMove != null && posResultMove.value() == -1) {

                lower = Integer.MIN_VALUE;
                higher = Integer.MAX_VALUE;
                depthStart = System.nanoTime();
                AlphaBeta alphaBeta2 = new AlphaBeta(state, heuristics, currentDepth, info, moveSorting, lower, higher, aspirationWindow);
                posResultMove = alphaBeta2.alphaBetaSearch();
            }

            long depthEnd = System.nanoTime();
            long duration = (depthEnd - depthStart) / 1000000;
            info.recordDepthTime(duration);

            if (info.getTimeout()) { // check if a timeout flag is set
                return resultMove.move();
            } else if(posResultMove.value() != -1){  
                resultMove = posResultMove;
            }

            //time prediction for the next depth and evaluation if the next iteration can be performed within the time limit.
            if (shouldStopSearch(info)) {
                break;
            }

            currentDepth++;

            if(resultMove.value() - Constants.ASPIRATION_WINDOW > resultMove.value()) {
                lower = Integer.MIN_VALUE;
            } else {
                lower = resultMove.value() - Constants.ASPIRATION_WINDOW;
            }
            
            if(resultMove.value() + Constants.ASPIRATION_WINDOW < resultMove.value()) {
                higher = Integer.MAX_VALUE;
            } else {
                higher = resultMove.value() + Constants.ASPIRATION_WINDOW;
            }
        }
        return resultMove.move();
    }

    /**
     * Determines whether the iterative deepening search should stop before attempting the next depth.
     * This method uses time and branching factor growth estimations to predict the required time
     * for the next depth and compares it with the remaining time to decide whether to abort further search.
     *
     * @param info the SearchInfo instance containing timing and branching factor data
     * @return true if the predicted search time exceeds the remaining allowed time, false otherwise.
     */
    private static boolean shouldStopSearch(SearchInfo info) {
        List<Long> times  = info.getDepthTimes();
        int size = times.size();

        //no prediction for depth 1
        if (size < 2) return false;

        long lastTime = times.get(size - 1);

        //ratio from last and predicted branching factor
        int next_children = estimateNextChildren(info);

        double avgTimePerNode = lastTime / (double) info.getNodes();

        //predicted next time considering the number of chuldren
        long predictedBFTime = (long) (next_children * avgTimePerNode) + lastTime;

        long currentTime = System.nanoTime() / 1_000_000;
        long timeUsed = currentTime - info.getStartTime();
        long timeLeft = info.getTimeLimit() - timeUsed;

        return predictedBFTime * Constants.TIMEBUFFER >= timeLeft;
    }

    /**
     * Estimates the number of children for the next depth level using recent historical values.
     * It calculates an average growth rate between the last and current branching factors and projects it forward.
     *
     * @param info the SearchInfo instance containing current and past branching factor information
     * @return an estimated branching factor for the next depth level.
     */
    private static int estimateNextChildren(SearchInfo info) {
        int parents = info.getNodes() - info.getNodesLastLvl() - 1;
        int childs = info.getNodes() - 1;

        double avgChild = (double) childs / (double) parents;
        return (int) avgChild * info.getNodesLastLvl();
    }
}
