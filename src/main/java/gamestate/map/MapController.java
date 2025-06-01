package gamestate.map;

import control.Coder;
import control.Constants;
import gamestate.attributes.GameAttributes;
import validation.GameRules;

/**
 * The MapController class manages the game map and its transitions.
 * It serves as the central interface for map operations, tile management and game mechanics.
 */
public class MapController {
    private final Map map;
    private final Transitions transitions;
    private final int bombRadius;
    private final int width;
    private final int height;

    /**
     * Creates a new MapController with the specified parameters.
     *
     * @param mapString   The initial map data as string array.
     * @param transitions The transitions object for managing tile movements.
     * @param width       The width of the map.
     * @param height      The height of the map.
     * @param bombRadius  The explosion radius for bombs.
     */
    public MapController(String[] mapString, Transitions transitions, int width, int height, int bombRadius) {
        this.height = height;
        this.width = width;
        this.bombRadius = bombRadius;

        this.map = new Map(width, height);
        this.transitions = transitions;
        initMap(mapString);
    }

    /**
     * Creates a deep copy of an existing MapController.
     *
     * @param obj The MapController to copy.
     */
    public MapController(MapController obj) {
        this.bombRadius = obj.getBombRadius();
        this.width = obj.getWidth();
        this.height = obj.getHeight();

        this.map = new Map(obj.getMap());
        this.transitions = obj.getTransitions();
    }


    /**
     * Returns the current game map.
     *
     * @return The Map object representing the current game state.
     */
    public Map getMap() {
        return map;
    }

    /**
     * Returns the transitions object used for movement calculations.
     *
     * @return The Transitions object.
     */
    public Transitions getTransitions() {
        return transitions;
    }

    /**
     * Returns the width of the map.
     *
     * @return The width (x-axis) of the map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map.
     *
     * @return The height (y-axis) of the map.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the bomb explosion radius.
     *
     * @return The radius of bomb explosions.
     */
    public int getBombRadius() {
        return bombRadius;
    }

    /**
     * Returns the tile value at the specified coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The character representing the tile type.
     */
    public char getTile(int x, int y) {
        return this.map.getTile(x, y);
    }

    /**
     * Sets the tile value at the specified coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param c The character value to set.
     */
    public void setTile(int x, int y, char c) {
        this.map.setTile(x, y, c);
    }

    /**
     * Calculates the next tile position based on current position and transitions.
     *
     * @param p The encoded current position.
     * @return The encoded next position after transition.
     */
    public int getNextTile(int p) {
        return transitions.getNextTile(Coder.decodePosX(p), Coder.decodePosY(p), Coder.decodePosDirec(p));
    }

