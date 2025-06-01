package algorithms.strategies;

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
    public static int findMove(int timeLimit, Heuristics heuristics, GameState state) {

        long startTimeMS = System.nanoTime() / 1000000; // start time for estimation of left time

        int currentDepth = 1; // starting depth
        int resultMove = 0;
        int posResultMove = 0;

        while (true) {

            System.gc(); // trigger the garbage collector manually so that we won't be interrupted during
            // our computations

            long startTimeAlphaBetaMS = System.nanoTime() / 1000000; // start Timer for one iteration

            // calculate a move with a certain depth via Alpha Beta Pruning
            SearchInfo info = new SearchInfo(timeLimit, startTimeMS);
            AlphaBeta alphaBeta = new AlphaBeta(state, heuristics, currentDepth, info);
            posResultMove = alphaBeta.chooseMove();

            if (info.isTimeout()) { // check if a timeout flag is set
                return resultMove;
            } else {
                resultMove = posResultMove;
            }

            currentDepth++;

            long endTimeAlphaBetaMS = System.nanoTime() / 1000000; // end timer for this iteration

            long duration = endTimeAlphaBetaMS - startTimeAlphaBetaMS;

            double estimatedBranchingFactor = info.getMaxBranchingFactor(); // the current estimation is the maximum in the
            // entire tree

            estimatedBranchingFactor *= Constants.SAFETY_FOR_BRANCHING_FACTOR; // add a safety factor

            if (info.isBombPhaseReached()) {
                // estimate the branching factor to the map size for phase 2
                estimatedBranchingFactor = (state.mapController().getHeight() * state.mapController().getWidth());
            }

            long timeSinceStartMS = (System.nanoTime() / 1000000) - startTimeMS;

            long timeLeft = timeLimit - timeSinceStartMS; // used for estimation below

            if (duration * estimatedBranchingFactor + Constants.TIMELIMIT_SAFETY_OFFSET >= timeLeft) {
                break; // The estimated calculation time for finding the next move is too high
            }

        }

        return resultMove;

    }

}
