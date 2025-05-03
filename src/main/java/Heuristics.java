import java.util.ArrayList;

public class Heuristics {

    private final ArrayList<Coordinate> corners;

    public Heuristics(MapController map){
        corners = findCorners(map);
    }

    public ArrayList<Coordinate> findCorners(MapController map){
        
        ArrayList<Coordinate> result = new ArrayList<>();
        Position currentPosition1;
        Position nextTile1;
        Position currentPosition2;
        Position nextTile2;
        boolean currentIsCorner;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                
                if(map.getTile(x, y) != '-'){

                    currentIsCorner = true;

                    for (int i = 0; i < 4; i++) {
                        
                        currentPosition1 = new Position(x, y, i);
                        nextTile1 = map.getNextTile(currentPosition1);
                        currentPosition2 = new Position(x, y, i+4);
                        nextTile2 = map.getNextTile(currentPosition2);

                        if (nextTile1 != null && map.getTile(nextTile1.x(), nextTile1.y()) != '-') {
                            if (nextTile2 != null && map.getTile(nextTile2.x(), nextTile2.y()) != '-') {
                                currentIsCorner = false;
                                break;
                            }
                        }
                        
                    }
                    if (currentIsCorner) {
                        result.add(new Coordinate(x, y));
                    } 
                }
            }
        }

        return result;
    }

    public double amountOfCornersCaptured(MapController map, GameState state){
        
        double counter = 0;

        for (Coordinate coordinate : corners) {
            if (map.getTile(coordinate.x(), coordinate.y()) == (state.getOurPlayerId() + '0')) {
                counter++;
            }
        }

        if (corners.isEmpty()) {
            return 0;
        }else{
            return counter / corners.size();
        }
    }

    public static double amountOfOwnTilesOverall(MapController map, GameState state){

        double own = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == (state.getOurPlayerId() + '0')) {
                    own++;
                }
            }

        }
        return own/(map.getHeight()*map.getWidth());
    } 

    public static double amountOfOwnTilesToOthers(MapController map, GameState state){
        
        double own = 0;
        int other = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == (state.getOurPlayerId() + '0')) {
                    own++;
                }else if (isPlayer(map.getTile(x, y))) {
                    other++;
                }
            }

        }

        return own/other;

    }

    private static boolean isPlayer(char c){
        return !(c == '-' || c == '0' || c == 'x' || c == 'i' || c == 'b' || c == 'c');
    }

}
