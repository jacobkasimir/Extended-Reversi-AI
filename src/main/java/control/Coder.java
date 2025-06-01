package control;

/**
 * Contains the encoder and decoder for moves, coordinates, players and positions
 */
public class Coder {
    /**
     * Encodes the information about x and y dimension of a certain coordinate into one value.
     *
     * @param xCoord The coordinate's x value.
     * @param yCoord The coordinate's y value.
     * @return Encoded value with information all attributes.
     */
    public static int encodeCoordinate(int xCoord, int yCoord) {
        return xCoord * 51 + yCoord;
    }

    /**
     * Encodes the information about x and y dimension, special value and playerId of a certain omve into one value.
     *
     * @param x        The move's x value.
     * @param y        The move's y value.
     * @param special  The move's special value.
     * @param playerId The playerId of the player who made the move.
     * @return Encoded value with information about all attributes.
     */
    public static int encodeMove(int x, int y, int special, int playerId) {
        return ((x * 51 + y) * 51 + special) * 51 + playerId;
    }

    /**
     * Encodes the information about x and y dimension and direction of a position into one value.
     *
     * @param x         The position's x value.
     * @param y         The position's y value.
     * @param direction The position's direction.
     * @return Encoded value with information about all attributes.
     */
    public static int encodePosition(int x, int y, int direction) {
        return (x * 51 + y) * 51 + direction;
    }

    /**
     * Decodes the information about the x value of a coordinate.
     *
     * @param value The encoded coordinate value.
     * @return The x value of the coordinate.
     */
    public static int decodeXCoords(int value) {
        return value / 51;
    }

    /**
     * Decodes the information about the y value of a coordinate.
     *
     * @param value The encoded coordinate value.
     * @return The y value of the coordinate.
     */
    public static int decodeYCoords(int value) {
        return value % 51;
    }

    /**
     * Decodes the information about the playerId of a move.
     *
     * @param value The encoded move value.
     * @return The playerId of the move.
     */
    public static int decodePlayerId(int value) {
        return value % 51;
    }

    /**
     * Decodes the information about the special value of a move.
     *
     * @param value The encoded move value.
     * @return The special value of the move.
     */
    public static int decodeSpecial(int value) {
        value /= 51;
        return value % 51;
    }

    /**
     * Decodes the information about the y value of a move.
     *
     * @param value The encoded move value.
     * @return The y value of the move.
     */
    public static int decodeY(int value) {
        value /= 51 * 51;
        return value % 51;
    }

    /**
     * Decodes the information about the x value of a move.
     *
     * @param value The encoded move value.
     * @return The x value of the move.
     */
    public static int decodeX(int value) {
        value /= 51 * 51 * 51;
        return value;
    }

    /**
     * Decodes the information about the direction value of a position.
     *
     * @param value The encoded position value.
     * @return The direction value of the position.
     */
    public static int decodePosDirec(int value) {
        return value % 51;
    }

    /**
     * Decodes the information about the y value of a position.
     *
     * @param value The encoded position value.
     * @return The y value of the position.
     */
    public static int decodePosY(int value) {
        value /= 51;
        return value % 51;
    }

    /**
     * Decodes the information about the x value of a position.
     *
     * @param value The encoded position value.
     * @return The x value of the position.
     */
    public static int decodePosX(int value) {
        value /= 51 * 51;
        return value % 51;
    }


    /**
     * Decodes a value to get the number of bombs of one player.
     *
     * @param value containing all the information of a single player.
     * @return number of bombs.
     */
    public static int decodeplayerBombs(int value) {
        value /= 51 * 51 * 51;
        return value;
    }

    /**
     * Decodes a value to get the number of override stones of one player.
     *
     * @param value containing all the information of a single player.
     * @return number of override stones.
     */
    public static int decodeplayerOverrides(int value) {
        value /= 51 * 51;
        return value % 51;
    }

    /**
     * Decodes a value to get the playerId of one player.
     *
     * @param value containing all the information of a single player.
     * @return playerId.
     */
    public static int decodePlayerPlayerId(int value) {
        value /= 51;
        return value % 51;
    }

    /**
     * Decodes an int-value to get 1 for in game and 0 for disqualified.
     *
     * @param value containing all the information of a single player.
     * @return 1 (in game) or 0 (disqualified)
     */
    public static int decodeplayerInGame(int value) {
        return value % 51;
    }

    /**
     * Encodes all information of one player to a single int-value.
     *
     * @param bombs     number of bombs of the player.
     * @param overrides number of override stones.
     * @param id        is the playerId.
     * @param inGame    1 if the player is still in the game, 0 if the player is disqualified.
     * @return The int-value containing all the information of a player.
     */
    public static int encodePlayer(int bombs, int overrides, int id, int inGame) {
        return ((bombs * 51 + overrides) * 51 + id) * 51 + inGame;
    }
}
