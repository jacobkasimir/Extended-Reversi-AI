/**
 * Provides heuristic evaluation methods to assess board positions
 * in a generalized multi-player Reversi-like game with a dynamic map.
 */
public class Heuristics {

    private final IntLinkedList corners;

    /**
     * Constructs a new Heuristics object and detects the corner positions on the map.
     * @param map The map on which to identify corner tiles.
     */
    public Heuristics(MapController map) {
        this.corners = findCorners(map);
    }

    /**
     * Finds corner tiles on the map. A tile is considered a "corner" if only one side is reachable,
     * and all orthogonal directions beyond that side are blocked or lead to a hole.
     * @param map The game map.
     * @return A list of encoded corner coordinates.
     */
    public IntLinkedList findCorners(MapController map) {
        IntLinkedList result = new IntLinkedList();

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == '-') continue;

                boolean currentIsCorner = true;

                for (int i = 0; i < 4; i++) {
                    int next1 = map.getNextTile(MoveVerifier.encodePosition(x, y, i));
                    int next2 = map.getNextTile(MoveVerifier.encodePosition(x, y, i + 4));

                    if (next1 != -1 && map.getTile(MoveVerifier.decodePosX(next1), MoveVerifier.decodePosY(next1)) != '-') {
                        if (next2 != -1 && map.getTile(MoveVerifier.decodePosX(next2), MoveVerifier.decodePosY(next2)) != '-') {
                            currentIsCorner = false;
                            break;
                        }
                    }
                }

                if (currentIsCorner) {
                    result.add(MoveVerifier.encodeCoordinate(x, y));
                }
            }
        }

        return result;
    }

    /**
     * Computes the ratio of captured corner tiles for a given player.
     * @param map The map to analyze.
     * @param playerId The ID of the player (0-indexed).
     * @return Fraction of all corners held by the player.
     */
    public double amountOfCornersCaptured(MapController map, int  playerId) {
        if (corners.isEmpty()) return 0;

        double counter = 0;
        ListNode node = corners.getHead();

        while (node != null) {
            int x = MoveVerifier.decodeXCoords(node.getValue());
            int y = MoveVerifier.decodeYCoords(node.getValue());
            if (map.getTile(x, y) == (char) (playerId + '0')) counter++;
            node = node.getNext();
        }

        return counter / corners.getSize();
    }

    /**
     * Calculates the percentage of all tiles on the board that are occupied by the player.
     * @param map The current map.
     * @param playerId The player's ID.
     * @return A normalized score between 0 and 1.
     */
    public static double amountOfOwnTilesOverall(MapController map, int playerId) {
        int own = 0;
        int total = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                char tile = map.getTile(x, y);
                if (tile != '-') total++;  // Count valid tiles only
                if (tile == (char) (playerId + '0')) own++;
            }
        }

        return total == 0 ? 0 : (double) own / total;
    }

    /**
     * Compares the number of own tiles to the number of all other players' tiles.
     * @param map The map to analyze.
     * @param playerId The player ID.
     * @return Ratio of own tiles to others’ tiles.
     */
    public static double amountOfOwnTilesToOthers(MapController map, int playerId) {
        double own = 0, other = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                char tile = map.getTile(x, y);
                if (tile == (char) (playerId + '0')) own++;
                else if (isPlayer(tile)) other++;
            }
        }

        return other == 0 ? 1 : own / other;
    }

    /**
     * Calculates a normalized mobility value for the player.
     * @param map The map state.
     * @param att The game state attributes.
     * @param playerId The player's ID.
     * @return A normalized score for relative mobility.
     */
    public static double mobility(MapController map, GameAttributes att, int playerId) {
        int own = att.getPlayer(playerId).getTiles().getSize();
        int totalOthers = 0;

        for (int i = 0; i < att.getNumberPlayers(); i++) {
            if (i != playerId) {
                totalOthers += att.getPlayer(i).getTiles().getSize();
            }
        }

        return (own + totalOthers == 0) ? 0 : (double) (own - totalOthers / (att.getNumberPlayers() - 1)) / (own + totalOthers);
    }

    /**
     * Measures how many of a player's stones are in "safe" positions,
     * i.e., either in a corner or cannot be captured by normal or special moves.
     * @param map The current game map.
     * @param att Game state information.
     * @param playerId The player ID.
     * @return A normalized safety score for the player's stones.
     */
    public double amountSaveStones(MapController map, GameAttributes att, int playerId) {
        IntLinkedList ownTiles = att.getPlayer(playerId).getTiles();
        if (ownTiles.getSize() == 0) return 0;

        double score = 0;
        ListNode node = ownTiles.getHead();

        while (node != null) {
            int x = MoveVerifier.decodeXCoords(node.getValue());
            int y = MoveVerifier.decodeYCoords(node.getValue());

            if (corners.contains(MoveVerifier.encodeCoordinate(x, y))) score += 1;
            else if (cantBeCapturedMove(map, x, y, att)) score += 1;
            else if (cantBeCapturedMoveAtAll(map, x, y, att)) score += 2;

            node = node.getNext();
        }

        return score / ownTiles.getSize();
    }

    private static boolean cantBeCapturedMove(MapController map, int x, int y, GameAttributes att) {
        for (byte i = 0; i < att.getNumberPlayers(); i++) {
            int move = MoveVerifier.encodeMove(x, y, 0, i);
            if (MoveVerifier.isValid(move, map, att)) return false;
        }
        return true;
    }

    private static boolean cantBeCapturedMoveAtAll(MapController map, int x, int y, GameAttributes att) {
        for (byte i = 0; i < att.getNumberPlayers(); i++) {
            for (int special : new int[]{0, att.getOurPlayerId(), 20, 21}) {
                int move = MoveVerifier.encodeMove(x, y, special, i);
                if (MoveVerifier.isValid(move, map, att)) return false;
            }
        }
        return true;
    }

    private static boolean isPlayer(char c) {
        return !(c == '-' || c == '0' || c == 'x' || c == 'i' || c == 'b' || c == 'c');
    }

    /**
     * Returns the number of overwrite tokens the player has.
     * @param state The game state.
     * @param playerId The player ID.
     * @return The number of available overwrites.
     */
    public static int numberOfOverrides(GameAttributes state, int playerId) {
        return state.getPlayer(playerId).getNumberOverwrites();
    }

    /**
     * Returns the number of bombs the player has.
     * @param state The game state.
     * @param playerId The player ID.
     * @return The number of available bombs.
     */
    public static int numberOfBombs(GameAttributes state, int playerId) {
        return state.getPlayer(playerId).getNumberBombs();
    }

    /**
     * Combines several heuristics into a single evaluation score for a player in a given game state.
     * Weights should be tuned experimentally or via machine learning.
     * @param map The current map.
     * @param att The game attributes.
     * @param playerId The player ID.
     * @return A combined heuristic score.
     */
    public int evaluate(MapController map, GameAttributes att, int playerId) {
        double wCorners = 0.3;
        double wMobility = 0.2;
        double wSafeStones = 0.2;
        double wTileRatio = 0.2;
        double wSpecials = 0.1;

        double score = 0;
        score += wCorners * amountOfCornersCaptured(map, playerId);
        score += wMobility * mobility(map, att, playerId);
        score += wSafeStones * amountSaveStones(map, att, playerId);
        score += wTileRatio * amountOfOwnTilesToOthers(map, playerId);
        score += wSpecials * (numberOfBombs(att, playerId) + numberOfOverrides(att, playerId)) / 10.0;

        return (int) Math.round(score * 1000); // 0-1000 points
    }
}
