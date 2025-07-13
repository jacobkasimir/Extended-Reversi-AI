package control;

/**
 * The constants for the game
 **/
public class Constants {
    public static final int NUMBER_OF_DIRECTIONS = 8;
    public static final byte GROUP_NUMBER = 5;
    public static final double SAFETY_FOR_BRANCHING_FACTOR = 1.5;
    public static final int TIMELIMIT_TIMEOUT_OFFSET = 200; //in ms
    public static final double TIMEBUFFER = 0.9; //in %
    public static final int ASPIRATION_WINDOW = 2_000_000;
    public static final int BOMB = 20;
    public static final int OVERRIDE = 21;

    // weights of heuristics
    //phase 1
    public static final double WEIGHTCORNERS = 600;
    public static final double WEIGHTOWNTOOVERALLPHASE1 = 825;
    public static final double WEIGHTOWNTOOPPONENTSPHASE1 = 825;
    public static final double WEIGHTBOMBS = 200;
    public static final double WEIGHTOVERRIDES = 400;
    //phase 2
    public static final double WEIGHTOWNTOOVERALLPHASE2 = 650;
    public static final double WEIGHTOWNTOOPPONENTSPHASE2 = 550;
    public static final double WEIGHTDISTSTRONGEST1 = 650;
    public static final double WEIGHTDISTSTRONGEST2 = 400;
    public static final double WEIGHTPLAYERSINGAME = 450;
}
