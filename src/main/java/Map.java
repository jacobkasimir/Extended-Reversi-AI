import java.util.Arrays;

/**
 * The Map class provides a two-dimensional grid structure for storing character-based map data.
 * This class is designed for representing game maps or similar grid-based structures
 * where each position is represented by a single character.
 */
public class Map {
    /**
     * The underlying two-dimensional array that holds the map data.
     * First dimension represents width (x-axis), second dimension represents height (y-axis).
     */
    private char[][] map;
    
    /**
     * Constructs a new Map with the specified dimensions.
     * Initializes an empty map where all positions contain the default char value.
     *
     * @param width The width (x-axis) of the map
     * @param height The height (y-axis) of the map
     */
    public Map(int width, int height){
        this.map = new char[width][height];
    }
    
    /**
     * Creates a deep copy of an existing Map.
     * All character values from the source map are copied to the new instance.
     *
     * @param obj The source Map to copy from
     */
    public Map(Map obj){
        this.map = new char[obj.getWidth()][obj.getHeight()];
        for(int i=0; i<obj.getWidth();i++){
            for(int y=0;y<obj.getHeight();y++){
                this.map[i][y] = obj.getTile(i, y);
            }
        }
    }
    
    /**
     * Retrieves the character at the specified position in the map.
     *
     * @param x The x-coordinate (horizontal position)
     * @param y The y-coordinate (vertical position)
     * @return The character at the specified position
     */
    public char getTile(int x, int y) {
        return map[x][y];
    }

    /**
     * Places a character at the specified position in the map.
     *
     * @param x The x-coordinate (horizontal position)
     * @param y The y-coordinate (vertical position)
     * @param c The character to place at the specified position
     */
    public void setTile(int x, int y, char c) {
        map[x][y] = c;
    }

    /**
     * Checks if this Map is equal to another object.
     * Two Maps are considered equal if they have identical dimensions and content.
     *
     * @param obj The object to compare with this Map
     * @return true if the objects are equal, false otherwise
     */
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

    /**
     * Generates a hash code for this Map.
     * The hash code is based on the content of the map array.
     *
     * @return A hash code value for this Map
     */
    @Override
    public int hashCode(){
        return Arrays.deepHashCode(map);
    }

    /**
     * Returns the internal array representing the map.
     * Note: This provides direct access to the map data.
     *
     * @return The two-dimensional char array containing the map data
     */
    public char[][] getMap() {
        return map;
    }

    /**
     * Gets the height of the map.
     *
     * @return The number of rows in the map (y-axis size)
     */
    public int getHeight(){
        return map[0].length;
    }

    /**
     * Gets the width of the map.
     *
     * @return The number of columns in the map (x-axis size)
     */
    public int getWidth(){
        return map.length;
    }
}