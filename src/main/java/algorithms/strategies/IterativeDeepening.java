package algorithms.strategies;
import control.Constants;
import gamestate.GameState;
import validation.MoveVerifier;

public class IterativeDeepening {

    /**
     * //TODO
     * 
     * @param timeLimit     the give time limit for this move request
     * @param heuristics    the heuristics instance
     * @param state         the current game state
     * @return              the computed move
     */
    public static int findMove(int timeLimit, Heuristics heuristics, GameState state){


        System.out.println("Erhaltenens timelimit: " + timeLimit + "ms."); //#TODO remove before uploading to matchpoint


        long startTime = System.nanoTime();

        System.gc();

        int currentDepth = 1;
        int resultMove = 0; 


        while (true) {


            //System.out.println("Aktuelle Tiefe: " + currentDepth); //#TODO remove before uploading to matchpoint

            System.gc();


            long startTimeAlphaBeta = System.nanoTime(); //start Timer for one iteration

            SearchInfo info = new SearchInfo(currentDepth);
            AlphaBeta alphaBeta = new AlphaBeta(state, heuristics, timeLimit, currentDepth, info);
            resultMove = alphaBeta.chooseMove();
            currentDepth++;


            long endTimeAlphaBeta = System.nanoTime(); //end timer for this iteration

            long duration = endTimeAlphaBeta - startTimeAlphaBeta;


            duration /= 1000000;

            int estimatedBranchingFactor = info.getMaxBranchingFactor(); //current estimation is the maximum in the entire tree

            estimatedBranchingFactor *= Constants.SAFETY_FOR_BRANCHING_FACTOR; //add a safety factor

            if(info.isBombPhaseReached()){
                estimatedBranchingFactor = (state.mapController().getHeight() * state.mapController().getWidth());
            } 

           
            long timeSinceStart = System.nanoTime() - startTime;

            long timeLeft = timeLimit - timeSinceStart/1000000;

            //System.out.println("time left: " + timeLeft); //#TODO Remove before uploading to Matchpoint

            
            if (duration * estimatedBranchingFactor + Constants.TIMELIMIT_SAFETY_OFFSET >= timeLeft) {
                break;
            }

        }

        long timeSinceStart = System.nanoTime() - startTime;

        long timeLeft = timeLimit - timeSinceStart/1000000;

        System.out.println("Vermutlich noch " + timeLeft + "ms übrig.");
        System.out.println("Nächstes Mal vermutliches timelimit: " + (timeLeft + 2000) + "ms.");

        return resultMove;

    }

}
