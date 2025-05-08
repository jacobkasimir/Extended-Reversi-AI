import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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

public static ArrayList<Move> allMovesPhase1(MapController controller, GameState state) {
    Set<Move> res = new LinkedHashSet<>();          // Damit keine Duplikate eingefügt werden (siehe overwrite von equals in Move)
    Set<Coordinate> ourTiles = controller.getOurTiles();
    Set<Coordinate> expansions = controller.getExpansionTiles();
    int ourId = state.getOurPlayerId();

    //Tiefensuche für jeden unserer Steine in alle Richtungen
    for (Coordinate c : ourTiles) {

        for (int dir = 0; dir < 8; dir++) {
            Position pos = new Position(c.x(), c.y(), dir);
            int tilesPassed = 0;
            boolean search = true;
            while (search) {
                pos = controller.getNextTile(pos);
                if (pos == null) break;

                Move move = new Move(pos.x(), pos.y(), 0, ourId);
                if (!GameRules.isNotHole(move, controller) ||
                    !GameRules.insideMap(move, controller)) {
                    break;
                }

                char tile = (char)controller.getTile(pos.x(), pos.y());
                boolean isOwn = tile == ('0' + ourId);

                if (GameRules.occupied(move, controller)) {
                    if (//GameRules.isExpansion(controller, move) ||
                        (tilesPassed > 0 && GameRules.hasOverwrite(state, move))) {
                        res.add(move);
                    }
                    if (isOwn) search = false; //Damit bei eigenem Feld nicht hochgzählt wird
                    tilesPassed++;

                } else {
                    // Bonusfelder beide Wahlen
                    if (tilesPassed > 0 && tile == 'b') {
                        res.add(new Move(pos.x(), pos.y(), 20, ourId));
                        res.add(new Move(pos.x(), pos.y(), 21, ourId));
                    }
                    // Choice-Felder alle Player-IDs
                    else if (tilesPassed > 0 && tile == 'c') {
                        for (int j = 1; j <= state.getNumberPlayers(); j++) {
                            res.add(new Move(pos.x(), pos.y(), j, ourId));
                        }
                    }
                    //Leeres Feld oder Expansion Feld + bereits occupied überlaufen
                    else if (tilesPassed > 0 && (c.x() != move.getX() || c.y() != move.getY())) {
                        res.add(move);
                    }
                    search = false;
                }
            }
        }
    }

    //Alle Expansion stones sind mögliche Züge
    for(Coordinate e: expansions){
        if(GameRules.hasOverwrite(state, new Move(e.x(),e.y(),0,ourId))){
            res.add(new Move(e.x(), e.y(), 0, ourId));
        }
    }

    return new ArrayList<Move>(res);
}

public static ArrayList<Move> calculateAllMoves_Bruteforce(MapController controller, GameState state) {

    Set<Move> moves = new LinkedHashSet<>();
    int width   = controller.getWidth();
    int height  = controller.getHeight();
    int ownId   = state.getOurPlayerId();
    int numPlayers = state.getNumberPlayers();

    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            char tile = (char) controller.getTile(x, y);

            //Bonus zwei für Bombe und Override
            if (tile == 'b') {
                Move mBomb = new Move(x, y, 20, ownId);
                if (MoveVerifier.isValid(mBomb, controller, state)) {
                    moves.add(mBomb);
                }
                Move mOverride = new Move(x, y, 21, ownId);
                if (MoveVerifier.isValid(mOverride, controller, state)) {
                    moves.add(mOverride);
                }
            }
            //Choice für jede Player-ID einmal
            else if (tile == 'c') {
                for (int choice = 1; choice <= numPlayers; choice++) {
                    Move m = new Move(x, y, choice, ownId);
                    if (MoveVerifier.isValid(m, controller, state)) {
                        moves.add(m);
                    }
                }
            }
            // Normales Feld, Expansion, Override 
            else {
                Move m = new Move(x, y, 0, ownId);
                if (MoveVerifier.isValid(m, controller, state)) {
                    moves.add(m);
                }
            }
        }
    }

    return new ArrayList<>(moves);
}


}
