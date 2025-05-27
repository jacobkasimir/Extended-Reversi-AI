package algorithms.strategies;



import algorithms.datastructures.IntLinkedList;
import algorithms.datastructures.ListNode;
import control.Coder;
import control.Constants;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import validation.GameRules;

/**
 * Provides heuristic evaluation methods to assess board positions
 * in a generalized multi-player Reversi-like game with a dynamic map.
 */
public class Heuristics {

    private final IntLinkedList corners; // list of all the corners on the map
    private final int initialBombs; //initial number of bombs
    private final int initialOverrides; //initial number of override stones
    private final int initalNumberPlayers; //initial numbers of players

    /**
     * Constructs a new Heuristics object and detects the corner positions on the map.
     * @param gameState The initial game state, when instantiating the Heuristics object.
     * @param bombCount The initial number of bombs per player.
     * @param overrideCount The initial number of override stones per player.
     * @param numberPlayers The initial number of players.
     */
    public Heuristics(GameState gameState, int bombCount, int overrideCount, int numberPlayers) {
        this.corners = findCorners(gameState.mapController());
        this.initialBombs = bombCount;
        this.initialOverrides = overrideCount;
        this.initalNumberPlayers = numberPlayers;
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
                    int next1 = map.getNextTile(Coder.encodePosition(x, y, i));
                    int next2 = map.getNextTile(Coder.encodePosition(x, y, i + 4));

                    if (next1 != -1 && map.getTile(Coder.decodePosX(next1), Coder.decodePosY(next1)) != '-') {
                        if (next2 != -1 && map.getTile(Coder.decodePosX(next2), Coder.decodePosY(next2)) != '-') {
                            currentIsCorner = false;
                            break;
                        }
                    }
                }

                if (currentIsCorner) {
                    result.add(Coder.encodeCoordinate(x, y));
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
            int x = Coder.decodeXCoords(node.getValue());
            int y = Coder.decodeYCoords(node.getValue());
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
        return Coder.decodeplayerOverrides(state.getPlayer(playerId)) / ((double) initialOverrides);
    }

    /**
     * Returns the number of bombs the player has.
     * @param state The game state.
     * @param playerId The player ID.
     * @return The number of available bombs.
     */
    public double ratioBombs(GameAttributes state, int playerId) {
        return Coder.decodeplayerBombs(state.getPlayer(playerId)) / ((double) initialBombs);
    }

    

