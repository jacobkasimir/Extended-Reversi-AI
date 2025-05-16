

/*
 * The MapController is the interface for other classes to interact with the map and transitions
 */
public class MapController{
    private final Map map;
    private final Transitions transitions;
    private IntLinkedList expansionTiles;
    private final int bombRadius;
    private final int width;
    private final int height;

    public MapController(String[] mapString, Transitions transitions, int width, int height, int bombRadius){
        this.map = new Map(width, height);
        this.transitions = transitions;

        this.height = height;
        this.width = width;
        this.bombRadius = bombRadius;

        initMap(mapString);
        initExpansionTiles();
    }

    public MapController(MapController obj){
        this.map = new Map(obj.getMap());
        this.transitions = obj.getTransitions();
        this.expansionTiles = new IntLinkedList();
        this.expansionTiles.addAll(obj.getExpansionTiles());
        this.bombRadius = obj.getBombRadius();
        this.width = obj.getWidth();
        this.height = obj.getHeight();
    }
    


    public Map getMap() {
        return map;
    }

    public Transitions getTransitions() {
        return transitions;
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
                setTile(i, j, newMap[i][j].charAt(0));
            }
        }
    }

    
    

    private void initExpansionTiles(){
        expansionTiles = new IntLinkedList();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (getTile(i, j) == 'x') {
                    expansionTiles.add(MoveVerifier.encodeCoordinate(i, j));
                }
            }
        }
    }

    public IntLinkedList getExpansionTiles() {
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
    public int getNextTile(int p) {
        return transitions.getNextTile(MoveVerifier.decodePosX(p), MoveVerifier.decodePosY(p), MoveVerifier.decodePosDirec(p));
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
    public void updateMap(int move, GameAttributes state){
        if(state.isPhase1()){
            // removing expansion Tiles out of expansionTiles
            if (getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'x') {
                expansionTiles.remove(MoveVerifier.encodeCoordinate(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)));
            }

            int[] directionCounts = new int[8]; //holds the number of tiles for every direction that are captured by the move
            boolean search;
            boolean isChoice = getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'c';    //Is the move placed on a choice tile?
            boolean isInversion = getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'i'; //Is the move placed on an inversion tile?
            int tilesPassed;    //holds the number of captured tiles for a specific direction
            int currentPosition; 

            for (int i = 0; i < 8; i++) { //search in every direction
                currentPosition = MoveVerifier.encodePosition(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move), i);
                search = true;
                tilesPassed = 0;
                while(search){  //counting the number of captured tiles in a specific direction (i)
                    currentPosition = getNextTile(currentPosition); //updates the currentPosition for direction i
                    if(currentPosition < 0) break;  //checks if nextTile is out of map
                    if(String.valueOf(MoveVerifier.decodePlayerId(move)).equals(String.valueOf(this.getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition))))){
                        //case that the currentPosition is occupied by a tile owned by the player
                        if(tilesPassed > 0) {
                            if (MoveVerifier.decodePosX(currentPosition) != MoveVerifier.decodeX(move) || MoveVerifier.decodePosY(currentPosition) != MoveVerifier.decodeY(move)){ //check that found tile is not the starting tile
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
                    else if (MoveVerifier.decodePosX(currentPosition) == MoveVerifier.decodeX(move) && MoveVerifier.decodePosY(currentPosition) == MoveVerifier.decodeY(move)) search = false;
                    else {
                        tilesPassed++;
                    }
                }
            }

            //updating captured tiles 
            for (int i = 0; i < 8; i++) {
                currentPosition = MoveVerifier.encodePosition(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move), i);
                for (int j = 0; j < directionCounts[i]+1; j++) {
                    if (getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)) == 'x') {
                        expansionTiles.remove(MoveVerifier.encodeCoordinate(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)));
                    }
                    //add tile to tiles of the player
                    state.getPlayer(MoveVerifier.decodePlayerId(move)).getTiles().add(MoveVerifier.encodeCoordinate(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)));
                    //remove tile from the opponents tiles
                    if(isPlayer(getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)))){
                        state.getPlayer((int)(getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)) - '0')).getTiles().remove(MoveVerifier.encodeCoordinate(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)));
                    }
                    //update the map
                    setTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition), (char)(MoveVerifier.decodePlayerId(move) + '0'));
                    currentPosition = getNextTile(currentPosition);
                }
            }
            state.getPlayer(MoveVerifier.decodePlayerId(move)).getTiles().add(MoveVerifier.encodeCoordinate(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)));
            

            if(isChoice) {
                choiceHandling(move);  //update if choice tile
                resetTiles(MoveVerifier.decodePlayerId(move), state);
                resetTiles(MoveVerifier.decodeSpecial(move), state);
            } else if(isInversion) {
                inversionHandling(state);   //update if inversion tile
                for (int player = 1; player < state.getNumberPlayers()+1; player++) {
                    resetTiles(player, state);
                }
            } 
        }
        else {
            //handles bombing (phase 2)
            bombing(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move), bombRadius);  
            resolveBombing(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move), bombRadius);
        }
    }

    //handles bombing
    private void bombing(int x, int y, int radius){
        if(GameRules.insideMap(MoveVerifier.encodeMove(x, y, 0, 0), this) && getTile(x, y) != '-'){
            setTile(x, y, '#');

            if(radius > 0){ 
                for (int i = 0; i < 8; i++) {
                    int newPosition = getNextTile(MoveVerifier.encodePosition(x, y, i));
                    if(newPosition >= 0) bombing(MoveVerifier.decodePosX(newPosition), MoveVerifier.decodePosY(newPosition), radius-1);
                }
            }
        }
    }

    //resolves '#' to '-'
    private void resolveBombing(int x, int y, int radius){
        if(GameRules.insideMap(MoveVerifier.encodeMove(x, y, 0, 0), this)){
            if(getTile(x, y) == '#') setTile(x, y, '-');

            if(radius > 0){ 
                for (int i = 0; i < 8; i++) {
                    int newPosition = getNextTile(MoveVerifier.encodePosition(x, y, i));
                    if(newPosition >= 0) resolveBombing(MoveVerifier.decodePosX(newPosition), MoveVerifier.decodePosY(newPosition), radius-1);
                }
            }
        }
    }

    //handles choice tiles (switching the tiles)
    public void choiceHandling(int move){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(getTile(i, j) == ((char)(MoveVerifier.decodePlayerId(move) + '0'))) setTile(i, j, ((char)(MoveVerifier.decodeSpecial(move) + '0')));
                else if(getTile(i, j) == ((char)(MoveVerifier.decodeSpecial(move) + '0'))) setTile(i, j, ((char)(MoveVerifier.decodePlayerId(move) + '0')));
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
     * @param playerId The Id we are given
     * @param attributes The gameAttributes
     */
    //resets ourTiles
    public void resetTiles(int playerId, GameAttributes attributes){
        attributes.getPlayer(playerId).getTiles().clear();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(map.getTile(i, j) == playerId + '0'){
                    attributes.getPlayer(playerId).getTiles().add(MoveVerifier.encodeCoordinate(i, j));
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
    private boolean helpCompare(char c, int currentPosition){
        return (getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)) == c);
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