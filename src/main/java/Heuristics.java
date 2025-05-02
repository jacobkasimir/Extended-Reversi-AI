import java.util.ArrayList;

public class Heuristics {

    private final ArrayList<Coordinate> corners;

    public Heuristics(MapController map){
        corners = findCorners(map);
    }

    public ArrayList<Coordinate> findCorners(MapController map){
        
        ArrayList<Coordinate> result = new ArrayList<>();
        int borderCount = 0;
        Position currentPosition;
        Position nextTile;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                
                if(map.getTile(x, y) != '-'){

                    for (int i = 0; i < 11; i++) { //11 = 8 + 3 = directions + auch "naechste runde" checken
                        
                        currentPosition = new Position(x, y, i);
                        nextTile = map.getNextTile(currentPosition);

                        if (nextTile == null || map.getTile(nextTile.x(), nextTile.y()) == '-') {
                            borderCount++;
                        }else{
                            borderCount = 0;
                        }

                        if (borderCount >= 4) {
                            result.add(new Coordinate(currentPosition.x(), currentPosition.y()));
                            break;
                        }
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
