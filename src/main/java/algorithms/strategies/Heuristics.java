package algorithms.strategies;



import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
import control.Constants;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import validation.GameRules;
import validation.MoveVerifier;

/**
 * Provides heuristic evaluation methods to assess board positions
 * in a generalized multi-player Reversi-like game with a dynamic map.
 */
public class Heuristics {

    private final IntLinkedList corners; // list of all the corners on the map
    private final int initialBombs; //initial number of bombs
    private final int initialOverrides; //initial number of override stones

    /**
     * Constructs a new Heuristics object and detects the corner positions on the map.
     * @param gameState The initial game state, when instantiating the Heuristics object.
     */
    public Heuristics(GameState gameState, int bombCount, int overrideCount) {
        this.corners = findCorners(gameState.mapController());
        this.initialBombs = bombCount;
        this.initialOverrides = overrideCount;
    }


    /**
     * Finds corner tiles on the map. A tile is considered a "corner" if only one side is reachable,
     * and all orthogonal directions beyond that side are blocked or lead to a hole.
     * 
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



    ///////////////////////////////////////////////////// HEURISTICS PHASE 1 //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Computes the ratio of captured corner tiles for a given player.
     * 
     * @param map The map to analyze.
     * @param playerId The ID of the player (0-indexed).
     * @return Fraction of all corners held by the player.
     */
    public double ratioOfCornersCaptured(MapController map, int  playerId) {
        if (corners.isEmpty()) return 0;

        double cornersCaptured = 0;
        ListNode node = corners.getHead();

        while (node != null) {
            int x = MoveVerifier.decodeXCoords(node.getValue());
            int y = MoveVerifier.decodeYCoords(node.getValue());
            if (map.getTile(x, y) == (char) (playerId + '0')) cornersCaptured++;
            node = node.getNext();
        }

        return cornersCaptured / corners.getSize();
    }


    

    /**
     * Returns the number of overwrite tokens the player has.
     * @param state The game state.
     * @param playerId The player ID.
     * @return The number of available overwrites.
     */
    public double ratioOverrides(GameAttributes state, int playerId) {
        return state.getPlayer(playerId).getNumberOverrides() / ((double) initialOverrides);
    }

    /**
     * Returns the number of bombs the player has.
     * @param state The game state.
     * @param playerId The player ID.
     * @return The number of available bombs.
     */
    public double ratioBombs(GameAttributes state, int playerId) {
        return state.getPlayer(playerId).getNumberBombs() / ((double) initialBombs);
    }

    

    /**
     * Calculates three different ratios in parallel.
     * 1.: ratioOfOwnTilesOverall() - ratio of own occupied tiles to all reachable tiles.
     * 2.: ratioOfOwnTilesToOthers() - ratio of own occupied tiles to all tiles occupied by opponents.
     * 3.: walls() - ratio of how much the own tiles are connected (neighboring) with each other.
     * 
     * @param map The current map.
     * @param playerId The player ID.
     * @return An array holding all three ratios.
     */
    public double[] mapRelatedHeuristics(MapController map, int playerId){
        double[] res = new double[5];

        //for ratioOfOwnTilesOverall()
        double ownTiles = 0;
        double reachableTiles = 0;

        //for ratioOfOwnTilesToOthers()
        double opponentTiles = 0;
        
        //for walls()
        double countNeighboring = 0;


        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {

                char tile = map.getTile(x, y);

                //for ratioOfOwnTilesOverall() (index = 0)
                if (tile != '-') reachableTiles++;  // count valid tiles only
                if (tile == (char) (playerId + '0')) ownTiles++;

                //for ratioOfOwnTilesToOthers() (index = 1)
                else if (GameRules.isPlayer(tile)) opponentTiles++;

                //for walls() (index = 2)
                if(tile == (char) (playerId + '0')){
                    for (int i = 0; i < Constants.NUMBER_OF_DIRECTIONS; i++) { //counting neighboring tiles that are occupied by the playerId
                        int xNeighbor = x;
                        int yNeighbor = y;

                        //calculate the coordinates for the neighboring tile for a given direction (i)
                        if(i > 0 && i < 4){
                            xNeighbor++;
                        }
                        else if(i > 4 && i < 8){
                            xNeighbor--;
                        }

                        if(i > 6 || i < 2){
                            yNeighbor--;
                        }
                        else if(i > 2 && i < 6){
                            yNeighbor++;
                        }
                        
                        //vertical and horizontal neighbors add value 2, diagonal only 1
                        if(insideMap(xNeighbor, yNeighbor, map) && map.getTile(xNeighbor, yNeighbor) == (char) (playerId + '0')){
                            if(x != xNeighbor && y != yNeighbor){
                                countNeighboring++;
                            }
                            else{
                                countNeighboring += 2;
                            }
                        }
                    }
                }
                
            }
        }

