
/**
 * The Map class provides a two-dimensional grid structure for storing character-based map data.
 * This class is designed for representing game maps or similar grid-based structures
 * where each position is represented by a single character.
 */
public class Map {
    /**
     * The underlying one-dimensional array that holds the map data.
     */
    private char[] map;
    private final int width;
    private final int height;
    
    /**
     * Constructs a new Map with the specified dimensions.
     * Initializes an empty map where all positions contain the default char value.
     *
     * @param width The width (x-axis) of the map
     * @param height The height (y-axis) of the map
     */
    public Map(int width, int height){
        this.width = width;
        this.height = height;
        this.map = new char[width*height];
    }
    
    public Map(Map other){
        this.width = other.getWidth();
        this.height = other.getHeight();
        this.map = new char[width*height];
        System.arraycopy(other.getMap(), 0, this.map, 0, other.getMap().length);
    }
    
    /**
     * Retrieves the character at the specified position in the map.
     *
     * @param x The x-coordinate (horizontal position)
     * @param y The y-coordinate (vertical position)
     * @return The character at the specified position
     */
    public char getTile(int x, int y) {
        return map[(x*height) +y];
    }

    /**
     * Places a character at the specified position in the map.
     *
     * @param x The x-coordinate (horizontal position)
     * @param y The y-coordinate (vertical position)
     * @param c The character to place at the specified position
     */
    public void setTile(int x, int y, char c) {
        map[(x*height) +y] = c;
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
                if(this.map[i] != other.getMap()[i]) return false;
            }
            return true;
        }
        return false;
    }


    /**
     * Returns the internal array representing the map.
     * Note: This provides direct access to the map data.
     *
     * @return The two-dimensional char array containing the map data
     */
    public char[] getMap() {
        return map;
    }

    /**
     * Gets the height of the map.
     *
     * @return The number of rows in the map (y-axis size)
     */
    public int getHeight(){
        return this.height;
    }

    /**
     * Gets the width of the map.
     *
     * @return The number of columns in the map (x-axis size)
     */
    public int getWidth(){
        return this.width;
    }
}