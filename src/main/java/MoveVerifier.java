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
     * @param state GameAttributes object capturing the current phase, number of players, and the player instances.
     * @param move  Represents a move made by a player on the board.
     * @param map   Instance of the Map Controller.
     * @return true if the move is valid.
     */
    public static boolean isValid(Move move, MapController map, GameAttributes state){

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

    
    public static ArrayList<Move> allMovesPhase2(MapController controller, GameAttributes state){
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

    public static ArrayList<Move> allMovesPhase1(MapController controller, GameAttributes state, int playerId) {
        Set<Move> res = new LinkedHashSet<>();          // Damit keine Duplikate eingefügt werden (siehe overwrite von equals in Move)
        Set<Coordinate> expansions = controller.getExpansionTiles();

        //Tiefensuche für jeden unserer Steine in alle Richtungen
        for (Coordinate c : state.getPlayer(playerId).getTiles()) {

            for (int dir = 0; dir < 8; dir++) {
                Position pos = new Position(c.x(), c.y(), dir);
                int tilesPassed = 0;
                boolean search = true;
                while (search) {
                    pos = controller.getNextTile(pos);
                    if (pos == null) break;

                    Move move = new Move(pos.x(), pos.y(), 0, playerId);
                    if (!GameRules.isNotHole(move, controller) ||
                        !GameRules.insideMap(move, controller)) {
                        break;
                    }

                    char tile = (char)controller.getTile(pos.x(), pos.y());
                    boolean isOwn = tile == ('0' + playerId);

                    if (GameRules.occupied(move, controller)) {
                        if (//GameRules.isExpansion(controller, move) ||
                            (tilesPassed > 0 && GameRules.hasOverwrite(state, move) && c.x() != move.x() && c.y() != move.y())) {
                            res.add(move);
                        }
                        if (isOwn) search = false; //Damit bei eigenem Feld nicht hochgzählt wird
                        tilesPassed++;

                    } else {
                        // Bonusfelder beide Wahlen
                        if (tilesPassed > 0 && tile == 'b') {
                            res.add(new Move(pos.x(), pos.y(), 20, playerId));
                            res.add(new Move(pos.x(), pos.y(), 21, playerId));
                        }
                        // Choice-Felder alle Player-IDs
                        else if (tilesPassed > 0 && tile == 'c') {
                            for (int j = 1; j <= state.getNumberPlayers(); j++) {
                                res.add(new Move(pos.x(), pos.y(), j, playerId));
                            }
                        }
                        //Leeres Feld oder Expansion Feld + bereits occupied überlaufen
                        else if (tilesPassed > 0 && (c.x() != move.x() || c.y() != move.y())) {
                            res.add(move);
                        }
                        search = false;
                    }
                }
            }
        }

        //Alle Expansion stones sind mögliche Züge
        for(Coordinate e: expansions){
            if(GameRules.hasOverwrite(state, new Move(e.x(),e.y(),0,playerId))){
                res.add(new Move(e.x(), e.y(), 0, playerId));
            }
        }

        return new ArrayList<Move>(res);
    }

    public static ArrayList<Move> calculateAllMoves_Bruteforce(MapController controller, GameAttributes state) {

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


    /**
     * Calculates a valid move using a simple brutforce deterministic strategy.
     * @param gameState The current gameState.
     * @param playerId The playerId to evaluate.
     * @return A valid move or a dummy move if none found.
     */
    public static ArrayList<Move> calculateMoves(GameState gameState, int playerId) {
        // for(Coordinate c : this.gameAttributes.getPlayer(gameAttributes.getOurPlayerId()).tiles){
        //     System.out.println(c);
        // }
        if(!gameState.attributes().getPlayer(playerId).isInGame()){
            return new ArrayList<>();
        }
        if(gameState.attributes().isPhase1()){
            return MoveVerifier.allMovesPhase1(gameState.mapController(), gameState.attributes(), playerId);
        }
        else return MoveVerifier.allMovesPhase2(gameState.mapController(), gameState.attributes());
    }

    
    public static ArrayList<Move> calculateNextMoves(GameState gameState, int lastPlayerId){
        lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers())+1;
        ArrayList<Move> res = MoveVerifier.calculateMoves(gameState, lastPlayerId);
        int counter = 1;

        while(res.isEmpty() && counter < gameState.attributes().getNumberPlayers()+1){
            lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers())+1;
            res = MoveVerifier.calculateMoves(gameState, lastPlayerId);
            counter++;
        }
        if(counter == gameState.attributes().getNumberPlayers()){
            if(gameState.attributes().isPhase1()){
                gameState.attributes().setGamePhase((byte)2);
                int nextPlayerId = lastPlayerId;
                if(nextPlayerId == 1){
                    nextPlayerId = gameState.attributes().getNumberPlayers();
                }
                else{
                    nextPlayerId--;
                }
                return calculateNextMoves(gameState, nextPlayerId);
            }
            else{
                return null;
            }
        } else{
            return res;
        }
    }
}