        //ratioOfOwnTilesOverall()
        res[0] = ownTiles / reachableTiles;

        //ratioOfOwnTilesToOthers()
        res[1] = ownTiles / opponentTiles;
        
        //walls()
        double maxNeighboring = ownTiles * 12; //times twelve because horizontal and vertical neighbors are valued with 2 and diagonal neighbors with 1
        res[2] = 1.0 - (countNeighboring / maxNeighboring); //inverted so that the bigger the ratio the better for the player

        return res;
    }





///////////////////////////////////////////////////// HEURISTICS PHASE 2 //////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
















    /**
     * Combines several heuristics into a single evaluation score for a player in a given game state.
     * Weights should be tuned experimentally or via machine learning.
     * @param map The current map.
     * @param att The game attributes.
     * @param playerId The player ID.
     * @return A combined heuristic score.
     */
    public int evaluate(MapController map, GameAttributes att, int playerId) {

        //scores of heuristics
        double[] res = mapRelatedHeuristics(map, playerId); //TODO oder own playerId
        double ownOverall = res[0];
        double ownOpponent = res[1];
        double walls = res[2];

        //weights of heuristics (we can modify them)
        double wCorners = 0.4;
        double wSafeStones = 0.6;
        double wTileRatio = 0.2;
        double wWalls = 0.2;
        double wSpecials = 0.2;

        double score = 0;
        score += wCorners * ratioOfCornersCaptured(map, playerId);
        //score += wSafeStones * amountSaveStones(map, att, playerId);
        score += wTileRatio * (ownOpponent + ownOpponent);
        score += wWalls * walls;
        score += wSpecials * (ratioBombs(att, playerId) + ratioOverrides(att, playerId)) / 10.0;

        /*
         * The heuristics in comments still cause exceptions
         * //TODO further debugging required to avoid disqualifications
         */

        return (int) Math.round(score * 1000); // 0-1000 points
    }




    private boolean insideMap(int x, int y, MapController map) {
        return x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight();
    }





    ///////////////////////////////////////////////////// OLD CODE ///////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    /**
     * Calculates the percentage of all tiles on the board that are occupied by the player.
     * 
     * @param map The current map.
     * @param playerId The player's ID.
     * @return A normalized score between 0 and 1.
     */
    public static double ratioOfOwnTilesOverall(MapController map, int playerId) {
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
    public static double ratioOfOwnTilesToOthers(MapController map, int playerId) {
        double own = 0, other = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                char tile = map.getTile(x, y);
                if (tile == (char) (playerId + '0')) own++;
                else if (GameRules.isPlayer(tile)) other++;
            }
        }

        return other == 0 ? 1 : own / other;
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
        IntLinkedList ownTiles = new IntLinkedList();//att.getPlayer(playerId).getTiles();
        if (ownTiles.getSize() == 0) return 0;

        double score = 0;
        ListNode node = ownTiles.getHead();

        while (node != null) {
            int x = MoveVerifier.decodeXCoords(node.getValue());
            int y = MoveVerifier.decodeYCoords(node.getValue());

            if (corners.contains(MoveVerifier.encodeCoordinate(x, y))) score += att.getNumberPlayers();
            if(bigCorner(map, att, x, y)) score += att.getNumberPlayers();
            for(int i = 0; i < att.getNumberPlayers(); i++) {
                if(att.getPlayer(i).getNumberOverrides() == 0) score++;
            }

            node = node.getNext();
        }

        return score / ownTiles.getSize() * att.getNumberPlayers();
    }

    private static boolean bigCorner(MapController map, GameAttributes a, int x, int y) {
        int next;
        int counter = 3;
        for (int i = 0; i < 8; i++) {
            if (counter == 0) return true;

            next = map.getNextTile(MoveVerifier.encodePosition(x, y, i));
            if (next == -1) {
                counter--;
                continue;
            }

            int nextx = MoveVerifier.decodePosX(next);
            int nexty = MoveVerifier.decodePosY(next);
            if (map.getTile(nextx, nexty) == '0' + a.getOurPlayerId()) {
                while (true) {
                    next = map.getNextTile(MoveVerifier.encodePosition(nextx, nexty, i));
                    if (next == -1) {
                        counter--;
                        break;
                    }
                    nextx = MoveVerifier.decodePosX(next);
                    nexty = MoveVerifier.decodePosY(next);
                    if (map.getTile(nextx, nexty) != '0' + a.getOurPlayerId()) {
                        if (i % 2 == 1) counter = 3;
                        break;
                    }
                }
            }
        }
        return counter == 0;
    }


    /**
     * Calculates a heuristic score representing how many of a player's tiles are surrounded
     * by their own tiles in adjacent cardinal directions (up, down, left, right).
     * <p>
     * This metric can be used to evaluate how compact or "walled-in" a player's territory is.
     * Walls tend to decrease the mobility of a player, that's why it is desirable to have a spread position.
     *
     * @param map The game map controller.
     * @param att The current game attributes, including player tile ownership.
     * @param playerId The ID of the player being evaluated.
     * @return A normalized score (negative or close to zero is better), indicating tile connectivity.
     */
    public static double walls(MapController map, GameAttributes att, int playerId) {
        IntLinkedList ownTiles = new IntLinkedList();//att.getPlayer(playerId).getTiles();
        int size = ownTiles.getSize();
        if (size == 0) return 0;

        double adjacentCount = 0;
        ListNode node = ownTiles.getHead();

        // Iterate over each tile
        while (node != null) {
            int value = node.getValue();

            // Check the 4 cardinal directions around each tile
            int[] directions = {
                    value - 51, // up
                    value + 51, // down
                    value - 1,  // left
                    value + 1   // right
            };

            for (int neighbor : directions) {
                if (neighbor >= 0) {
                    int nx = MoveVerifier.decodePosX(neighbor);
                    int ny = MoveVerifier.decodePosY(neighbor);

                    // Increase count if neighbor tile is ours
                    if (map.getTile(nx, ny) == (char) ('0' + playerId)) {
                        adjacentCount++;
                    }
                }
            }
            node = node.getNext();
        }

        // Ratio of total tiles to number of own neighbors
        double ratio = size / adjacentCount;

        // Normalize the score to be negative if ratio > 1 (less compact), positive otherwise
        return (ratio > 1) ? -ratio + 1 : ratio - 1;
    }


    /*private static boolean cantBeCapturedMoveAtAll(MapController map, int x, int y, GameAttributes att) {
        for (byte i = 0; i < att.getNumberPlayers(); i++) {
            for (int special : new int[]{0, att.getOurPlayerId(), 20, 21}) {
                int move = MoveVerifier.encodeMove(x, y, special, i);
                if (MoveVerifier.isValid(move, map, att)) return false;
            }
        }
        return true;
    }*/
}
