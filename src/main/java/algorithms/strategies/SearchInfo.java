package algorithms.strategies;

import control.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the necessary information required during the iterative deepening search process.
 */
public class SearchInfo {

    private final long startTime;
    private final int timeLimit;

    private int branchingFactor;
    private int lastBranchingFactor;
    private int secondLastBranchingFactor;

    private boolean timeout = false;

    private final List<Long> depthTimes = new ArrayList<>(); //Time per depth

    /**
     * Creates a new SearchInfo Instance with the specified parameters.
     *
     * @param timeLimit the time limit given by the server
     * @param startTime the timestamp when iterative deepening begins
     */
    public SearchInfo(int timeLimit, long startTime) {
        this.timeLimit = timeLimit;
        this.startTime = startTime;
        branchingFactor = 0;
    }

    /**
     * Returns the maximum branching factor.
     *
     * @return the maximum branching factor in the search tree.
     */
    public int getBranchingFactor() {
        return branchingFactor;
    }

    /**
     * Updates the maximum branching factor by comparing it to the new one.
     *
     * @param branching the new branching factor to add
     */
    public void updateBranchingFactor(int branching) {
        branchingFactor = Math.max(branchingFactor, branching);
    }

    /**
     * Updates the last and second last branching factors, for the prediction of the next branching factor.
     */
    public void updateLastBranchingFactor() {
        secondLastBranchingFactor = lastBranchingFactor;
        lastBranchingFactor = branchingFactor;
    }

    /**
     * Checks whether the time limit for the current search has been exceeded.
     * <p>
     * This method compares the current system time against the start time and the allowed time limit,
     * taking into account a small safety offset
     * If the limit is exceeded, it marks the instance as timed out.
     *
     * @return true, if the time limit has been exceeded or a timeout has already been flagged
     */
    public boolean isTimeout() {
        if (this.timeout) {
            return true;
        }
        long currentTime = System.nanoTime() / 1000000;
        if (currentTime - this.getStartTime() + Constants.TIMELIMIT_TIMEOUT_OFFSET >= this.getTimeLimit()) {
            this.setTimeout(true);
            return true;
        }
        return false;
    }

    /**
     * Returns whether a timeout has already been detected for this search.
     *
     * @return true if the timeout flag is set, false otherwise
     */
    public boolean getTimeout() {
        return this.timeout;
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

    /**
     * Records the time taken to complete the current search depth.
     *
     * @param duration The time in milliseconds taken for the current depth search.
     */
    public void recordDepthTime(long duration) {
        depthTimes.add(duration);
    }

    /**
     * Returns the list of recorded search times for each completed depth.
     *
     * @return A list containing the duration (in milliseconds) of each depth-level search.
     */
    public List<Long> getDepthTimes() {
        return depthTimes;
    }

    /**
     * Returns the branching factor of the most recently completed search depth.
     *
     * @return the last branching factor
     */
    public int getLastBranchingFactor() {
        return lastBranchingFactor;
    }

    /**
     * Returns the branching factor of the second most recently completed search depth.
     *
     * @return the second last branching factor
     */
    public int getSecondLastBranchingFactor() {
        return secondLastBranchingFactor;
    }

}
