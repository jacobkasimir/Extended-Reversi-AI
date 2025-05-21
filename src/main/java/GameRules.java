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
     * Determines if the targeted tile is occupied by a player or item.
     *
     * @param move The move to check.
     * @param map  The map controller.
     * @return true if the tile is occupied; false if free.
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
     * @param state Game state.
     * @param move  Encoded move containing player ID.
     * @return true if player has bombs.
     */
    public static boolean hasBomb(GameAttributes state, int move) {
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberBombs() > 0;
    }

    /**
     * Checks if a player has overwrite actions remaining.
     *
     * @param state Game state.
     * @param move  Encoded move with player ID.
     * @return true if overwrites are available.
     */
    public static boolean hasOverwrite(GameAttributes state, int move) {
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberOverrides() > 0;
    }

    /**
     * Validates if a player is in-game and the ID is in bounds.
     *
     * @param move  Encoded move.
     * @param state Game state.
     * @return true if move is from a valid player in-game.
     */
    public static boolean validPlayer(int move, GameAttributes state) {
        byte playerId = (byte) MoveVerifier.decodePlayerId(move);
        return playerId >= 1 && playerId <= state.getNumberPlayers()
                && state.getPlayer(playerId).isInGame();
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
     * Validates the special field of a move for legality in the current game phase.
     *
     * @param state Game attributes.
     * @param move  The move.
     * @param map   Map controller.
     * @return true if the special field is valid.
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
     * Determines if the move captures enemy tiles by enclosing them.
     *
     * @param move The move.
     * @param map  The map.
     * @return true if a capture occurs.
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
     * Helper method to get tile character at the target position of a move.
     *
     * @param map  Map controller.
     * @param move The move.
     * @return Character of the tile.
     */
    private static char getTileAtMove(MapController map, int move) {
        return map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move));
    }
}
