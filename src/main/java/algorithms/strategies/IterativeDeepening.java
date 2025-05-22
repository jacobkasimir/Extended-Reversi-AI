package algorithms.strategies;
import gamestate.GameState;
import validation.MoveVerifier;

public class IterativeDeepening {

    /**
     * //TODO
     * 
     * @param timeLimit     the give time limit for this move request
     * @param heuristics    the heuristics instance
     * @param state         the current game state
     * @return              the choosen
     */
    public static int findMove(int timeLimit, Heuristics heuristics, GameState state){

        long startTime = System.nanoTime();

        System.gc();

        int currentDepth = 1;
        int resultMove = 0; 

        while (true) {

            System.out.println("Aktuelle Tiefe: " + currentDepth);

            long startTimeAlphaBeta = System.nanoTime();
            AlphaBeta alphaBeta = new AlphaBeta(state, heuristics, timeLimit, currentDepth);
            resultMove = alphaBeta.chooseMove();
            currentDepth++;
            long endTimeAlphaBeta = System.nanoTime();

            long duration = startTimeAlphaBeta - endTimeAlphaBeta;

            duration /= 1000000;
            
            int numberOfMovesDepthOne = MoveVerifier.calculateMoves(state, state.attributes().getOurPlayerId()).getSize();

            long timeSinceStart = startTime - System.nanoTime();

            long timeLeft = timeLimit - timeSinceStart/1000000;

            if (duration * numberOfMovesDepthOne >= timeLeft) {
                break;
            }

        }

        return resultMove;

    }

}
