import java.util.ArrayList;

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

    public static ArrayList<Move> allMovesPhase2(MapController controller, GameState state){
        ArrayList<Move> res = new ArrayList<>();
        for (int i = 0; i < controller.getWidth(); i++) {
            for (int j = 0; j < controller.getHeight(); j++) {
                if(controller.getTile(i, j) != '-' && state.getPlayer(state.getOurPlayerId()).getNumberBombs()>0){
                    res.add(new Move(i, j, 0, state.getOurPlayerId()));
                }
            }
        }
        return res;
    }

    public static ArrayList<Move> allMovesPhase1(MapController controller, GameState state){
        ArrayList<Move> res = new ArrayList<>();
        ArrayList<Coordinate> ourTiles = controller.getOurTiles();
        int ourId = state.getOurPlayerId();


        for(Coordinate c : ourTiles){
            int tilesPassed;
            boolean search;
            Position currentPosition;
            for(int i = 0; i < 8; i++) {
                currentPosition = new Position(c.x(), c.y(), i);
                search = true;
                tilesPassed = 0;
                while(search){
                    currentPosition = controller.getNextTile(currentPosition);
                    if (currentPosition == null) break;
                    int currentTile = controller.getTile(currentPosition.x(), currentPosition.y());
                    Move posMove = new Move(currentPosition.x(), currentPosition.y(), 0, ourId);
                    if(GameRules.isNotHole(posMove, controller) && GameRules.insideMap(posMove, controller)){
                        if(GameRules.occupied(posMove, controller)){
                            if((tilesPassed > 0 && GameRules.hasOverwrite(state, posMove)) || GameRules.isExpansion(controller, posMove)) {
                                res.add(posMove);
                                if(currentTile == 48 + ourId) search = false;
                                tilesPassed++;
                            }
                            if(currentTile == 48 + ourId) search = false;
                            tilesPassed++;
                        }else{
                            if(tilesPassed > 0 && currentTile == 'b'){
                                res.add(new Move(currentPosition.x(), currentPosition.y(), 20, ourId));
                                res.add(new Move(currentPosition.x(), currentPosition.y(), 21, ourId));
                            }else if(tilesPassed >0 && currentTile == 'c'){
                                for(int j = 1; i <= state.getNumberPlayers();j++){
                                    res.add(new Move(currentPosition.x(), currentPosition.y(), j, ourId));
                                }
                            }else{
                                if (tilesPassed > 0 && (c.x() != posMove.getX() || c.y() != posMove.getY())) res.add(posMove);
                            }
                            search = false;
                        }
                    }else{
                            search = false;
                        }
                }
            }
        }
        return res;
    }

}
