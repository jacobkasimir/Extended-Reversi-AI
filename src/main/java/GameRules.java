
public class GameRules {

    public static boolean isNotHole(Move move, MapController map ){
        return map.getTile(move.getX(), move.getY()) != '-';
    }

    public static boolean insideMap(Move move, MapController map){
        return map.getWidth() > move.getX() && move.getX() >= 0 && map.getHeight() > move.getY() && move.getY() >= 0;
    }

    public static boolean occupied(Move move, MapController map){
        return !(map.getTile(move.getX(), move.getY()) == '0' || map.getTile(move.getX(), move.getY()) == 'b' || 
        map.getTile(move.getX(), move.getY()) == 'i' || map.getTile(move.getX(), move.getY()) == 'c');
    }

    public static boolean hasBomb(GameState state, Move move){
        return state.getPlayer(move.getPlayer()).getNumberBombs() > 0;
    }

    public static boolean hasOverwrite(GameState state, Move move){
        return state.getPlayer(move.getPlayer()).getNumberOverwrites() > 0;
    }

    public static boolean validMove(Move move, GameState state){
        if(move.getPlayer() < 1 || move.getPlayer() > state.getNumberPlayers()) return false;
        return state.getPlayer(move.getPlayer()).isInGame();
    }

    public static boolean isExpansion(MapController map, Move move){
        return map.getTile(move.getX(), move.getY()) == 'x';
    }

    public static boolean checkSpecial(GameState state, Move move, MapController map){
        if(!state.isPhase1()) return move.getSpecial() == 0;
        switch(map.getTile(move.getX(), move.getY())){
            case 'b': {
                return move.getSpecial() == 20 || move.getSpecial() == 21;
            }
            case 'c': {
                return move.getSpecial() >= 1 && move.getSpecial() <= state.getNumberPlayers();
            }
            default: return move.getSpecial() == 0;
        }
    }

    public static boolean capturing(Move move, MapController map){
        int direction;
        int tilesPassed;
        boolean search;
        Position currentPosition;
        for(int i = 0; i < 8; i++) {
            currentPosition = new Position(move.getX(), move.getY(), i);
            search = true;
            tilesPassed = 0;
            while(search){
                currentPosition = map.getNextTile(currentPosition);
                if (currentPosition == null) break;
                if(String.valueOf(move.getPlayer()).equals(String.valueOf(map.getTile(currentPosition.x(), currentPosition.y())))){
                    if(tilesPassed > 0) {
                        if (currentPosition.x() != move.getX() || currentPosition.y() != move.getY()) return true;
                        search = false;
                    }
                    else search = false;
                } 
                else if (helpCompare('0', currentPosition, map)) search = false;
                else if (helpCompare('c', currentPosition, map)) search = false;
                else if (helpCompare('i', currentPosition, map)) search = false;
                else if (helpCompare('b', currentPosition, map)) search = false;
                else if (helpCompare('-', currentPosition, map)) search = false;
                else {
                    tilesPassed++;
                }
            }
        }
        return false;
    }

    private static boolean helpCompare(char c, Position currentPosition, MapController map){
        return (map.getTile(currentPosition.x(), currentPosition.y()) == c);
    }

}


