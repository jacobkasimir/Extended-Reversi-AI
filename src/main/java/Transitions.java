/*
 * The Transitions class stores all the data for the transitions.
 * It provides simple methods for using transitions.
 */

public class Transitions{
    //private static Transitions instance;
    //private static boolean isInitialized = false;

    private int[] transitionsArray;
    private final int width;
    private final int height;



    public Transitions(int width, int height){
        this.transitionsArray = new int[3*width*height*8];
        this.width = width;
        this.height = height;

        for (int i = 0; i < transitionsArray.length; i++) {
            transitionsArray[i] = -1;
        }
    }

    
    /** 
     * Returns the next tile with the corresponding direction
     * @param x The x-coordinate of the current tile
     * @param y The y-coordinate of the current tile
     * @param direc The direction from the current tile
     * @return Position The Position-object of the next tile 
     */
    public int getNextTile(int x, int y, int direc) {
        if(direc < 9 && direc >= 0){
            if(getTransition(x, y, direc) == 0){
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
     * Sets a new transition in the transitionsArray-matrix
     * @param xTile The x-coordinate of the first position
     * @param yTile The y-coordinate of the first position
     * @param direcTile The direction for the first position
     * @param xNextTile The x-coordinate of the second position
     * @param yNextTile The y-coordinate of the second position
     * @param direcNextTile The direction for the second position
     */
    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        if(direcTile < 8 && direcTile >= 0 && direcNextTile < 8 && direcNextTile >= 0){ 
            transitionsArray[(width*8*yTile)+(xTile*8)+direcTile] = MoveVerifier.encodePosition(xNextTile, yNextTile, direcNextTile);
            transitionsArray[(width*8*yNextTile)+(xNextTile*8)+direcNextTile] = MoveVerifier.encodePosition(xTile, yTile, direcTile);
        }
    }

    private int getTransition(int x, int y, int direction){
        return MoveVerifier.encodePosition(transitionsArray[(width*8*y)+(x*8)+direction], transitionsArray[(width*8*y)+(x*8)+direction+1], transitionsArray[(width*8*y)+(x*8)+direction+2]);
    }

    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj){
        return true;
    }
    
}