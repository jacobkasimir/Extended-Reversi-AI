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

        long startTime = System.nanoTime();

        System.gc();

        int currentDepth = 1;
        int resultMove = 0; 


        //trying to estimate the branching factor by taking the average of all players
        int total = 0;
        int playersInGame = 0;
        for(int p = 1; p <= state.attributes().getNumberPlayers(); p++){
            if (state.attributes().getPlayer(p).isInGame()) {
                playersInGame++;
                total += MoveVerifier.calculateMoves(state, p).getSize();
            }
        }

        //int estimatedBranchingFactor = total / playersInGame;

        while (true) {

            System.out.println("Aktuelle Tiefe: " + currentDepth);

            long startTimeAlphaBeta = System.nanoTime(); //start Timer for one iteration
            SearchInfo info = new SearchInfo(currentDepth);
            AlphaBeta alphaBeta = new AlphaBeta(state, heuristics, timeLimit, currentDepth, info);
            resultMove = alphaBeta.chooseMove();
            currentDepth++;
            long endTimeAlphaBeta = System.nanoTime(); //end timer for this iteration

            long duration = endTimeAlphaBeta - startTimeAlphaBeta;

            // System.out.println("Duration " + duration);

            duration /= 1000000;

            int estimatedBranchingFactor = info.getAverageBranchingAtDepth(0);

            estimatedBranchingFactor *= Constants.SAFETY_FOR_BRANCHING_FACTOR; //safety

            if(info.isBombPhaseReached()) estimatedBranchingFactor = (state.mapController().getHeight() * state.mapController().getWidth());

            System.out.println("Estimated Branching Factor: " + estimatedBranchingFactor);

            //old version
            // int numberOfMovesDepthOne = MoveVerifier.calculateMoves(state, state.attributes().getOurPlayerId()).getSize();
            
            // System.out.println("NumberOfMoves: " + numberOfMovesDepthOne);

            long timeSinceStart = System.nanoTime() - startTime;

            long timeLeft = timeLimit - timeSinceStart/1000000;

            System.out.println("time left: " + timeLeft);

            

            //vorher: ...*numberOfMovesDepthOne
            if (duration * estimatedBranchingFactor >= timeLeft) {
                break;
            }

        }

        return resultMove;

    }

    //empty tiles as a lower bound to estimate when bombing phase starts
    //not usable -> acual no lower bound -> edge cases, e.g. pikachu
    private int emptyTiles(){
        return 0; //TODO
    }

    private boolean bombingInNextMoves(int depth){

        

        return false; //TODO
    }


}
