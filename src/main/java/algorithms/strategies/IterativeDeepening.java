package algorithms.strategies;

import gamestate.GameState;

import java.util.List;

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
        int resultMove = 0;
        int posResultMove;

        SearchInfo info = new SearchInfo(timeLimit, startTimeMS);

        System.gc(); // trigger the garbage collector manually so that we won't be interrupted during our computations
        while (true) {

            long depthStart = System.nanoTime();
            // calculate a move with a certain depth via Alpha Beta Pruning
            AlphaBetaNewVersion alphaBeta = new AlphaBetaNewVersion(state, heuristics, currentDepth, info, moveSorting);
            info.updateLastBranchingFactor(); //for further calculations
            posResultMove = alphaBeta.alphaBetaSearch();

            long depthEnd = System.nanoTime();
            long duration = (depthEnd - depthStart) / 1000000;
            info.recordDepthTime(duration);

            if (info.getTimeout()) { // check if a timeout flag is set
                return resultMove;
            } else {
                resultMove = posResultMove;
            }

            //time prediction for the next depth and evaluation if the next iteration can be performed within the time limit.
            if (shouldStopSearch(info)) {
                break;
            }

            currentDepth++;

        }
        return resultMove;
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
        long predictedNextTime = getPredictedNextTime(times, size, lastTime);


        int bf = info.getBranchingFactor();
        int next_bf = estimateNextBranchingFactor(info);

        //ratio from last and predicted branching factor
        double factor = (double) next_bf / bf;
        //predicted next time considering the branching factor
        long predictedBFTime = (long) (lastTime * factor * 1.1); //10 % safety buffer

        long currentTime = System.nanoTime() / 1_000_000;
        long timeUsed = currentTime - info.getStartTime();
        long timeLeft = info.getTimeLimit() - timeUsed;

        // safety buffer
        long buffer = 50; // ms

        return (predictedNextTime + predictedBFTime / 2.0) + buffer >= timeLeft;
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
        int bf = info.getBranchingFactor();
        int last_bf = info.getLastBranchingFactor();
        int secondLast_bf = info.getSecondLastBranchingFactor();

        if (last_bf == 0) return 0;
        else if (secondLast_bf == 0) return bf * bf / last_bf;
        else {
            double avg1 = (double) last_bf / secondLast_bf;
            double avg2 = (double) bf / last_bf;
            double avgGrowth = (avg1 + avg2) / 2;
            return (int) (bf * avgGrowth);
        }
    }
}
