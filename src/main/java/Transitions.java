public class Transitions{
    private Position[][][] transitions;
    
    public Transitions(int width, int height){
        this.transitions = new Position[width][height][8];
    }

    public Position getNextTile(int x, int y, int direc) {
        if(direc < 9 && direc > 0) return transitions[x][y][(direc+4) % 8];
        return new Position(0,0,1); //Fehlerfall
    }

    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        if(direcTile < 9 && direcTile > 0 && direcNextTile < 9 && direcNextTile > 0){ 
            transitions[xTile][yTile][direcTile] = new Position(xNextTile, yNextTile, direcNextTile);
        }
    }
    
}