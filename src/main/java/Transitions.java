public class Transitions{
    private int width;
    private int depth;
    private int players;

    private char[][][] transitions;
    
    public Transitions(int width, int depth, int players){
        this.width = width;
        this.depth = depth;
        this.players = players;
        this.transitions = new char[width][depth][8];
    }
}