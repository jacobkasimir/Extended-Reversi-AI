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
    public static int findMove(int timeLimit, Heuristics heuristics, GameState state, boolean moveSorting) {

        long startTimeMS = System.nanoTime() / 1000000; // start time for estimation of left time
        int currentDepth = 1; // starting depth
        Result resultMove = new Result(0,0);
        Result posResultMove;
        int lower = Integer.MIN_VALUE;
        int higher = Integer.MAX_VALUE;

        SearchInfo info = new SearchInfo(timeLimit, startTimeMS);

        System.gc(); // trigger the garbage collector manually so that we won't be interrupted during our computations
        while (true) {

            long depthStart = System.nanoTime();
            // calculate a move with a certain depth via Alpha Beta Pruning
            AlphaBetaNewVersion alphaBeta = new AlphaBetaNewVersion(state, heuristics, currentDepth, info, moveSorting, lower, higher);
            posResultMove = alphaBeta.alphaBetaSearch();

            if(posResultMove != null && posResultMove.value() == -1) {
                System.out.println("FAIL HIGH/LOW");
                lower = Integer.MIN_VALUE;
                higher = Integer.MAX_VALUE;
                depthStart = System.nanoTime();
                AlphaBetaNewVersion alphaBeta2 = new AlphaBetaNewVersion(state, heuristics, currentDepth, info, moveSorting, lower, higher);
                posResultMove = alphaBeta2.alphaBetaSearch();
            }

            long depthEnd = System.nanoTime();
            long duration = (depthEnd - depthStart) / 1000000;
            info.recordDepthTime(duration);

            if (info.getTimeout()) { // check if a timeout flag is set
                return resultMove.move();
            } else {
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
        //long predictedNextTime = getPredictedNextTime(times, size, lastTime);

        //ratio from last and predicted branching factor
        int next_bf = estimateNextBranchingFactor(info);

        double avgTimePerNode = lastTime / (double) info.getNodes();

        //predicted next time considering the branching factor
        long predictedBFTime = (long) (next_bf * avgTimePerNode) + lastTime;

        long currentTime = System.nanoTime() / 1_000_000;
        long timeUsed = currentTime - info.getStartTime();
        long timeLeft = info.getTimeLimit() - timeUsed;

        // safety buffer
        //long buffer = 50; // ms

        // return (predictedNextTime + predictedBFTime / 2.0) + buffer >= timeLeft;
        return predictedBFTime * Constants.TIMEBUFFER >= timeLeft;
    }

    /**
     * Predicts the time required to complete the next search depth based on recent depth timings.
     * Uses either a two-point or three-point average growth estimate depending on how many depth times are available.
     *
     * @param times    a list of recorded durations for completed search depths
     * @param size     the size of the list (number of recorded depths)
     * @param lastTime the duration of the most recent completed depth
     * @return an estimated duration for the next search depth.
     */
    private static long getPredictedNextTime(List<Long> times, int size, long lastTime) {
        long secondLastTime = times.get(size - 2);
        long predictedNextTime;

        if (size > 2) {
            long thirdLastTime = times.get(size - 3);
            double avg1 = (double) secondLastTime/ thirdLastTime;
            double avg2 = (double) lastTime / secondLastTime;
            double avgGrowth = (avg1 + avg2) / 2;
            predictedNextTime = (long) (lastTime * avgGrowth); //predicted time for the next depth
        }   else {
            predictedNextTime = lastTime * lastTime / secondLastTime; //predicted time for the next depth
        }
        return predictedNextTime;
    }

    /**
     * Estimates the branching factor for the next depth level using recent historical values.
     * It calculates an average growth rate between the last and current branching factors and projects it forward.
     *
     * @param info the SearchInfo instance containing current and past branching factor information
     * @return an estimated branching factor for the next depth level.
     */
    private static int estimateNextBranchingFactor(SearchInfo info) {
        int parents = info.getNodes() - info.getNodesLastLvl() - 1;
        int childs = info.getNodes() - 1;

        double avgChild = (double) childs / (double) parents;
        return (int) avgChild * info.getNodesLastLvl();
    }
}
