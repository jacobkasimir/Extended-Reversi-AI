/*
 * The Transitions class stores all the data for the transitions.
 * It provides simple methods for using transitions.
 */

import java.util.Arrays;

public class Transitions{
    private static Transitions instance;
    private static boolean isInitialized = false;

    private Position[] transitionsArray;
    private int width;
    private int height;



    private Transitions(){

    }

    public static Transitions getInstance(){
        if(instance == null){
            instance = new Transitions();
        } 
        return instance;
    }

    public void init(int width, int height){
        if(!isInitialized){
            isInitialized = true;
            this.transitionsArray = new Position[width*height*8];
            this.width = width;
            this.height = height;
        }
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
                    case 0: 
                        y--;
                        break;
                    case 1: 
                        x++;
                        y--;
                        break;
                    case 2: 
                        x++;
                        break;
                    case 3: 
                        x++;
                        y++;
                        break;
                    case 4: 
                        y++;
                        break;
                    case 5: 
                        x--;
                        y++;
                        break;
                    case 6: 
                        x--;
                        break;
                    case 7: 
                        x--;
                        y--;
                        break;
                    default:
                        return null;
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
        if(direcTile < 9 && direcTile >= 0 && direcNextTile < 9 && direcNextTile >= 0){ 
            transitionsArray[(width*8*(yTile+1))+((xTile+1)*8)+direcTile] = new Position(xNextTile, yNextTile, direcNextTile);
        }
    }

    private Position getTransition(int x, int y, int direction){
        return transitionsArray[(width*9*(y+1))+((x+1)*8)+direction];
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Transitions)) return false;
        Transitions other = (Transitions) obj;
        if(this.transitionsArray.length == other.transitionsArray.length){
            for (int i = 0; i < this.transitionsArray.length; i++) {
                if(!this.transitionsArray[i].equals(other.transitionsArray[i])){
                    return false;
                } 
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Arrays.deepHashCode(transitionsArray);
    }
    
}