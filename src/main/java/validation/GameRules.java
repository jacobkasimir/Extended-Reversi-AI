package validation;

import control.Constants;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;

/**
 * Class with different static methods to validate single parts of the game rules.
 * There is no need to create an instance of this class because all methods are static.
 */
public class GameRules {

    /**
     * Checks if the tile at the given move is not a hole ('-').
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if the tile is not a hole; false otherwise.
     */
    public static boolean isNotHole(int move, MapController map) {
        return getTileAtMove(map, move) != '-';
    }

    /**
     * Checks if the move coordinates are within the map boundaries.
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if within bounds; false otherwise.
     */
    public static boolean insideMap(int move, MapController map) {
        int x = MoveVerifier.decodeX(move);
        int y = MoveVerifier.decodeY(move);
        return x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight();
    }

    /**
     * Determines if the targeted tile is occupied.
     *
     * @param move The move to check.
     * @param map  The map controller.
     * @return true, if the tile is occupied by another player or an extension stone.
     */
    public static boolean occupied(int move, MapController map) {
        char tile = getTileAtMove(map, move);
        return switch (tile) {
            case '0', 'b', 'i', 'c' -> false;
            default -> true;
        };
    }

    /**
     * Checks if a player has any bombs remaining.
     *
     * @param attr The current game attributes.
     * @param move  The move.
     * @return true if player has bombs.
     */
    public static boolean hasBomb(GameAttributes attr, int move) {
        return attr.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberBombs() > 0;
    }

    /**
     * Checks if a player has override stones remaining.
     *
     * @param attr Game attributes.
     * @param move  The move.
     * @return true if override stones are available.
     */
    public static boolean hasOverride(GameAttributes attr, int move) {
        return attr.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberOverrides() > 0;
    }

    /**
     * Validates if a player is in-game and the id is in bounds.
     *
     * @param move  The move.
     * @param attr Game attributes.
     * @return true if move is from a valid player in-game.
     */
    public static boolean validPlayer(int move, GameAttributes attr) {
        int playerId = MoveVerifier.decodePlayerId(move);
        return playerId >= 1 && playerId <= attr.getNumberPlayers()
                && attr.getPlayer(playerId).isInGame();
    }

    /**
     * Checks if a move targets an expansion tile ('x').
     *
     * @param map  Map controller.
     * @param move The move.
     * @return true if tile is an expansion tile.
     */
    public static boolean isExpansion(MapController map, int move) {
        return getTileAtMove(map, move) == 'x';
    }

    /**
     * Validates the special field of a move for correctness.
     *
     * @param attr Game attributes.
     * @param move  The move.
     * @param map   Map controller.
     * @return true if the special field is valid.
     */
    public static boolean checkSpecial(GameAttributes attr, int move, MapController map) {
        int special = MoveVerifier.decodeSpecial(move);
        if (!attr.isPhase1()) return special == 0; //Special must always be 0 in phase 2 (bombing)

        char tile = getTileAtMove(map, move);
        return switch (tile) {
            case 'b' -> special == 20 || special == 21; //bonus field (20->bomb) (21->override)
            case 'c' -> special >= 1 && special <= attr.getNumberPlayers(); //choice -> player to switch with
            default -> special == 0; //for all other fields, special must be 0
        };
    }

    /**
     * Determines if the move captures opponent tiles.
     *
     * @param move The move.
     * @param map  The map.
     * @return true if a capture occurs.
     */
    public static boolean capturing(int move, MapController map) {
        int playerId =  MoveVerifier.decodePlayerId(move);

        //coordinates of the targeted tile
        int startX = MoveVerifier.decodeX(move);
        int startY = MoveVerifier.decodeY(move);

        //check for each direction from the targeted field if we capture
        for (int dir = 0; dir < Constants.NUMBER_OF_DIRECTIONS; dir++) {
            int current = MoveVerifier.encodePosition(startX, startY, dir); //create Position
            int steps = 0; //count the number of stones that would be captured in this direction

            while (true) {
                current = map.getNextTile(current); //one step forwards
                if (current < 0) break; //outside of the map

                //decode the new position
                int currentX = MoveVerifier.decodePosX(current);
                int currentY = MoveVerifier.decodePosY(current);

                char tile = map.getTile(currentX, currentY);

                //check if we reach a tile of the move's player
                if (tile == (char) ('0' + playerId)) {
                    //when at least one stone is captured and we did not reach the start tile we can return true
                    if (steps > 0 && (currentX != startX || currentY != startY)) return true;
                    break; //false for this direction
                }

                //no capturing when we reach '-', '0', 'i', 'c', 'b' or the starting field
                if (tile == '-' || tile == '0' || tile == 'i' || tile == 'c' || tile == 'b') break;
                if (currentX == startX && currentY == startY) break;

                steps++; //count captured stones
            }
        }
        return false; //No capture found
    }

    /**
     * Helper method to get tile at the target position of a move.
     *
     * @param map  Map controller.
     * @param move The move.
     * @return char of the tile.
     */
    private static char getTileAtMove(MapController map, int move) {
        return map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move));
    }
}
