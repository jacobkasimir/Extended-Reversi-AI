public class MapController{
    private Map map;
    private Transitions transitions;
    private int bombRadius;
    private int width;
    private int depth;

    public MapController(String[] mapString, String[] transitionsString, int width, int depth, int bombRadius){
        this.map = new Map(width, depth);
        this.transitions = new Transitions(width, depth);
        this.depth = depth;
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void refreshMap(Move move){
        
    }

    private void initMap(String[] mapString){
        //initialize map
        String[][] newMap = new String[mapString.length][depth]; //array that holds all map entries as Strings
        for (int i = 0; i < mapString.length; i++) {
            newMap[i] = mapString[i].split(" ");
        }

        for (int i = 0; i < newMap.length; i++) {
            for (int j = 0; j < newMap[i].length; j++) {
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
        }
    }
}