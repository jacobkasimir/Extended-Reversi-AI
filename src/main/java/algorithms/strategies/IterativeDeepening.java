package algorithms.strategies;

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
        int resultMove = 0;
        int posResultMove = 0;

        while (true) {

            System.gc(); // trigger the garbage collector manually so that we won't be interrupted during
            // our computations

            // calculate a move with a certain depth via Alpha Beta Pruning
            SearchInfo info = new SearchInfo(timeLimit, startTimeMS);
            AlphaBetaNewVersion alphaBetaNew = new AlphaBetaNewVersion(state, heuristics, currentDepth, info, moveSorting);
            posResultMove = alphaBetaNew.alphaBetaSearch();

            if (info.isTimeout()) { // check if a timeout flag is set
                return resultMove;
            } else {
                resultMove = posResultMove;
            }

            currentDepth++;

        }

    }

}