    /**
     * Sets up a transition between two tile positions.
     *
     * @param xTile         The x-coordinate of the start position.
     * @param yTile         The y-coordinate of the start position.
     * @param direcTile     The direction from the start position.
     * @param xNextTile     The x-coordinate of the destination.
     * @param yNextTile     The y-coordinate of the destination.
     * @param direcNextTile The direction at the destination.
     */
    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        transitions.setTransition(xTile, yTile, direcTile, xNextTile, yNextTile, direcNextTile);
    }

    /**
     * Initializes the map using data received from the server.
     * Converts the string representation into the internal map format.
     *
     * @param mapString Array of strings containing the map data.
     */
    private void initMap(String[] mapString) {
        for (int i = 0; i < height; i++) {
            String[] temp = mapString[i].split(" ");
            for (int j = 0; j < width; j++) {
                setTile(j, i, temp[j].charAt(0));
            }
        }
    }


    /**
     * Updates the map state based on a player's move.
     * Handles both phases.
     *
     * @param move       The encoded move to process.
     * @param attributes The associated game attributes.
     */
    public void updateMap(int move, GameAttributes attributes) {
        if (attributes.isPhase1()) {

            int[] directionCounts = new int[Constants.NUMBER_OF_DIRECTIONS]; //holds the number of tiles for every direction that are captured by the move
            boolean search; //boolean that decides whether to search further in a direction or not
            boolean isChoice = getTile(Coder.decodeX(move), Coder.decodeY(move)) == 'c'; //Is the move placed on a choice tile?
            boolean isInversion = getTile(Coder.decodeX(move), Coder.decodeY(move)) == 'i'; //Is the move placed on an inversion tile?
            int tilesPassed; //holds the number of captured tiles for a specific direction
            int currentPosition; //temporary variable

            for (int i = 0; i < Constants.NUMBER_OF_DIRECTIONS; i++) { //search in every direction
                currentPosition = Coder.encodePosition(Coder.decodeX(move), Coder.decodeY(move), i);
                search = true;
                tilesPassed = 0;

                while (search) {  //counting the number of captured tiles in a specific direction
                    currentPosition = getNextTile(currentPosition); //updates the currentPosition for a direction i
                    if (currentPosition < 0) break;  //checks if the nextTile is out of a map
                    if (String.valueOf(Coder.decodePlayerId(move)).equals(String.valueOf(this.getTile(Coder.decodePosX(currentPosition), Coder.decodePosY(currentPosition))))) {
                        //case that the currentPosition is occupied by a tile owned by the player
                        if (tilesPassed > 0) {
                            if (Coder.decodePosX(currentPosition) != Coder.decodeX(move) || Coder.decodePosY(currentPosition) != Coder.decodeY(move)) {
                                //check that the found tile is not the starting tile
                                directionCounts[i] = tilesPassed;
                            }
                        }
                        search = false;
                    }
                    //termination conditions for a direction
                    else if (helpCompare('0', currentPosition)) search = false;
                    else if (helpCompare('c', currentPosition)) search = false;
                    else if (helpCompare('i', currentPosition)) search = false;
                    else if (helpCompare('b', currentPosition)) search = false;
                    else if (helpCompare('-', currentPosition)) search = false;
                    else if (Coder.decodePosX(currentPosition) == Coder.decodeX(move) && Coder.decodePosY(currentPosition) == Coder.decodeY(move))
                        search = false;
                    else {
                        tilesPassed++;
                    }
                }
            }

            //updating captured tiles 
            for (int i = 0; i < Constants.NUMBER_OF_DIRECTIONS; i++) {
                currentPosition = Coder.encodePosition(Coder.decodeX(move), Coder.decodeY(move), i);

                for (int j = 0; j < directionCounts[i] + 1; j++) { //update the map
                    setTile(Coder.decodePosX(currentPosition), Coder.decodePosY(currentPosition), (char) (Coder.decodePlayerId(move) + '0'));
                    currentPosition = getNextTile(currentPosition);
                }
            }

            if (isChoice) {
                choiceHandling(move); //update if choice tile
            } else if (isInversion) {
                inversionHandling(attributes); //update if inversion tile
            }
        } else {
            //handles bombing (phase 2)
            bombing(Coder.decodeX(move), Coder.decodeY(move), bombRadius);
            resolveBombing(Coder.decodeX(move), Coder.decodeY(move), bombRadius);
        }
    }

    /**
     * Compares this MapController with another object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapController other)) return false;

        return this.width == other.width && this.height == other.height && this.bombRadius == other.bombRadius &&
                this.map.equals(other.map) && transitions.equals(transitions);
    }


    /**
     * Processes bomb explosion at specified coordinates.
     * Marks affected tiles with '#' within the bomb radius.
     *
     * @param x      The x-coordinate of the bomb.
     * @param y      The y-coordinate of the bomb.
     * @param radius The remaining explosion radius.
     */
    private void bombing(int x, int y, int radius) {
        if (GameRules.insideMap(Coder.encodeMove(x, y, 0, 0), this) &&
                GameRules.isNotHole(Coder.encodeMove(x, y, 0, 0), this)) {
            setTile(x, y, '#');

            if (radius > 0) {
                int newPosition;
                for (int i = 0; i < Constants.NUMBER_OF_DIRECTIONS; i++) {
                    newPosition = getNextTile(Coder.encodePosition(x, y, i));
                    if (newPosition >= 0) {
                        bombing(Coder.decodePosX(newPosition), Coder.decodePosY(newPosition), radius - 1);
                    }
                }
            }
        }
    }

    /**
     * Resolves bomb explosions by converting marked tiles to empty tiles.
     *
     * @param x      The x-coordinate of the bomb.
     * @param y      The y-coordinate of the bomb.
     * @param radius The remaining explosion radius.
     */
    private void resolveBombing(int x, int y, int radius) {
        if (GameRules.insideMap(Coder.encodeMove(x, y, 0, 0), this)) {
            if (getTile(x, y) == '#') setTile(x, y, '-');

            if (radius > 0) {
                for (int i = 0; i < Constants.NUMBER_OF_DIRECTIONS; i++) {
                    int newPosition = getNextTile(Coder.encodePosition(x, y, i));
                    if (newPosition >= 0)
                        resolveBombing(Coder.decodePosX(newPosition), Coder.decodePosY(newPosition), radius - 1);
                }
            }
        }
    }

    /**
     * Handles the choice tile mechanic by switching tiles between players.
     *
     * @param move The encoded move containing player information.
     */
    public void choiceHandling(int move) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (getTile(i, j) == ((char) (Coder.decodePlayerId(move) + '0'))) {
                    setTile(i, j, ((char) (Coder.decodeSpecial(move) + '0')));
                } else if (getTile(i, j) == ((char) (Coder.decodeSpecial(move) + '0'))) {
                    setTile(i, j, ((char) (Coder.decodePlayerId(move) + '0')));
                }
            }
        }
    }

    /**
     * Handles the inversion tile mechanic by rotating player ownership.
     *
     * @param attributes The associated game attributes.
     */
    private void inversionHandling(GameAttributes attributes) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (GameRules.isPlayer(getTile(i, j))) {
                    setTile(i, j, (char) (((getTile(i, j) - '0') % attributes.getNumberPlayers() + 1) + '0'));
                }
            }
        }
    }


    /**
     * Helper method to compare a tile at a position with a specified character.
     *
     * @param c               The character to compare with.
     * @param currentPosition The encoded position to check.
     * @return true if the tile matches the character, false otherwise.
     */
    private boolean helpCompare(char c, int currentPosition) {
        return (getTile(Coder.decodePosX(currentPosition), Coder.decodePosY(currentPosition)) == c);
    }

}
