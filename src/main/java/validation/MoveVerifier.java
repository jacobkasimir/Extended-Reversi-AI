package validation;

import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import algorithms.datastructures.IntLinkedList;
import control.Coder;

/**
 * Verifies if a move is valid by using the individual rules from the GameRules.
 */
public class MoveVerifier {

    /**
     * Checks for each individual phase whether all rules regarding a single move
     * are satisfied.
     *
     * @param gameState GameState object holding a MapController and a
     *                  GameAttributes object.
     * @param move      Represents a move made by a player on the board.
     * @return true if the move is valid.
     */
    public static boolean isValid(int move, GameState gameState) {
        MapController map = gameState.mapController();
        GameAttributes attributes = gameState.attributes();

        if (!(GameRules.insideMap(move, map) && GameRules.validPlayer(move, attributes) &&
                GameRules.isNotHole(move, map) && GameRules.checkSpecial(attributes, move, map))) {
            return false;
        }

        if (attributes.isPhase1()) {
            if (GameRules.occupied(move, map)) {
                return (GameRules.hasOverride(attributes, move)
                        && (GameRules.capturing(move, map) || GameRules.isExpansion(map, move)));
            } else {
                return GameRules.capturing(move, map);
            }
        } else {
            return GameRules.hasBomb(attributes, move);
        }
    }

    /**
     * Calculates all possible Moves on a current game state for a certain player in
     * game phase 1.
     *
     * @param gameState GameState object holding a MapController and a
     *                  GameAttributes object.
     * @param playerId  The playerId of the player whose moves should be calculated.
     * @return All possible moves in a IntLinkedList.
     */
    public static IntLinkedList allMovesPhase1(GameState gameState, int playerId) {
        MapController map = gameState.mapController();
        GameAttributes attributes = gameState.attributes();
        IntLinkedList moves = new IntLinkedList();
        int width = map.getWidth();
        int height = map.getHeight();
        int numPlayers = attributes.getNumberPlayers();
        char tile;
        int move;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tile = map.getTile(x, y);

                switch (tile) {
                    // Bonus for both coices (20: Bomb, 21: Override)
                    case 'b' -> {
                        move = Coder.encodeMove(x, y, 20, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                            moves.add(move);
                        }
                        move = Coder.encodeMove(x, y, 21, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                            moves.add(move);
                        }
                    }
                    // Choice for every playerId
                    case 'c' -> {
                        for (int choice = 1; choice <= numPlayers; choice++) {
                            move = Coder.encodeMove(x, y, choice, playerId);
                            if (MoveVerifier.isValid(move, gameState)) {
                                moves.add(move);
                            }
                        }
                    }
                    // expansion stones, inversion tiles, normal tiles
                    default -> {
                        move = Coder.encodeMove(x, y, 0, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                            moves.add(move);
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Calculates all possible Moves on a current game state in game phase 2.
     *
     * @param gameState GameState object holding a MapController and a
     *                  GameAttributes object.
     * @return All possible moves in a IntLinkedList.
     */
    public static IntLinkedList allMovesPhase2(GameState gameState) {
        MapController map = gameState.mapController();
        GameAttributes attributes = gameState.attributes();

        IntLinkedList res = new IntLinkedList();
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.getTile(i, j) != '-'
                        && Coder.decodeplayerBombs(attributes.getPlayer(attributes.getOurPlayerId())) > 0) {
                    res.addIfNotContains(Coder.encodeMove(i, j, 0, attributes.getOurPlayerId()));
                }
            }
        }
        return res;
    }

    /**
     * Encapsulates the move calculating algorithmus for the different game phases
     * and is called by other methods to get all possible moves of a player on a
     * certain game state.
     *
     * @param gameState The current gameState.
     * @param playerId  The playerId to evaluate.
     * @return All possible moves in a IntLinkedList.
     */
    public static IntLinkedList calculateMoves(GameState gameState, int playerId) {
        if (Coder.decodeplayerInGame(gameState.attributes().getPlayer(playerId)) == 0) {
            return new IntLinkedList();
        }
        if (gameState.attributes().isPhase1()) {
            return MoveVerifier.allMovesPhase1(gameState, playerId);
        } else
            return allMovesPhase2(gameState);
    }

    /**
     * Encapsulates the move finding logic for the next player given a last playerId
     * considering disqualification, not being able to make a move.
     *
     * @param gameState    The current gameState.
     * @param lastPlayerId The playerId that has done the last move.
     * @return All possible moves of the next player in a IntLinkedList.
     */
    public static IntLinkedList calculateNextMoves(GameState gameState, int lastPlayerId) {
        lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers()) + 1;
        IntLinkedList res = MoveVerifier.calculateMoves(gameState, lastPlayerId);
        int counter = 1; // counts how many players have been checked

        // Iterates over players until someone does have moves left
        while (res.isEmpty() && counter < gameState.attributes().getNumberPlayers() + 1) {
            lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers()) + 1;
            res = MoveVerifier.calculateMoves(gameState, lastPlayerId);
            counter++;
        }
        // Handles logic if no player has moves left
        if (counter == gameState.attributes().getNumberPlayers()) {
            // Switch from phase 1 in 2
            if (gameState.attributes().isPhase1()) {
                gameState.attributes().setGamePhase(2);
                int nextPlayerId = lastPlayerId;
                if (nextPlayerId == 1) {
                    nextPlayerId = gameState.attributes().getNumberPlayers();
                } else {
                    nextPlayerId--;
                }
                return calculateNextMoves(gameState, nextPlayerId);
            } else { // End game logic
                return new IntLinkedList();
            }
        } else {
            return res;
        }
    }
}
