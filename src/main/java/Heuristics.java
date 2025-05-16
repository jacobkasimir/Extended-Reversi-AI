import java.util.ArrayList;

public class Heuristics {

    private final IntLinkedList corners;

    public Heuristics(MapController map){
        corners = findCorners(map);
    }

    //can be private but public for using in unit tests
    //TODO set to private
    public IntLinkedList findCorners(MapController map){
        
        IntLinkedList result = new IntLinkedList();
        int currentPosition1;
        int nextTile1;
        int currentPosition2;
        int nextTile2;
        boolean currentIsCorner;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                
                if(map.getTile(x, y) != '-'){

                    currentIsCorner = true;

                    for (int i = 0; i < 4; i++) {
                        
                        currentPosition1 = MoveVerifier.encodePosition(x, y, i);
                        nextTile1 = map.getNextTile(currentPosition1);
                        currentPosition2 = MoveVerifier.encodePosition(x, y, i+4);
                        nextTile2 = map.getNextTile(currentPosition2);

                        if (nextTile1 != 0 && map.getTile(MoveVerifier.decodePosX(nextTile1), MoveVerifier.decodePosY(nextTile1)) != '-') {
                            if (nextTile2 != 0 && map.getTile(MoveVerifier.decodePosX(nextTile2), MoveVerifier.decodePosY(nextTile2)) != '-') {
                                currentIsCorner = false;
                                break;
                            }
                        }
                        
                    }
                    if (currentIsCorner) {
                        result.add(MoveVerifier.encodeCoordinate(x,y));
                    } 
                }
            }
        }

        return result;
    }

    public double amountOfCornersCaptured(MapController map, byte playerId){
        
        double counter = 0;

        ListNode coordinate = corners.getHead();
        while (coordinate != null) {
            if (map.getTile(MoveVerifier.decodeXCoords(coordinate.getValue()), MoveVerifier.decodeYCoords(coordinate.getValue())) == (playerId + '0')) {
                counter++;
            }
        }

        if (corners.isEmpty()) {
            return 0;
        }else{
            return counter / corners.getSize();
        }
    }

    public static double amountOfOwnTilesOverall(MapController map, byte playerId){

        double own = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == (playerId + '0')) {
                    own++;
                }
            }

        }
        return own/(map.getHeight()*map.getWidth());
    } 

    public static double amountOfOwnTilesToOthers(MapController map, byte playerId){
        
        double own = 0;
        int other = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == (playerId + '0')) {
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

    public static int numberOfOverrides(GameAttributes state, byte playerId){
        return state.getPlayer(playerId).getNumberOverwrites();
    }

    public static int numberOfBombs(GameAttributes state, byte playerId){
        return state.getPlayer(playerId).getNumberBombs();
    }

    public int evaluate(GameState gameState){
        return (int)((amountOfOwnTilesOverall(gameState.mapController(), gameState.attributes().getOurPlayerId()) + 
        amountOfOwnTilesToOthers(gameState.mapController(), gameState.attributes().getOurPlayerId()) + 2*amountOfCornersCaptured(gameState.mapController(), gameState.attributes().getOurPlayerId())) * 100);
    }
}
