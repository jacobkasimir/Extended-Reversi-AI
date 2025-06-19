package control;

/**
 * The constants for the game
 **/
public class Constants {
    public static final int NUMBER_OF_DIRECTIONS = 8;
    public static final byte GROUP_NUMBER = 5;
    public static final double SAFETY_FOR_BRANCHING_FACTOR = 1.5;
    public static final int TIMELIMIT_SAFETY_OFFSET = 20; //in ms
    public static final int TIMELIMIT_TIMEOUT_OFFSET = 150; //in ms
    public static final double TIMEBUFFER = 0.82; //in %
    public static final int ASPIRATION_WINDOW = 1000;
}
