public class Heuristics {

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
