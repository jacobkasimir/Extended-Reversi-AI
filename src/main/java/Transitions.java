public class Transitions{
    private Position[][][] transitions;
    
    public Transitions(int width, int height){
        this.transitions = new Position[width][height][8];
    }

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
        return new Position(0,0,1); //Fehlerfall
    }

    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        if(direcTile < 9 && direcTile > 0 && direcNextTile < 9 && direcNextTile > 0){ 
            transitions[xTile][yTile][direcTile] = new Position(xNextTile, yNextTile, direcNextTile);
        }
    }
    
}