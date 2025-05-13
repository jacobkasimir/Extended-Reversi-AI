
import java.util.Arrays;


/*
 * The Map class stores all the char values of the map.
 * It provides simple methods for accessing the map
 */
public class Map {
    private char[][] map;
    
    public Map(int width, int height){
        this.map = new char[width][height];
    }
    
    public Map(Map obj){
        this.map = obj.getMap().clone();
    }
    
    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The y-coordinate of the required tile
     * @return char The value stored at the required tile
     */
    public char getTile(int x, int y) {
        return map[x][y];
    }

    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The x-coordinate of the required tile
     * @param c The value that is to be set
     */
    public void setTile(int x, int y, char c) {
        map[x][y] = c;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Map)) return false;
        Map other = (Map) obj;

        if(this.map.length == other.map.length){
            for (int i = 0; i < this.map.length; i++) {
                if(this.map[i].length != other.map[i].length) return false;

                for (int j = 0; j < this.map[i].length; j++) {
                    if(this.map[i][j] != other.map[i][j]) return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Arrays.deepHashCode(map);
    }

    public char[][] getMap() {
        return map;
    }

}
