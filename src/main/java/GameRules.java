import javax.swing.text.Position;

public class GameRules {

    public static boolean isNotHole(Move move,MapController map ){
        return map.getMap.getTile(move.x, move.y) != 'h';
    }

    public static boolean insideMap(Move move, MapController map){
        return map.getMap().getWidth() > move.x  && move.x >= 0 && map.getMap().getDepth() > move.y >= 0;
    }

    public static boolean occupied(Move move, MapController map){
        return !(map.getMap().getTile(move.x, move.y) == '0' || map.getMap().getTile(move.x, move.y) == 'b' || map.getMap().getTile(move.x, move.y) == 'i' || map.getMap().getTile(move.x, move.y) == 'c');
    }

    public static boolean hasBomb(GameStatus state, Move move){
        return state.getPlayer(move.player).getNumberBombs() > 0;
    }

    public static boolean hasOverwrite(GameStatus state, Move move){
        return state.getPlayer(move.player).getNumberOverwrites() > 0;
    }

    public static boolean validMove(Move move, GameStatus state){

        if(move.player < 1 && move.player > state.getNumberPlayers()) return false;
        return state.getPlayer(move.player).isInGame();
    }

    public static boolean isExpansion(MapController map, Move move){
        return map.getMap().getTile(move.x, move.y) == 'x';
    }

    public static boolean checkSpecial(GameStatus state, Move move, MapController map){
        switch(map.getMap().getTile(move.x, move.y)){
            case 'b': {
                return move.special == 20 || move.special == 21;
            }break;
            case 'c': {
                return move.special >= 1 && move.special <= state.getNumberPlayers();
            }break;
            default: return move.special == 0;
        }
    }

    public static boolean capturing(Move move, MapController map){
        int direction;
        int tilesPassed;
        boolean search;
        Position currentPosition;
        for(int i = 0; i < 7; i++) {
            currentPosition = new Position(move.x, move.y, i);
            search = true;
            tilesPassed = 0;
            while(search){
                currentPosition = getNextTile(currentPosition);
                if (currentPosition == null) return false;
                if(map.getMap().getTile(currentPosition.x, currentPosition.y) == (char)move.player){
                    if(tilesPassed > 0) {
                        if (currentPosition.x != move.x || currentPosition.y != move.y) return true;
                        search = false;
                    }
                    else search = false;
                } else if (helpCompare('0', currentPosition))search = false;
                 else if (helpCompare('c', currentPosition)) search = false;
                else if (helpCompare('i', currentPosition)) search = false;
                else if (helpCompare('b', currentPosition)) search = false;
                else {
                    tilesPassed++;
                }
            }
        }
        return false;
    }

    private boolean helpCompare(char c, Position currentPosition){
        return (map.getMap().getTile(currentPosition.x, currentPosition.y) == c);
    }

}


