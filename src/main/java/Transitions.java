/*
 * The Transitions class stores all the data for the transitions.
 * It provides simple methods for using transitions.
 */

import java.util.Arrays;

public class Transitions{
    //private static Transitions instance;
    //private static boolean isInitialized = false;

    private Position[] transitionsArray;
    private final int width;
    private final int height;



    public Transitions(int width, int height){
        this.transitionsArray = new Position[width*height*8];
        this.width = width;
        this.height = height;
    }

    
    /** 
     * Returns the next tile with the corresponding direction
     * @param x The x-coordinate of the current tile
     * @param y The y-coordinate of the current tile
     * @param direc The direction from the current tile
     * @return Position The Position-object of the next tile 
     */
    public Position getNextTile(int x, int y, int direc) {
        if(direc < 9 && direc >= 0){
            if(getTransition(x, y, direc) == null){
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
                        return null;
                    }
                }
                if(x < 0 || x > width-1 || y < 0 || y > height-1) return null;
                else return new Position(x, y, direc);
            }
            else {
                Position res = getTransition(x, y, direc);   
                return new Position(res.x(), res.y(), (res.direc()+4) % 8);
            }
        }
        return new Position(0,0,0); //Error!
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
            transitionsArray[(width*8*yTile)+(xTile*8)+direcTile] = new Position(xNextTile, yNextTile, direcNextTile);
            transitionsArray[(width*8*yNextTile)+(xNextTile*8)+direcNextTile] = new Position(xTile, yTile, direcTile);
        }
    }

    private Position getTransition(int x, int y, int direction){
        return transitionsArray[(width*8*y)+(x*8)+direction];
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

    @Override
    public int hashCode(){
        return Arrays.deepHashCode(transitionsArray);
    }
    
}