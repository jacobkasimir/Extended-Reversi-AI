package validation;

import java.util.ArrayList;

import algorithms.strategies.Heuristics;
import algorithms.strategies.Score;
import control.Coder;
import control.Constants;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;

/**
 * Verifies if a move is valid by using the individual rules from the GameRules.
 */
public class MoveVerifier {

    /**
     * Checks for each phase whether all rules regarding a single move
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
     * @return All possible moves in an IntLinkedList.
     */
    public static ArrayList<Score> allMovesPhase1(GameState gameState, int playerId, Heuristics heuristics) {
        MapController map = gameState.mapController();
        GameAttributes attributes = gameState.attributes();
        ArrayList<Score> moves = new ArrayList<>();
        int width = map.getWidth();
        int height = map.getHeight();
        int numPlayers = attributes.getNumberPlayers();
        char tile;
        int move;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tile = map.getTile(x, y);

                switch (tile) {
                    // Bonus for both choices 
                    case 'b' -> {
                        move = Coder.encodeMove(x, y, Constants.BOMB, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                                GameState toEvalGameState = gameState.clone();
                                toEvalGameState.updateGameState(move);
                                Score score = new Score(move,heuristics.evaluate(toEvalGameState, gameState.attributes().getOurPlayerId()),toEvalGameState);
                                moves.add(score);
                        }
                        move = Coder.encodeMove(x, y, Constants.OVERRIDE, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                                GameState toEvalGameState = gameState.clone();
                                toEvalGameState.updateGameState(move);
                                Score score = new Score(move,heuristics.evaluate(toEvalGameState, gameState.attributes().getOurPlayerId()),toEvalGameState);
                                moves.add(score);
                        }
                    }
                    // Choice for every playerId
                    case 'c' -> {
                        for (int choice = 1; choice <= numPlayers; choice++) {
                            move = Coder.encodeMove(x, y, choice, playerId);
                            if (MoveVerifier.isValid(move, gameState)) {
                                GameState toEvalGameState = gameState.clone();
                                toEvalGameState.updateGameState(move);
                                Score score = new Score(move,heuristics.evaluate(toEvalGameState, gameState.attributes().getOurPlayerId()),toEvalGameState);
                                moves.add(score);
                            }
                        }
                    }
                    // expansion stones, inversion tiles, normal tiles
                    default -> {
                        move = Coder.encodeMove(x, y, 0, playerId);
                        if (MoveVerifier.isValid(move, gameState)) {
                            GameState toEvalGameState = gameState.clone();
                            toEvalGameState.updateGameState(move);
                            Score score = new Score(move,heuristics.evaluate(toEvalGameState, gameState.attributes().getOurPlayerId()),toEvalGameState);
                            moves.add(score);
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
     * @return All possible moves in an IntLinkedList.
     */
    public static ArrayList<Score> allMovesPhase2(GameState gameState, Heuristics heuristics) {
        MapController map = gameState.mapController();
        GameAttributes attributes = gameState.attributes();

        ArrayList<Score> res = new ArrayList<>();
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.getTile(i, j) != '-'
                        && Coder.decodeplayerBombs(attributes.getPlayer(attributes.getOurPlayerId())) > 0) {
                    int move = Coder.encodeMove(i, j, 0, attributes.getOurPlayerId());
                    GameState toEvalGameState = gameState.clone();
                    toEvalGameState.updateGameState(move);
                    Score score = new Score(move,heuristics.evaluate(toEvalGameState, attributes.getOurPlayerId()),toEvalGameState);
                    res.add(score);
                }
            }
        }
        return res;
    }

    /**
     * Encapsulates the move calculating algorithms for the different game phases
     * and is called by other methods to get all possible moves of a player on a
     * certain game state.
     *
     * @param gameState The current gameState.
     * @param playerId  The playerId to evaluate.
     * @return All possible moves in an IntLinkedList.
     */
    public static ArrayList<Score> calculateMoves(GameState gameState, int playerId, Heuristics heuristics) {
        if (Coder.decodeplayerInGame(gameState.attributes().getPlayer(playerId)) == 0) {
            return new ArrayList<>();
        }
        if (gameState.attributes().isPhase1()) {
            return MoveVerifier.allMovesPhase1(gameState, playerId, heuristics);
        } else
            return allMovesPhase2(gameState, heuristics);
    }

    /**
     * Encapsulates the move finding logic for the next player given a last playerId
     * considering disqualification, not being able to make a move.
     *
     * @param gameState    The current gameState.
     * @param lastPlayerId The playerId that has done the last move.
     * @return All possible moves of the next player in an IntLinkedList.
     */
    public static ArrayList<Score> calculateNextMoves(GameState gameState, int lastPlayerId, Heuristics heuristics) {
        lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers()) + 1;
        ArrayList<Score> res = MoveVerifier.calculateMoves(gameState, lastPlayerId, heuristics);
        int counter = 1; // counts how many players have been checked

        // Iterates over players until someone does have moves left
        while (res.isEmpty() && counter < gameState.attributes().getNumberPlayers() + 1) {
            lastPlayerId = (lastPlayerId % gameState.attributes().getNumberPlayers()) + 1;
            res = MoveVerifier.calculateMoves(gameState, lastPlayerId, heuristics);
            counter++;
        }
        // Handles logic if no player has moves left
        if (counter == gameState.attributes().getNumberPlayers()) {
            // Switch from phase 1 to 2
            if (gameState.attributes().isPhase1()) {
                gameState.attributes().setGamePhase(2);
                int nextPlayerId = lastPlayerId;
                if (nextPlayerId == 1) {
                    nextPlayerId = gameState.attributes().getNumberPlayers();
                } else {
                    nextPlayerId--;
                }
                return calculateNextMoves(gameState, nextPlayerId, heuristics);
            } else { // End game logic
                return new ArrayList<>();
            }
        } else {
            return res;
        }
    }
}
