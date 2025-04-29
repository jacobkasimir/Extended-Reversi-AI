/*
 * The MapController is the interface for other classes to interact with the map and transitions
 */
public class MapController{
    private Map map;
    private Transitions transitions;
    private int bombRadius;
    private int width;
    private int height;

    public MapController(String[] mapString, String[] transitionsString, int width, int height, int bombRadius){
        this.map = new Map(width, height);
        this.transitions = new Transitions(width, height);
        this.height = height;
        this.width = width;
        this.bombRadius = bombRadius;

        initMap(mapString);
        initTransitions(transitionsString);
    }
    
    
    /** 
     * @return int The width (x-axis) of the map
     */
    public int getWidth() {
        return width;
    }

    
    /** 
     * @return int The height (y-axis) of the map
     */
    public int getHeight() {
        return height;
    }


    /** 
     * @return int The BombRadius of the map
     */
    public int getBombRadius() {
        return bombRadius;
    }

    
    
    /** 
     * Initializes the map by translating the message sent by the server
     * @param mapString The data sent by the server
     */
    private void initMap(String[] mapString){
        String[][] newMap = new String[width][height]; //array that holds all map entries as Strings
        for (int i = 0; i < height; i++) {
            String[] temp = mapString[i].split(" ");
            for(int j = 0; j < width; j++){
                newMap[j][i] = temp[j];
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.map.setTile(i, j, newMap[i][j].charAt(0));
            }
        }
    }

    
    /** 
     * Initializes the transitions by translating the message sent by the server
     * @param mapString The data sent by the server
     */
    private void initTransitions(String[] transitionsString){
        String[][] newTransition = new String[transitionsString.length][7]; //array that holds all transitions as Strings
        for (int i = 0; i < transitionsString.length; i++) {
            newTransition[i] = transitionsString[i].split(" ");
        }

        for (int i = 0; i < newTransition.length; i++) {
            this.transitions.setTransition(Integer.parseInt(newTransition[i][0]), Integer.parseInt(newTransition[i][1]), Integer.parseInt(newTransition[i][2]), 
            Integer.parseInt(newTransition[i][4]), Integer.parseInt(newTransition[i][5]), Integer.parseInt(newTransition[i][6]));

            this.transitions.setTransition(Integer.parseInt(newTransition[i][4]), Integer.parseInt(newTransition[i][5]), Integer.parseInt(newTransition[i][6]), 
            Integer.parseInt(newTransition[i][0]), Integer.parseInt(newTransition[i][1]), Integer.parseInt(newTransition[i][2]));
        }
    }

    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The y-coordinate of the required tile
     * @return char The value stored at the required tile
     */
    public char getTile(int x, int y) {
       return map.getTile(x, y);
    }

    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The y-coordinate of the required tile
     * @param c The value that is to be set
     */
    public void setTile(int x, int y, char c) {
        map.setTile(x, y, c);
    }

    
    
    /** 
     * @param p The Position-object of the current position
     * @return Position The next position after the transition
     */
    public Position getNextTile(Position p) {
        return transitions.getNextTile(p.x(), p.y(), p.direc());
    }

    
    /** 
     * @param p1 The first position for the transition
     * @param p2 The second position for the transition
     */
    public void setTransition(Position p1, Position p2) {
        transitions.setTransition(p1.x(), p1.y(), p1.direc(), p2.x(), p2.y(), p2.direc());
    }

    
    /** 
     * Updates the map for the given move
     * @param move The played move
     */
    public void updateMap(Move move, boolean isPhase1){
        if(isPhase1){
            
        }
        else {
            bombing(move.getX(), move.getY(), bombRadius);
        }
    }

    private void bombing(int x, int y, int radius){
        if(GameRules.insideMap(new Move(x, y, 0, 0), this)){
            setTile(x, y, '-');
        }

        if(radius > 0){
            for (int i = 0; i < 8; i++) {
                Position newPosition = getNextTile(new Position(x, y, i));
                if(newPosition != null) bombing(newPosition.x(), newPosition.y(), radius-1);
            }
        }
    }

    //for Testing
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MapController)) return false;
        MapController other = (MapController) obj;

        return this.width == other.width && this.height == other.height && this.bombRadius == other.bombRadius &&
            this.map.equals(other.map) && this.transitions.equals(other.transitions);
    }
}