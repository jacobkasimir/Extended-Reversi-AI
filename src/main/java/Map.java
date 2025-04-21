/*
 * The Map class stores all the char values of the map.
 * It provides simple methods for accessing the map
 */
public class Map {
    private char[][] map;
    
    public Map(int width, int height){
        this.map = new char[width][height];
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

}
