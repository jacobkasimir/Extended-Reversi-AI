package algorithms.strategies;

import java.util.ArrayList;

public class SearchInfo {
    
    private int maxDepth;
    private boolean reachedBombPhase;
    private final long startTime;
    private final int timeLimit;

    public long getStartTime() {
        return startTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    private ArrayList<Integer> averageBranchingAtDepth; //? [1,2,3,4,5,5]
    private ArrayList<Integer> numberOfValuesAverageBranchingFactorAtDepth; //? 

    private int maxBranchingFactor;
    private int minBranchingFactor;
    private int overallAverageBranchingFactor;
    private int numberOfValuesAverageBranchingFactor;
    private boolean timeout = false;


    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public SearchInfo(int targetDepth, int timeLimit, long startTime){
        this.timeLimit = timeLimit;
        this.startTime = startTime;
        maxDepth = targetDepth;
        reachedBombPhase = false;
        maxBranchingFactor = 0;
        minBranchingFactor = Integer.MAX_VALUE;
        overallAverageBranchingFactor = 0;
        numberOfValuesAverageBranchingFactor = 0;
        averageBranchingAtDepth = new ArrayList<>(targetDepth); //muss evtl. angepasst werden, wenn man nicht für jede iteration ein neues objekt erstellen will
        numberOfValuesAverageBranchingFactorAtDepth = new ArrayList<>(targetDepth);
        for (int i = 0; i < targetDepth; i++) {
            averageBranchingAtDepth.add(0);
            numberOfValuesAverageBranchingFactorAtDepth.add(0);
        }
    }

    public void addNewBranchingFactor(int branching, int currentDepth){
        updateMaxBranchingFactor(branching);
        updateMinBranchingFactor(branching);
        updateOverallBranchingFactor(branching);
        updateAverageBranchingAtDepth(branching, currentDepth);
    }

    public int getAverageBranchingAtDepth(int depth){ //evtl. +1 für die depth, je nachdem wie bei uns definert
        if (depth != 0) {
            depth--;
        }
        return averageBranchingAtDepth.get(depth);
    }

    private void updateAverageBranchingAtDepth(int branching, int depth){
        if (depth != 0) {
            depth--;
        }
        int tmp = averageBranchingAtDepth.get(depth) * numberOfValuesAverageBranchingFactorAtDepth.get(depth);
        numberOfValuesAverageBranchingFactorAtDepth.set(depth, numberOfValuesAverageBranchingFactorAtDepth.get(depth) + 1);
        tmp += branching;
        averageBranchingAtDepth.set(depth, tmp / numberOfValuesAverageBranchingFactorAtDepth.get(depth));
    }
    
    public int getMaxBranchingFactor(){
        return maxBranchingFactor;
    }

    private void updateMaxBranchingFactor(int branching){ //evtl. wieder public machen,auch unten, aber klüger alles über eine methode -> addNew...()
        maxBranchingFactor = Math.max(maxBranchingFactor, branching);
    }

    public int getMinBranchingFactor(){
        return minBranchingFactor;
    }

    private void updateMinBranchingFactor(int branching){
        minBranchingFactor = Math.min(minBranchingFactor, branching);
    }

    public int getOverallBranchingFactor(){
        return overallAverageBranchingFactor;
    }

    private void updateOverallBranchingFactor(int branching){
        int tmp = overallAverageBranchingFactor * numberOfValuesAverageBranchingFactor;
        numberOfValuesAverageBranchingFactor++;
        tmp += branching;
        overallAverageBranchingFactor = tmp / numberOfValuesAverageBranchingFactor;
    }

    public boolean isBombPhaseReached(){
        return reachedBombPhase;
    }

    public void setReachedBombPhase(){
        reachedBombPhase = true;
    }

}
