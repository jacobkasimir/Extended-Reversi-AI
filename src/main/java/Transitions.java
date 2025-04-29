/*
 * The Transitions class stores all the data for the transitions.
 * It provides simple methods for using transitions.
 */

import java.util.Arrays;

public class Transitions{
    private Position[][][] transitions;
    
    public Transitions(int width, int height){
        this.transitions = new Position[width][height][8];
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
            if(transitions[x][y][direc] == null){
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
                if(x < 0 || x > transitions.length-1 || y < 0 || y > transitions[1].length-1) return null;
                else return new Position(x, y, direc);
            }
            else {
                Position res = transitions[x][y][direc];   
                return new Position(res.x(), res.y(), (res.direc()+4) % 8);
            }
        }
        return new Position(0,0,0); //Error!
    }

    
    /** 
     * Sets a new transition in the transitions-matrix
     * @param xTile The x-coordinate of the first position
     * @param yTile The y-coordinate of the first position
     * @param direcTile The direction for the first position
     * @param xNextTile The x-coordinate of the second position
     * @param yNextTile The y-coordinate of the second position
     * @param direcNextTile The direction for the second position
     */
    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        if(direcTile < 9 && direcTile >= 0 && direcNextTile < 9 && direcNextTile >= 0){ 
            transitions[xTile][yTile][direcTile] = new Position(xNextTile, yNextTile, direcNextTile);
        }
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Transitions)) return false;
        Transitions other = (Transitions) obj;
        if(this.transitions.length == other.transitions.length){
            for (int i = 0; i < this.transitions.length; i++) {
                if(this.transitions[i].length != other.transitions[i].length) return false;

                for (int j = 0; j < this.transitions[i].length; j++) {
                    if(this.transitions[i][j].length != other.transitions[i][j].length) return false;
                    
                    for (int k = 0; k < this.transitions[i][j].length; k++) {
                        if(this.transitions[i][j][k] != null && other.transitions[i][j][k] == null) return false;
                        if(this.transitions[i][j][k] == null && other.transitions[i][j][k] != null) return false;
                        else if(this.transitions[i][j][k] != null && other.transitions[i][j][k] != null &&
                                !this.transitions[i][j][k].equals(other.transitions[i][j][k])) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Arrays.deepHashCode(transitions);
    }
    
}