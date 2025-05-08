
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * The MapController is the interface for other classes to interact with the map and transitions
 */
public class MapController{
    private final Map map;
    private final Transitions transitions;
    private Set<Coordinate> ourTiles;
    private Set<Coordinate> expansionTiles;
    private final int bombRadius;
    private final int width;
    private final int height;

    public MapController(String[] mapString, Transitions transitions, int width, int height, int bombRadius){
        this.map = new Map(width, height);
        this.transitions = transitions;

        ourTiles = new LinkedHashSet<>();

        this.height = height;
        this.width = width;
        this.bombRadius = bombRadius;

        initMap(mapString);

        initExpansionTiles();

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


    
    public Set<Coordinate> getOurTiles() {
        return ourTiles;
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
                setTile(i, j, newMap[i][j].charAt(0));
            }
        }
    }

    
    

    private void initExpansionTiles(){
        expansionTiles = new LinkedHashSet<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (getTile(i, j) == 'x') {
                    expansionTiles.add(new Coordinate(i, j));
                }
            }
        }
    }

    public Set<Coordinate> getExpansionTiles() {
        return expansionTiles;
    }

    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The y-coordinate of the required tile
     * @return char The value stored at the required tile
     */
    public char getTile(int x, int y) {
       return this.map.getTile(x, y);
    }

    
    /** 
     * @param x The x-coordinate of the required tile
     * @param y The y-coordinate of the required tile
     * @param c The value that is to be set
     */
    public void setTile(int x, int y, char c) {
        this.map.setTile(x, y, c);
    }

    
    /** 
     * @param p The Position-object of the current position
     * @return Position The next position after the transition
     */
    public Position getNextTile(Position p) {
        return transitions.getNextTile(p.x(), p.y(), p.direc());
    }

    
    /** 
     * @param xTile The x-coordinate of the first position
     * @param yTile The y-coordinate of the first position
     * @param direcTile The direction for the first position
     * @param xNextTile The x-coordinate of the second position
     * @param yNextTile The y-coordinate of the second position
     * @param direcNextTile The direction for the second position
     */
    public void setTransition(int xTile, int yTile, int direcTile, int xNextTile, int yNextTile, int direcNextTile) {
        transitions.setTransition(xTile, yTile, direcTile, xNextTile, yNextTile, direcNextTile);
    }

    

    /** 
     * Updates the map for the given move
     * @param move The played move
     * @param state The associated GameAttributes
     */
    public void updateMap(Move move, GameAttributes state){
        if(state.isPhase1()){
            // removing expansion Tiles out of expansionTiles
            if (getTile(move.x(), move.y()) == 'x') {
                expansionTiles.remove(new Coordinate(move.x(), move.y()));
            }

            int[] directionCounts = new int[8]; //holds the number of tiles for every direction that are captured by the move
            boolean search;
            boolean isChoice = getTile(move.x(), move.y()) == 'c';    //Is the move placed on a choice tile?
            boolean isInversion = getTile(move.x(), move.y()) == 'i'; //Is the move placed on an inversion tile?
            int tilesPassed;    //holds the number of captured tiles for a specific direction
            Position currentPosition; 

            for (int i = 0; i < 8; i++) { //search in every direction
                currentPosition = new Position(move.x(), move.y(), i);
                search = true;
                tilesPassed = 0;
                while(search){  //counting the number of captured tiles in a specific direction (i)
                    currentPosition = getNextTile(currentPosition); //updates the currentPosition for direction i
                    if(currentPosition == null) break;  //checks if nextTile is out of map
                    if(String.valueOf(move.player()).equals(String.valueOf(this.getTile(currentPosition.x(), currentPosition.y())))){
                        //case that the currentPosition is occupied by a tile owned by the player
                        if(tilesPassed > 0) {
                            if (currentPosition.x() != move.x() || currentPosition.y() != move.y()){ //check that found tile is not the starting tile
                                directionCounts[i] = tilesPassed;
                            }
                            search = false;
                        }
                        else search = false;
                    } 
                    //termination conditions for a direction
                    else if (helpCompare('0', currentPosition)) search = false;
                    else if (helpCompare('c', currentPosition)) search = false;
                    else if (helpCompare('i', currentPosition)) search = false;
                    else if (helpCompare('b', currentPosition)) search = false;
                    else if (helpCompare('-', currentPosition)) search = false;
                    else if (currentPosition.x() == move.x() && currentPosition.y() == move.y()) search = false;
                    else {
                        tilesPassed++;
                    }
                }
            }

            // if (getTile(move.getX(), move.getY()) == 'x') {
            //     setTile(move.getX(), move.getY(), (char) ('0' + move.getPlayer()));
            // }

            //updating captured tiles 
            for (int i = 0; i < 8; i++) {
                currentPosition = new Position(move.x(), move.y(), i);
                for (int j = 0; j < directionCounts[i]+1; j++) {
                    //if(getTile(currentPosition.x(), currentPosition.y()) != 'x'){
                        //updating ourTiles and expansionTiles
                        if (getTile(currentPosition.x(), currentPosition.y()) == 'x') {
                            expansionTiles.remove(new Coordinate(currentPosition.x(), currentPosition.y()));
                        }
                        if(move.player() == state.getOurPlayerId()){
                            ourTiles.add(new Coordinate(currentPosition.x(), currentPosition.y()));
                        }
                        setTile(currentPosition.x(), currentPosition.y(), (char)(move.player() + '0'));
                    //}
                    currentPosition = getNextTile(currentPosition);
                }
            }
            if(move.player() == state.getOurPlayerId()){
                ourTiles.add(new Coordinate(move.x(), move.y()));
            }
            

            if(isChoice) {
                choiceHandling(move);  //update if choice tile
                resetOurTiles(state.getOurPlayerId());
            } else if(isInversion) {
                inversionHandling(state);   //update if inversion tile
                resetOurTiles(state.getOurPlayerId());
            } else if(move.player() != state.getOurPlayerId()){
                List<Coordinate> toRemove = new ArrayList<>();
                for(Coordinate c : ourTiles){
                    if(map.getTile(c.x(), c.y()) != state.getOurPlayerId() +'0'){
                        toRemove.add(c);
                    }
                }
                ourTiles.removeAll(toRemove);
            }
        }
        else {
            bombing(move.x(), move.y(), bombRadius);  //handles bombing (phase 2)
        }
    }

    //handles bombing
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

    //handles choice tiles (switching the tiles)
    public void choiceHandling(Move move){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(getTile(i, j) == ((char)(move.player() + '0'))) setTile(i, j, ((char)(move.special() + '0')));
                else if(getTile(i, j) == ((char)(move.special() + '0'))) setTile(i, j, ((char)(move.player() + '0')));
            }
        }
    }

    //handles inversion tiles (switching the tiles)
    private void inversionHandling(GameAttributes state){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(isPlayer(getTile(i, j))) setTile(i, j, (char)(((getTile(i, j)-'0') % state.getNumberPlayers()+1)+'0'));
            }
        }
    }

    
    /** 
     * resets the ArrayList that holds alll the Positions of our occupied tiles
     * @param ourPlayerId The Id we are given
     */
    //resets ourTiles
    public void resetOurTiles(int ourPlayerId){
        ourTiles.clear();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(map.getTile(i, j) == ourPlayerId + '0'){
                    ourTiles.add(new Coordinate(i, j));
                }
            }
        }
    }

    //checks if 'c' is a player
    private boolean isPlayer(char c){
        if(c == '0' || c == 'b' || c == '-' || c == 'i' || c == 'c' || c == 'x') return false;
        else return true;
    }

    //compares the char values
    private boolean helpCompare(char c, Position currentPosition){
        return (getTile(currentPosition.x(), currentPosition.y()) == c);
    }



    //for Testing
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof MapController)) return false;
        MapController other = (MapController) obj;

        return this.width == other.width && this.height == other.height && this.bombRadius == other.bombRadius &&
            this.map.equals(other.map) && transitions.equals(transitions);
    }

   
}