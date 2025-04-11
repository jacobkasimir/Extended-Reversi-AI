public class Map {
    private int width;
    private int depth;
    private int players;

    private char[][] map;
    
    public Map(int width, int depth, int players){
        this.width = width;
        this.depth = depth;
        this.players = players;
        this.map = new char[width][depth];
    }

    
}
