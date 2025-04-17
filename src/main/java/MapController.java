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
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBombRadius() {
        return bombRadius;
    }

    public void setBombRadius(int bombRadius) {
        
        this.bombRadius = bombRadius;
    }


    public void refreshMap(Move move){
        // Noch nicht fertig
    }
    
    private void initMap(String[] mapString){
        //initialize map
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

    private void initTransitions(String[] transitionsString){
        //initialize transitions
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


    public char getTile(int x, int y) {
       return map.getTile(x, y);
    }

    public void setTile(int x, int y, char c) {
        map.setTile(x, y, c);
    }


    public Position getNextTile(Position p) {
        return transitions.getNextTile(p.x(), p.y(), p.direc());
    }

    public void setTransition(Position p1, Position p2) {
        transitions.setTransition(p1.x(), p1.y(), p1.direc(), p2.x(), p2.y(), p2.direc());
    }
}