public class Heuristics {

    public static double amountOfOwnTilesOverall(MapController map, GameState state){

        double result = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y) == (state.getOurPlayerId() + '0')) {
                    result++;
                }
            }

        }
        return result/(map.getHeight()*map.getWidth());
    } 
}