    /**
     * Calculates three different ratios in parallel.
     * 1.: ratioOfOwnTilesOverall() - ratio of own occupied tiles to all reachable tiles.
     * 2.: ratioOfOwnTilesToOthers() - ratio of own occupied tiles to all tiles occupied by opponents.
     * 3.: walls() - ratio of how much the own tiles are connected (neighboring) with each other.
     * 
     * @param map The current MapController.
     * @param playerId The player ID.
     * @return An array holding all three ratios.
     */
    public double[] mapRelatedHeuristicsPhase1(MapController map, int playerId){
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
                        
                        //vertical and horizontal neighbors add value 2, diagonal only 1
                        int nextPosition = map.getNextTile(Coder.encodePosition(x, y, i));
                        if(nextPosition != -1 && map.getTile(Coder.decodePosX(nextPosition), Coder.decodePosY(nextPosition)) == (char) (playerId + '0')){
                            if (i == 1 || i == 3 || i == 5 || i == 7) {
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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates three different ratios in parallel.
     * 1.: ratioOfOwnTilesOverall() - ratio of own occupied tiles to all reachable tiles.
     * 2.: ratioOfOwnTilesToOthers() - ratio of own occupied tiles to all tiles occupied by opponents.
     * 3.: distanceToStrongestOpponent1() - ratio of own Tiles to the amount of tiles of the strongest opponent (1 means greaterEquals).
     * 4.: distanceToStrongestOpponent2() - ratio of the amount of tiles of the strongest opponent to our own Tiles (0 if ownTiles > strongestPlayerTiles).
     * 5.: ratioPlayersInGame() - ratio of alive players to number of initial players.
     * 
     * @param state The current game state.
     * @param playerId The player ID.
     * @return An array holding all three ratios.
     */
    public double[] mapRelatedHeuristicsPhase2(GameState state, int playerId){
        double[] res = new double[5];

        //for ratioOfOwnTilesOverall()
        double ownTiles = 0;
        double reachableTiles = 0;

        //for ratioOfOwnTilesToOthers()
        double opponentTiles = 0;
        
        //for distanceToStrongestOpponent()
        int[] tilesPerPlayer = new int[state.attributes().getNumberPlayers()];


        for (int x = 0; x < state.mapController().getWidth(); x++) {
            for (int y = 0; y < state.mapController().getHeight(); y++) {

                char tile = state.mapController().getTile(x, y);

                //for ratioOfOwnTilesOverall() (index = 0)
                if (tile != '-') reachableTiles++;  // count valid tiles only


                //for distanceToStrongestOpponent()
                if (GameRules.isPlayer(tile)){
                    tilesPerPlayer[(int) tile - 49 ]++;
                    
                    //for ratioOfOwnTilesToOthers() (index = 1)
                    if (((int) tile - 49 ) != playerId) opponentTiles++;
                }
                
            }
        }
        ownTiles = tilesPerPlayer[playerId-1];


        //ratioOfOwnTilesOverall()
        res[0] = ownTiles / Math.max(1, reachableTiles);

        //ratioOfOwnTilesToOthers()
        res[1] = ownTiles / opponentTiles;
        
        //distanceToStrongestPlayer1() & distanceToStrongestPlayer2() & ratioPlayersInGame()
        tilesPerPlayer[playerId-1] = 1;
        int strongestOpponent = 1;
        for (int i : tilesPerPlayer) {
            if(i>strongestOpponent){
                strongestOpponent = i;
            }
        }
        if(strongestOpponent > ownTiles){
            res[2] = ownTiles / Math.max(1, strongestOpponent);
            res[3] = 0;
            res[4] = 0;
        }
        else{
            res[2] = 1;
            res[3] = 1.0 - strongestOpponent / Math.max(ownTiles,1);
            res[4] = ratioPlayersInGame(state.attributes());
        }
        
        return res;
    }


    /**
     * Returns the ratio of players in the game to the number of players that started the game.
     * @param attributes The game attributes.
     * @return The number of available overwrites.
     */
    private double ratioPlayersInGame(GameAttributes attributes) {
        return 1.0 - attributes.getNumberPlayers() / ((double) initalNumberPlayers);
    }


///////////////////////////////////////////////////// EVALUATE ///////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Combines several heuristics into a single evaluation score for a player in a given game state.
     * Weights should be tuned experimentally or via machine learning.
     * @param state The game state
     * @param playerId The player ID.
     * @return A combined heuristic score.
     */
    public int evaluate(GameState state, int playerId) {
        if(state.attributes().isPhase1()){
            //scores of heuristics
            double[] res = mapRelatedHeuristicsPhase2(state, playerId);
            double ownToOverall = res[0];
            double ownToOpponents = res[1];
            double walls = res[2];

            //weights of heuristics (we can modify them)
            double weightCorners = 0.5;
            double weightOwnToOverall = 0.6;
            double weightOwnToOpponents = 0.2;
            double weightWalls = 0.2;
            double weightBombs = 0.2;
            double weightOverrides = 0.2;

            double score = 0;

            score += weightCorners * ratioOfCornersCaptured(state.mapController(), playerId);
            score += weightOwnToOverall * ownToOverall;
            score += weightOwnToOpponents * ownToOpponents;
            score += weightWalls * walls;
            score += weightBombs * ratioBombs(state.attributes(), playerId);
            score += weightOverrides * ratioOverrides(state.attributes(), playerId);

            return (int) Math.round(score * 1000); // 0-1000 points
        }
        else{
            double[] res = mapRelatedHeuristicsPhase1(state.mapController(), playerId);
            double ownToOverall = res[0];
            double ownToOpponents = res[1];
            double distanceToStrongestOpponent1 = res[2];
            double distanceToStrongestOpponent2 = res[3];
            double ratioPlayersInGame = res[4];

            //weights of heuristics (we can modify them)
            double weightOwnToOverall = 0.6;
            double weightOwnToOpponents = 0.2;
            double weightdistStrongest1 = 0.2;
            double weightdistStrongest2 = 0.2;
            double weightPlayersInGame = 0.2;

            double score = 0;

            score += weightOwnToOverall * ownToOverall;
            score += weightOwnToOpponents * ownToOpponents;
            score += weightdistStrongest1 * distanceToStrongestOpponent1;
            score += weightdistStrongest2 * distanceToStrongestOpponent2;
            score += weightPlayersInGame * ratioPlayersInGame;

            return (int) Math.round(score * 1000); // 0-1000 points
        }        
    }



    private boolean insideMap(int x, int y, MapController map) {
        return x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight();
    }





    ///////////////////////////////////////////////////// OLD CODE ///////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    

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
            int x = Coder.decodeXCoords(node.getValue());
            int y = Coder.decodeYCoords(node.getValue());

            if (corners.contains(Coder.encodeCoordinate(x, y))) score += att.getNumberPlayers();
            if(bigCorner(map, att, x, y)) score += att.getNumberPlayers();
            for(int i = 0; i < att.getNumberPlayers(); i++) {
                if(Coder.decodeplayerOverrides(att.getPlayer(i)) == 0) score++;
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

            next = map.getNextTile(Coder.encodePosition(x, y, i));
            if (next == -1) {
                counter--;
                continue;
            }

            int nextx = Coder.decodePosX(next);
            int nexty = Coder.decodePosY(next);
            if (map.getTile(nextx, nexty) == '0' + a.getOurPlayerId()) {
                while (true) {
                    next = map.getNextTile(Coder.encodePosition(nextx, nexty, i));
                    if (next == -1) {
                        counter--;
                        break;
                    }
                    nextx = Coder.decodePosX(next);
                    nexty = Coder.decodePosY(next);
                    if (map.getTile(nextx, nexty) != '0' + a.getOurPlayerId()) {
                        if (i % 2 == 1) counter = 3;
                        break;
                    }
                }
            }
        }
        return counter == 0;
    }

}