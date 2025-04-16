public class Map {
    private char[][] map;
    
    public Map(int width, int depth){
        this.map = new char[width][depth];
    }

    
    public char getTile(int x, int y) {
        return map[x][y];
    }

    public void setTile(int x, int y, char c) {
        map[x][y] = c;
    }

}
