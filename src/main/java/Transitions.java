import java.util.Arrays;

/**
 * Manages transitions between tiles in a grid-based system.
 * Each tile can have transitions in 8 different directions.
 * The transitions are stored in a linear array representing x, y, and direction coordinates.
 */
public class Transitions {
    private final int[] transitionsArray;
    private final int width;
    private final int height;

    /**
     * Creates a new transition grid with specified dimensions.
     *
     * @param width Width of the transition grid
     * @param height Height of the transition grid
     */
    public Transitions(int width, int height) {
        this.transitionsArray = new int[width*height*8];
        this.width = width;
        this.height = height;

        Arrays.fill(transitionsArray, -1);
    }

    /**
     * Calculates the next tile position based on the current position and direction.
     * Uses predefined transitions if available, otherwise computes adjacent position.
     * Directions: 0=N, 1=NE, 2=E, 3=SE, 4=S, 5=SW, 6=W, 7=NW
     *
     * @param x X-coordinate of the current tile
     * @param y Y-coordinate of current tile
     * @param direc Direction from the current tile (0-7)
     * @return Encoded position containing next coordinates and direction, -1 if invalid
     */
    public int getNextTile(int x, int y, int direc) {
        if(direc < 9 && direc >= 0){
            if(getTransition(x, y, direc) == -1){
                switch (direc) {
                    case 0 -> y--;
                    case 1 -> { 
                        x++;
                        y--;
                    }
                    case 2 -> x++; 
                    case 3 -> {
                        x++;
                        y++;
                    }
                    case 4 -> y++;
                    case 5 -> { 
                        x--;
                        y++;
                    }
                    case 6 -> x--; 
                    case 7 -> {
                        x--;
                        y--;
                    }
                    default -> {
                        return -1;
                    }
                }
                if(x < 0 || x > width-1 || y < 0 || y > height-1) return -1;
                else return MoveVerifier.encodePosition(x, y, direc);
            }
            else {
                int res = getTransition(x, y, direc);   
                return MoveVerifier.encodePosition(MoveVerifier.decodePosX(res), MoveVerifier.decodePosY(res), (MoveVerifier.decodePosDirec(res)+4) % 8);
            }
        }
        return -1; //Error!
    }

    /**
     * Creates a two-way transition between two tile positions.
     *
     * @param xTile First tile X-coordinate
     * @param yTile First tile Y-coordinate
     * @param direcTile Direction from first tile (0-7)
     * @param xNextTile Second tile X-coordinate
     * @param yNextTile Second tile Y-coordinate
     * @param direcNextTile Direction from second tile (0-7)
     */
    public void setTransition(int xTile, int yTile, int direcTile, 
                            int xNextTile, int yNextTile, int direcNextTile) {
        if(direcTile < 8 && direcTile >= 0 && direcNextTile < 8 && direcNextTile >= 0){ 
            transitionsArray[(width*8*yTile)+(xTile*8)+direcTile] = MoveVerifier.encodePosition(xNextTile, yNextTile, direcNextTile);
            transitionsArray[(width*8*yNextTile)+(xNextTile*8)+direcNextTile] = MoveVerifier.encodePosition(xTile, yTile, direcTile);
        }
    }

    /**
     * Retrieves transition value for given position and direction.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param direction Direction to check (0-7)
     * @return Encoded transition value or -1 if none exists
     */
    private int getTransition(int x, int y, int direction){
        return transitionsArray[(width*8*y)+(x*8)+direction];
    }

    /**
     * @return Width of the transition grid
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Height of the transition grid
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks equality with another object.
     * Note: Current implementation always returns true.
     *
     * @param obj Object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return true;
    }
}