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
    public static boolean isValid(int move, MapController map, GameAttributes state){

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

    
    public static IntLinkedList allMovesPhase2(MapController controller, GameAttributes state){
        IntLinkedList res = new IntLinkedList();
        for (int i = 0; i < controller.getWidth(); i++) {
            for (int j = 0; j < controller.getHeight(); j++) {
                if(controller.getTile(i, j) != '-' && state.getPlayer(state.getOurPlayerId()).getNumberBombs()>0){
                    res.add(encodeMove(i, j, 0, state.getOurPlayerId()));
                }
            }
        }
        return res;
    }

    public static IntLinkedList allMovesPhase1(MapController controller, GameAttributes state, int playerId) {
        IntLinkedList res = new IntLinkedList();          // Damit keine Duplikate eingefügt werden (siehe overwrite von equals in Move)
        IntLinkedList expansions = controller.getExpansionTiles();
        IntLinkedList temp =  state.getPlayer(playerId).getTiles();
        ListNode node = temp.getHead();

        //Tiefensuche für jeden unserer Steine in alle Richtungen
        while(node != null ) {
            int x = decodeXCoords(node.getValue());
            int y = decodeYCoords(node.getValue());

            for (int dir = 0; dir < 8; dir++) {
                Position pos = new Position(x, y, dir);
                int tilesPassed = 0;
                boolean search = true;
                while (search) {
                    pos = controller.getNextTile(pos);
                    if (pos == null) break;

                    int move =  encodeMove(pos.x(), pos.y(), 0, playerId);
                    if (!GameRules.isNotHole(move, controller) ||
                        !GameRules.insideMap(move, controller)) {
                        break;
                    }

                    char tile = (char)controller.getTile(pos.x(), pos.y());
                    boolean isOwn = tile == ('0' + playerId);

                    if (GameRules.occupied(move, controller)) {
                        if (//GameRules.isExpansion(controller, move) ||
                            (tilesPassed > 0 && GameRules.hasOverwrite(state, move) && x != decodeX(move) && y != decodeY(move))) {
                            res.add(move); //auskommentiert für debugging //TODO
                        }
                        if (isOwn) search = false; //Damit bei eigenem Feld nicht hochgzählt wird
                        tilesPassed++;

                    } else {
                        // Bonusfelder beide Wahlen
                        if (tilesPassed > 0 && tile == 'b') {
                            res.add(encodeMove(pos.x(), pos.y(), 20, playerId));
                            res.add(encodeMove(pos.x(), pos.y(), 21, playerId));
                        }
                        // Choice-Felder alle Player-IDs
                        else if (tilesPassed > 0 && tile == 'c') {
                            for (int j = 1; j <= state.getNumberPlayers(); j++) {
                                res.add(encodeMove(pos.x(), pos.y(), j, playerId));
                            }
                        }
                        //Leeres Feld oder Expansion Feld + bereits occupied überlaufen
                        else if (tilesPassed > 0 && (x != decodeX(move) || y != decodeY(move))) {
                            res.add(move);
                        }
                        search = false;
                    }
                }
            }
            node = node.getNext();
        }

        //Alle Expansion stones sind mögliche Züge
        node = expansions.getHead();
        while (node != null){
            if(GameRules.hasOverwrite(state,encodeMove(decodeXCoords(node.getValue()),decodeYCoords(node.getValue()),0,playerId))){
                res.add(encodeMove(decodeXCoords(node.getValue()),decodeYCoords(node.getValue()), 0, playerId));
            }
            node = node.getNext();
        }

        return res;
    }

    public static IntLinkedList calculateAllMoves_Bruteforce(MapController controller, GameAttributes state, int playerId) {

        IntLinkedList moves = new IntLinkedList();
        int width   = controller.getWidth();
        int height  = controller.getHeight();
        int ownId   = state.getOurPlayerId();
        int numPlayers = state.getNumberPlayers();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char tile = (char) controller.getTile(x, y);

                //Bonus zwei für Bombe und Override
                if (tile == 'b') {
                    int mBomb = encodeMove(x, y, 20, playerId);
                    if (MoveVerifier.isValid(mBomb, controller, state)) {
                        moves.add(mBomb);
                    }
                    int mOverride = encodeMove(x, y, 21, playerId);
                    if (MoveVerifier.isValid(mOverride, controller, state)) {
                        moves.add(mOverride);
                    }
                }
                //Choice für jede Player-ID einmal
                else if (tile == 'c') {
                    for (int choice = 1; choice <= numPlayers; choice++) {
                        int m = encodeMove(x, y, choice, playerId);
                        if (MoveVerifier.isValid(m, controller, state)) {
                            moves.add(m);
                        }
                    }
                }
                // Normales Feld, Expansion, Override 
                else {
                    int m = encodeMove(x, y, 0, playerId);
                    if (MoveVerifier.isValid(m, controller, state)) {
                        moves.add(m);
                    }
                }
            }
        }

        return moves;
    }


    /**
     * Calculates a valid move using a simple brutforce deterministic strategy.
     * @param gameState The current gameState.
     * @param playerId The playerId to evaluate.
     * @return A valid move or a dummy move if none found.
     */
    public static IntLinkedList calculateMoves(GameState gameState, int playerId) {
        // for(Coordinate c : this.gameAttributes.getPlayer(gameAttributes.getOurPlayerId()).tiles){
        //     System.out.println(c);
        // }
        if(!gameState.attributes().getPlayer(playerId).isInGame()){
            return new IntLinkedList();
        }
        if(gameState.attributes().isPhase1()){
            return allMovesPhase1(gameState.mapController(), gameState.attributes(), playerId);
            //return MoveVerifier.calculateAllMoves_Bruteforce(gameState.mapController(), gameState.attributes(), playerId);
        }
        else return allMovesPhase2(gameState.mapController(), gameState.attributes());
    }

    
    public static IntLinkedList calculateNextMoves(GameState gameState, int lastPlayerId){
        lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers())+1;
        IntLinkedList res = MoveVerifier.calculateMoves(gameState, lastPlayerId);
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

    public static int encodeCoordinate(int xCoord, int yCoord) {
        return xCoord * 51 + yCoord; 
    }

    public static int encodeMove(int x, int y, int special, int playerId) {
        return ((x * 51 + y) * 51 + special) * 51 + playerId;
    }

    public static int decodeXCoords(int value) {
    return value / 51;
    }

    public static int decodeYCoords(int value) {
        return value % 51;
    }

    public static int decodePlayerId(int value) {
        return value % 51;
    }

    public static int decodeSpecial(int value) {
        value /= 51;
        return value % 51;
    }

    public static int decodeY(int value) {
        value /= 51 * 51;
        return value % 51;
    }

    public static int decodeX(int value) {
        value /= 51 * 51 * 51;
        return value;
    }

}
