public class Map {
    private char[][] map;
    
    public Map(int width, int height){
        this.map = new char[width][height];
    }
    
    
    public char getTile(int x, int y) {
        return map[x][y];
    }

    public void setTile(int x, int y, char c) {
        map[x][y] = c;
    }

}
