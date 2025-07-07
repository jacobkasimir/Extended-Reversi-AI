package algorithms.strategies;

import java.util.ArrayList;

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

    private final ArrayList<Integer> corners; // list of all the corners on the map
    private final int initialBombs; // initial number of bombs
    private final int initialOverrides; // initial number of override stones
    private final int initalNumberPlayers; // initial numbers of players
    private int numberBonusTiles; // initial number of bonus tiles on the map

    /**
     * Constructs a new Heuristics object and detects the corner positions on the
     * map.
     *
     * @param gameState     The initial game state, when instantiating the
     *                      Heuristics object.
     * @param bombCount     The initial number of bombs per player.
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
     * Finds corner tiles on the map. A tile is considered a "corner" if only one
     * side is reachable,
     * and all orthogonal directions beyond that side are blocked or lead to a hole.
     *
     * @param map The game map.
     * @return A list of encoded corner coordinates.
     */
    public ArrayList<Integer> findCorners(MapController map) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int bonusTileCounter = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == 'b')
                    bonusTileCounter++;

                if (map.getTile(x, y) == '-')
                    continue;

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

        this.numberBonusTiles = bonusTileCounter;
        return result;
    }

    /**
     * Computes the ratio of captured corner tiles for a given player.
     *
     * @param map      The map to analyze.
     * @param playerId The ID of the player (0-indexed).
     * @return Fraction of all corners held by the player.
     */
    public double ratioOfCornersCaptured(MapController map, int playerId) {
        if (corners.isEmpty()) return 0;

        double cornersCaptured = 0;

        for (Integer corner : corners) {
            int x = Coder.decodeXCoords(corner);
            int y = Coder.decodeYCoords(corner);
            if (map.getTile(x, y) == (char) (playerId + '0')) cornersCaptured++;
        }

        return cornersCaptured / corners.size();
    }

    /**
     * Returns the number of overwrite tokens the player has.
     *
     * @param state    The game state.
     * @param playerId The player ID.
     * @return The number of available overwrites.
     */
    public double ratioOverrides(GameAttributes state, int playerId) {
        return Coder.decodeplayerOverrides(state.getPlayer(playerId)) 
                / Math.max((double) initialOverrides + numberBonusTiles, 1.0);
    }

    /**
     * Returns the number of bombs the player has.
     *
     * @param state    The game state.
     * @param playerId The player ID.
     * @return The number of available bombs.
     */
    public double ratioBombs(GameAttributes state, int playerId) {
        return Coder.decodeplayerBombs(state.getPlayer(playerId))
                / Math.max((double) numberBonusTiles + initialBombs, 1.0);
    }

    /**
     * Calculates two different ratios in parallel.
     * 1.: ratioOfOwnTilesOverall() - ratio of own occupied tiles to all reachable
     * tiles.
     * 2.: ratioOfOwnTilesToOthers() - ratio of own occupied tiles to all tiles
     * occupied by opponents.
     *
     * @param map      The current MapController.
     * @param playerId The player ID.
     * @return An array holding both ratios.
     */
    public double[] mapRelatedHeuristicsPhase1(MapController map, int playerId) {
        double[] res = new double[5];

        // for ratioOfOwnTilesOverall()
        double ownTiles = 0;
        double reachableTiles = 0;

        // for ratioOfOwnTilesToOthers()
        double occupiedTiles = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {

                char tile = map.getTile(x, y);

                // for ratioOfOwnTilesOverall() (index = 0)
                if (tile != '-')
                    reachableTiles++; // count valid tiles only
                if (tile == (char) (playerId + '0'))
                    ownTiles++;

                // for ratioOfOwnTilesToOthers() (index = 1)
                if (GameRules.isPlayer(tile))
                    occupiedTiles++;

            }
        }

        // ratioOfOwnTilesOverall()
        res[0] = ownTiles / Math.max(1, reachableTiles);

        // ratioOfOwnTilesToOthers()
        res[1] = ownTiles / Math.max(1, occupiedTiles);

        return res;
    }

    /**
     * Calculates five different ratios in parallel.
     * 1.: ratioOfOwnTilesOverall() - ratio of own occupied tiles to all reachable
     * tiles.
     * 2.: ratioOfOwnTilesToOthers() - ratio of own occupied tiles to all tiles
     * occupied by opponents.
     * 3.: distanceToStrongestOpponent1() - ratio of own Tiles to the amount of
     * tiles of the strongest opponent (1 means greaterEquals).
     * 4.: distanceToStrongestOpponent2() - ratio of the amount of tiles of the
     * strongest opponent to our own Tiles (0 if ownTiles > strongestPlayerTiles).
     * 5.: ratioPlayersInGame() - ratio of alive players to number of initial
     * players.
     *
     * @param state    The current game state.
     * @param playerId The player ID.
     * @return An array holding all five ratios.
     */
    public double[] mapRelatedHeuristicsPhase2(GameState state, int playerId) {
        double[] res = new double[5];

        // for ratioOfOwnTilesOverall()
        double ownTiles = 0;
        double reachableTiles = 0;

        // for ratioOfOwnTilesToOthers()
        double occupiedTiles = 0;

        // for distanceToStrongestOpponent()
        int[] tilesPerPlayer = new int[state.attributes().getNumberPlayers()];

        for (int x = 0; x < state.mapController().getWidth(); x++) {
            for (int y = 0; y < state.mapController().getHeight(); y++) {

                char tile = state.mapController().getTile(x, y);

                // for ratioOfOwnTilesOverall() (index = 0)
                if (tile != '-')
                    reachableTiles++; // count valid tiles only

                // for distanceToStrongestOpponent()
                if (GameRules.isPlayer(tile)) {
                    tilesPerPlayer[(int) tile - 49]++;

                    // for ratioOfOwnTilesToOthers() (index = 1)
                    occupiedTiles++;
                }

            }
        }
        ownTiles = tilesPerPlayer[playerId - 1];

        // ratioOfOwnTilesOverall()
        res[0] = ownTiles / Math.max(1, reachableTiles);

        // ratioOfOwnTilesToOthers()
        res[1] = ownTiles / Math.max(1, occupiedTiles);

        // distanceToStrongestPlayer1() & distanceToStrongestPlayer2() &
        // ratioPlayersInGame()
        tilesPerPlayer[playerId - 1] = 1;
        int strongestOpponent = 1;
        for (int i : tilesPerPlayer) {
            if (i > strongestOpponent) {
                strongestOpponent = i;
            }
        }
        if (strongestOpponent > ownTiles) {
            res[2] = ownTiles / Math.max(1, strongestOpponent);
            res[3] = 0;
            res[4] = 0;
        } else {
            res[2] = 1.0;
            res[3] = 1.0 - strongestOpponent / Math.max(ownTiles, 1);
            res[4] = ratioPlayersInGame(state.attributes());
        }

        return res;
    }

    /**
     * Returns the ratio of players in the game to the number of players that
     * started the game.
     *
     * @param attributes The game attributes.
     * @return The number of available overwrites.
     */
    private double ratioPlayersInGame(GameAttributes attributes) {
        return 1.0 - attributes.getNumberPlayers() / ((double) initalNumberPlayers);
    }

    /**
     * Combines several heuristics into a single evaluation score for a player in a
     * given game state.
     * Weights should be tuned experimentally or via machine learning.
     *
     * @param state    The game state
     * @param playerId The player ID.
     * @return A combined heuristic score.
     */
    public int evaluate(GameState state, int playerId) {
        if (state.attributes().isPhase1()) { // evaluation for Phase 1
            // scores of heuristics
            double[] res = mapRelatedHeuristicsPhase1(state.mapController(), playerId);
            double ownToOverall = res[0];
            double ownToOpponents = res[1];

            double score = 0;

            score += Constants.WEIGHTCORNERS * ratioOfCornersCaptured(state.mapController(), playerId);
            score += Constants.WEIGHTOWNTOOVERALLPHASE1 * ownToOverall;
            score += Constants.WEIGHTOWNTOOPPONENTSPHASE1 * ownToOpponents;
            score += Constants.WEIGHTBOMBS * ratioBombs(state.attributes(), playerId);
            score += Constants.WEIGHTOVERRIDES * ratioOverrides(state.attributes(), playerId);

            return (int) Math.round(score * 1000); 
        } else { // evaluation for Phase 2
            double[] res = mapRelatedHeuristicsPhase2(state, playerId);
            double ownToOverall = res[0];
            double ownToOpponents = res[1];
            double distanceToStrongestOpponent1 = res[2];
            double distanceToStrongestOpponent2 = res[3];
            double ratioPlayersInGame = res[4];

            double score = 0;

            score += Constants.WEIGHTOWNTOOVERALLPHASE2 * ownToOverall;
            score += Constants.WEIGHTOWNTOOPPONENTSPHASE2 * ownToOpponents;
            score += Constants.WEIGHTDISTSTRONGEST1 * distanceToStrongestOpponent1;
            score += Constants.WEIGHTDISTSTRONGEST2 * distanceToStrongestOpponent2;
            score += Constants.WEIGHTDISTSTRONGEST2 * ratioPlayersInGame;

            return (int) Math.round(score * 1000); 
        }
    }

}
