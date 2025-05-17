/**
 * Contains the validation rules for player moves during both phases of the game.
 */
public class GameRules {

    /**
     * Checks if the tile at the given move is not a hole.
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if the tile is not a hole, false otherwise.
     */
    public static boolean isNotHole(int move, MapController map) {
        return getTileAtMove(map, move) != '-';
    }

    /**
     * Checks if the move is within the map boundaries.
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if the move is inside the map bounds, false otherwise.
     */
    public static boolean insideMap(int move, MapController map) {
        int x = MoveVerifier.decodeX(move);
        int y = MoveVerifier.decodeY(move);
        return x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight();
    }

    /**
     * Checks whether the tile is occupied by a player or special item.
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if the tile is already occupied, false otherwise.
     */
    public static boolean occupied(int move, MapController map) {
        char tile = getTileAtMove(map, move);
        return !(tile == '0' || tile == 'b' || tile == 'i' || tile == 'c');
    }

    /**
     * Checks if the player has at least one bomb.
     *
     * @param state Game state including players.
     * @param move  The move containing the player ID.
     * @return true if the player has bombs, false otherwise.
     */
    public static boolean hasBomb(GameAttributes state, int move) {
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberBombs() > 0;
    }

    /**
     * Checks if the player has any Overwrite moves left.
     *
     * @param state Game state including players.
     * @param move  The move containing the player ID.
     * @return true if the player has Overwrites remaining, false otherwise.
     */
    public static boolean hasOverwrite(GameAttributes state, int move) {
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberOverwrites() > 0;
    }

    /**
     * Validates whether the player making the move is valid and still in the game.
     *
     * @param move  The move to validate.
     * @param state The game attributes.
     * @return true if the player ID is valid and the player is in the game.
     */
    public static boolean validMove(int move, GameAttributes state) {
        byte playerId = (byte) MoveVerifier.decodePlayerId(move);
        return playerId >= 1 && playerId <= state.getNumberPlayers() && state.getPlayer(playerId).isInGame();
    }

    /**
     * Checks whether the move targets an expansion tile.
     *
     * @param map  The map controller instance.
     * @param move The move to validate.
     * @return true if the target tile is an expansion tile, false otherwise.
     */
    public static boolean isExpansion(MapController map, int move) {
        return getTileAtMove(map, move) == 'x';
    }

    /**
     * Validates the special field of a move, depending on the game phase and tile type.
     *
     * @param state Game attributes containing phase and player info.
     * @param move  The move to validate.
     * @param map   The map controller instance.
     * @return true if the special field is valid for the current tile and phase.
     */
    public static boolean checkSpecial(GameAttributes state, int move, MapController map) {
        int special = MoveVerifier.decodeSpecial(move);
        if (!state.isPhase1()) return special == 0;

        char tile = getTileAtMove(map, move);
        return switch (tile) {
            case 'b' -> special == 20 || special == 21;
            case 'c' -> special >= 1 && special <= state.getNumberPlayers();
            default -> special == 0;
        };
    }

    /**
     * Determines whether a move results in capturing other tiles (enclosure rule).
     *
     * @param move The move to validate.
     * @param map  The map controller instance.
     * @return true if the move captures other tiles, false otherwise.
     */
    public static boolean capturing(int move, MapController map) {
        byte playerId = (byte) MoveVerifier.decodePlayerId(move);
        int startX = MoveVerifier.decodeX(move);
        int startY = MoveVerifier.decodeY(move);

        for (int dir = 0; dir < 8; dir++) {
            int current = MoveVerifier.encodePosition(startX, startY, dir);
            int steps = 0;

            while (true) {
                current = map.getNextTile(current);
                if (current < 0) break;

                int cx = MoveVerifier.decodePosX(current);
                int cy = MoveVerifier.decodePosY(current);
                char tile = map.getTile(cx, cy);

                if (tile == (char) ('0' + playerId)) {
                    if (steps > 0 && (cx != startX || cy != startY)) return true;
                    break;
                }

                if (tile == '-' || tile == '0' || tile == 'i' || tile == 'c' || tile == 'b') break;
                if (cx == startX && cy == startY) break;

                steps++;
            }
        }
        return false;
    }

    /**
     * Returns the character at the tile targeted by the move.
     *
     * @param map  The map controller instance.
     * @param move The move to evaluate.
     * @return The tile character.
     */
    private static char getTileAtMove(MapController map, int move) {
        return map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move));
    }
}
