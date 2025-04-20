/**
 * Verifies if a move is valid by using the individual rules from the GameRules.
 */
public class MoveVerifier {

    /**
     * Checks for each individual phase whether all rules regarding a single move are satisfied.
     *
     * @param state GameState object capturing the current phase, number of players, and the player instances.
     * @param move  Represents a move made by a player on the board.
     * @param map   Instance of the Map Controller.
     * @return true if the move is valid.
     */
    public static boolean isValid(Move move, MapController map, GameState state){

        if(!(GameRules.insideMap(move, map) &&
        GameRules.validMove(move, state) &&
        GameRules.isNotHole(move, map) && GameRules.checkSpecial(state, move, map))) {
            return false;
        }

        if(state.isPhase1()){
            if(GameRules.occupied(move, map)) {
                return (GameRules.hasOverwrite(state, move) && (GameRules.capturing(move, map) || GameRules.isExpansion(map, move)));
            } else {
                return GameRules.capturing(move, map);
            }
        }else{
            return GameRules.hasBomb(state, move);
        }
    }
}
