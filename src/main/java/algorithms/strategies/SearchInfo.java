package algorithms.strategies;

/**
 * Stores the necessary information required during the iterative deepening search process.
 */
public class SearchInfo {

    private final long startTime;
    private final int timeLimit;

    private boolean reachedBombPhase;
    private int maxBranchingFactor;
    private boolean timeout = false;

    /**
     * Creates a new SearchInfo Instance with the specified parameters.
     *
     * @param timeLimit the time limit given by the server
     * @param startTime the timestamp when iterative deepening begins
     */
    public SearchInfo(int timeLimit, long startTime) {
        this.timeLimit = timeLimit;
        this.startTime = startTime;
        reachedBombPhase = false;
        maxBranchingFactor = 0;
    }

    /**
     * Adds a new branching factor to the current instance.
     * Calls all further methods to update the different attributes.
     *
     * @param branching the branching factor that should be stored in the SearchInfo instance
     */
    public void addNewBranchingFactor(int branching) {
        updateMaxBranchingFactor(branching);
    }

    /**
     * Returns the maximum branching factor.
     *
     * @return the maximum branching factor in the search tree.
     */
    public int getMaxBranchingFactor() {
        return maxBranchingFactor;
    }

    /**
     * Updates the maximum branching factor by comparing it to the new one.
     *
     * @param branching the new branching factor to add
     */
    private void updateMaxBranchingFactor(int branching) {
        maxBranchingFactor = Math.max(maxBranchingFactor, branching);
    }

    /**
     * Returns if we reached the bombing phase (phase 2) with one branch in the search tree.
     *
     * @return True if we reached phase 2 with at least one branch in the search tree
     */
    public boolean isBombPhaseReached() {
        return reachedBombPhase;
    }

    /**
     * Marks that the bomb phase has been reached during the search process.
     */
    public void setReachedBombPhase() {
        reachedBombPhase = true;
    }

    /**
     * Returns whether the time limit has been exceeded and the recursive call should be aborted.
     *
     * @return True if the recursive call should be aborted.
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout flag indicating whether the time limit has been exceeded.
     *
     * @param timeout True if a timeout is reached
     */
    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the timestamp when iterative deepening begins.
     *
     * @return the timestamp when iterative deepening begins
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns the time limit given by the server.
     *
     * @return the time limit given by the server
     */
    public int getTimeLimit() {
        return timeLimit;
    }

}
